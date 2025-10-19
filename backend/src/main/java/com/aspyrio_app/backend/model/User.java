package com.aspyrio_app.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable=false, length=100)
    private String password;

    @Column(nullable=false, unique=true, length=100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, columnDefinition = "user_role")
    private Role role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
