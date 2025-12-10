package com.aspyrio_app.backend.repository;

import com.aspyrio_app.backend.model.FitnessCenter;
import com.aspyrio_app.backend.model.Studio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudioRepository extends JpaRepository<Studio, Long> {
    List<Studio> findByFitnessCenter(FitnessCenter fitnessCenter);
}


