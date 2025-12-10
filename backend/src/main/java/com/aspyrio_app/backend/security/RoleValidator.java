package com.aspyrio_app.backend.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RoleValidator {
    public void ensureHasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            System.err.println("RoleValidator: User is not authenticated");
            System.err.println("Current authentication: " + auth);
            throw new AccessDeniedException("User is not authenticated");
        }
        
        System.err.println("RoleValidator: Checking role " + role + " for user " + auth.getName());
        System.err.println("User authorities: " + auth.getAuthorities());
        
        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority(role))) {
            System.err.println("RoleValidator: User does not have required role: " + role);
            throw new AccessDeniedException("User does not have required role: " + role);
        }
    }
}
