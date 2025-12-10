package com.aspyrio_app.backend.repository;

import com.aspyrio_app.backend.model.FitnessCenter;
import com.aspyrio_app.backend.model.FitnessCenterNetwork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FitnessCenterRepository extends JpaRepository<FitnessCenter, Long> {
    Optional<FitnessCenter> findByName(String name);
    Optional<FitnessCenter> findByNetwork(FitnessCenterNetwork network);
    List<FitnessCenter> findAllByNetwork(FitnessCenterNetwork network);
}
