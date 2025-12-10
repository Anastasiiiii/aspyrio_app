package com.aspyrio_app.backend.service.pass;

import com.aspyrio_app.backend.dto.PassResponse;
import com.aspyrio_app.backend.model.Pass;
import com.aspyrio_app.backend.model.User;
import com.aspyrio_app.backend.repository.PassRepository;
import com.aspyrio_app.backend.repository.UserRepository;
import com.aspyrio_app.backend.security.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GeneratePassService {
    private final PassRepository passRepository;
    private final UserRepository userRepository;
    private final AuthValidator authValidator;
    
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String SECRET_KEY = "aspyrio-pass-secret-key";

    @Transactional
    public PassResponse generatePass() {
        authValidator.ensureAuthenticated();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(30);
        Pass savedPass = null;
        int maxRetries = 5;
        for (int i = 0; i < maxRetries; i++) {
            try {
                String token = generateToken(currentUser.getId(), LocalDateTime.now());

                if (passRepository.findByToken(token).isPresent()) {
                    Thread.sleep(10);
                    continue;
                }

                Pass pass = new Pass();
                pass.setUser(currentUser);
                pass.setToken(token);
                pass.setExpiresAt(expiresAt);
                pass.setUsed(false);

                savedPass = passRepository.save(pass);
                break;
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                if (i == maxRetries - 1) {
                    throw new RuntimeException("Failed to generate unique token after " + maxRetries + " attempts", e);
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while generating token", ie);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error generating pass", e);
            }
        }

        if (savedPass == null) {
            throw new RuntimeException("Failed to generate pass");
        }

        return mapToResponse(savedPass);
    }

    private String generateToken(Long userId, LocalDateTime timestamp) {
        try {
            String randomPart = UUID.randomUUID().toString().substring(0, 8);
            String data = userId + ":" + timestamp.toString() + ":" + randomPart;
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    SECRET_KEY.getBytes(StandardCharsets.UTF_8),
                    HMAC_ALGORITHM
            );
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String hmacBase64 = Base64.getEncoder().encodeToString(hmacBytes);
            return userId + ":" + timestamp.toString() + ":" + randomPart + ":" + hmacBase64;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return UUID.randomUUID().toString();
        }
    }

    private PassResponse mapToResponse(Pass pass) {
        PassResponse response = new PassResponse();
        response.setId(pass.getId());
        response.setUserId(pass.getUser().getId());
        response.setToken(pass.getToken());
        response.setExpiresAt(pass.getExpiresAt());
        response.setUsed(pass.getUsed());
        response.setCreatedAt(pass.getCreatedAt());
        response.setUpdatedAt(pass.getUpdatedAt());
        return response;
    }
}

