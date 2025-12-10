package com.aspyrio_app.backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String PROPERTY_SOURCE_NAME = "dotenv";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            Dotenv dotenv = null;
            String envFilePath = null;
            
            Path currentDirEnv = Paths.get(System.getProperty("user.dir"), ".env");
            if (currentDirEnv.toFile().exists()) {
                envFilePath = currentDirEnv.getParent().toString();
            }
            
            if (envFilePath == null) {
                Path backendEnv = Paths.get(System.getProperty("user.dir"), "backend", ".env");
                if (backendEnv.toFile().exists()) {
                    envFilePath = backendEnv.getParent().toString();
                }
            }
            
            if (envFilePath == null) {
                Path currentPath = Paths.get(System.getProperty("user.dir"));
                Path pomPath = findPomFile(currentPath);
                if (pomPath != null) {
                    Path envPath = pomPath.getParent().resolve(".env");
                    if (envPath.toFile().exists()) {
                        envFilePath = envPath.getParent().toString();
                    }
                }
            }
            
            if (envFilePath == null) {
                try {
                    String codeSourcePath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
                    if (codeSourcePath.contains("target/classes")) {
                        Path classPath = Paths.get(codeSourcePath);
                        Path backendEnv = classPath.resolve("../../.env").normalize();
                        if (backendEnv.toFile().exists()) {
                            envFilePath = backendEnv.getParent().toString();
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            
            if (envFilePath != null) {
                try {
                    dotenv = Dotenv.configure()
                            .directory(envFilePath)
                            .ignoreIfMissing()
                            .load();
                    System.out.println("Loading .env from: " + envFilePath);
                } catch (Exception e) {
                    System.out.println("Failed to load .env from " + envFilePath + ": " + e.getMessage());
                }
            }
            
            if (dotenv == null) {
                try {
                    dotenv = Dotenv.configure()
                            .ignoreIfMissing()
                            .load();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            Map<String, Object> envMap = new HashMap<>();
            if (dotenv != null) {
                dotenv.entries().forEach(entry -> {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (environment.getProperty(key) == null && value != null && !value.trim().isEmpty()) {
                        envMap.put(key, value);
                    }
                });
            }

            if (!envMap.isEmpty()) {
                MutablePropertySources propertySources = environment.getPropertySources();
                MapPropertySource dotenvPropertySource = new MapPropertySource(PROPERTY_SOURCE_NAME, envMap);
                propertySources.addAfter("systemEnvironment", dotenvPropertySource);
                System.out.println("✓ Loaded " + envMap.size() + " environment variables from .env file");
            } else {
                System.out.println("Note: .env file not found or empty. Using system environment variables.");
            }
        } catch (Exception e) {
            System.out.println("Note: .env file not loaded: " + e.getMessage() + ". Using system environment variables.");
        }
    }
    
    private Path findPomFile(Path startPath) {
        Path current = startPath;
        int maxDepth = 5;
        int depth = 0;
        
        while (current != null && depth < maxDepth) {
            Path pomFile = current.resolve("pom.xml");
            if (pomFile.toFile().exists()) {
                return pomFile;
            }
            current = current.getParent();
            depth++;
        }
        return null;
    }
}

