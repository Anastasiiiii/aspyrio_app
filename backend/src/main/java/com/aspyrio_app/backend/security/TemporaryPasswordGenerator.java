package com.aspyrio_app.backend.security;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TemporaryPasswordGenerator {
    public String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
