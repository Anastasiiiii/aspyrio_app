package com.aspyrio_app.backend.repository;

import com.aspyrio_app.backend.model.Pass;
import com.aspyrio_app.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PassRepository extends JpaRepository<Pass, UUID> {
    Optional<Pass> findByToken(String token);
    
    List<Pass> findByUser(User user);
    
    @Query("SELECT p FROM Pass p WHERE p.user = :user AND p.expiresAt > :now AND p.used = false ORDER BY p.createdAt DESC")
    List<Pass> findActivePassesByUser(@Param("user") User user, @Param("now") LocalDateTime now);
    
    @Query("SELECT p FROM Pass p WHERE p.token = :token AND p.expiresAt > :now AND p.used = false")
    Optional<Pass> findValidPassByToken(@Param("token") String token, @Param("now") LocalDateTime now);
}


