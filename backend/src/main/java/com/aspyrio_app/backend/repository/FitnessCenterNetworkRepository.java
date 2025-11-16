package com.aspyrio_app.backend.repository;

import com.aspyrio_app.backend.model.FitnessCenterNetwork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FitnessCenterNetworkRepository extends JpaRepository<FitnessCenterNetwork, Long> {
    Optional<FitnessCenterNetwork> findByName(String name);
}
