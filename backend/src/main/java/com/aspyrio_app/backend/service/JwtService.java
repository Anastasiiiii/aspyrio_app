package com.aspyrio_app.backend.service;

import com.aspyrio_app.backend.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void initKeys() throws Exception {
        try (InputStream privateIS = new ClassPathResource("keys/ec_private_pkcs8.pem").getInputStream()) {
            String privateKeyPEM = new String(privateIS.readAllBytes())
                    .replaceAll("-----BEGIN (.*)-----", "")
                    .replaceAll("-----END (.*)-----", "")
                    .replaceAll("[^A-Za-z0-9+/=]", ""); // залишаємо тільки Base64 символи

            byte[] decodedPrivate = Base64.getDecoder().decode(privateKeyPEM);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedPrivate);
            KeyFactory kf = KeyFactory.getInstance("EC");
            this.privateKey = kf.generatePrivate(spec);
        }

        try (InputStream publicIS = new ClassPathResource("keys/ec_public_x509.pem").getInputStream()) {
            String publicKeyPEM = new String(publicIS.readAllBytes())
                    .replaceAll("-----BEGIN (.*)-----", "")
                    .replaceAll("-----END (.*)-----", "")
                    .replaceAll("[^A-Za-z0-9+/=]", ""); // залишаємо тільки Base64 символи

            byte[] decodedPublic = Base64.getDecoder().decode(publicKeyPEM);
            X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(decodedPublic);
            KeyFactory kf = KeyFactory.getInstance("EC");
            this.publicKey = kf.generatePublic(pubSpec);
        }
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(privateKey, SignatureAlgorithm.ES256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
