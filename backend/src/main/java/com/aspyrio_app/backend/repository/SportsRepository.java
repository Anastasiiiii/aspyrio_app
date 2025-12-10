package com.aspyrio_app.backend.repository;

import com.aspyrio_app.backend.model.Sports;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SportsRepository extends JpaRepository<Sports, Long> {
    Optional<Sports> findByName(String name);
}


