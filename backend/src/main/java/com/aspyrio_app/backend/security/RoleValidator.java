package com.aspyrio_app.backend.security;

import com.aspyrio_app.backend.model.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RoleValidator {
    public void ensureHasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority(role))) {
            throw new RuntimeException("User does not have required role: " + role);
        }
    }
}
