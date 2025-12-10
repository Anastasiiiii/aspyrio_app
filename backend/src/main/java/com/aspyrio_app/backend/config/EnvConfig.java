package com.aspyrio_app.backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfig {
    public static void loadEnvFile() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .directory("./")
                    .load();
            
            dotenv.entries().forEach(entry -> {
                String key = entry.getKey();
                String value = entry.getValue();
                if (System.getenv(key) == null) {
                    System.setProperty(key, value);
                }
            });
        } catch (Exception e) {
            System.out.println("Note: .env file not found or has errors. Using system environment variables.");
        }
    }
    
    @Bean
    public Dotenv dotenv() {
        return Dotenv.configure()
                .ignoreIfMissing()
                .directory("./")
                .load();
    }
}
