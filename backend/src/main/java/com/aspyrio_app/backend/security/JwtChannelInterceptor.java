package com.aspyrio_app.backend.security;

import com.aspyrio_app.backend.repository.UserRepository;
import com.aspyrio_app.backend.service.auth.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {
    
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authToken = accessor.getFirstNativeHeader("Authorization");
            
            if (authToken == null || !authToken.startsWith("Bearer ")) {
                System.err.println("WebSocket: No valid Authorization header");
                return null;
            }
            
            String token = authToken.substring(7);
            try {
                String username = jwtService.extractUsername(token);
                var user = userRepository.findByUsername(username).orElse(null);
                
                if (user == null) {
                    System.err.println("WebSocket: User not found: " + username);
                    return null;
                }
                
                UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRole().name())
                        .build();
                
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                                userDetails, 
                                null, 
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                        );
                
                        accessor.setUser(authentication);
                        System.out.println("WebSocket: Authentication set for user: " + username);
                        System.out.println("WebSocket: User authorities: " + authentication.getAuthorities());
            } catch (Exception e) {
                System.err.println("WebSocket JWT validation failed: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        } else if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            System.err.println("WebSocket: No Authorization header in CONNECT message");
        }
        
        return message;
    }
}

