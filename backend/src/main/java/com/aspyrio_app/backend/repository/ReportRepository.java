package com.aspyrio_app.backend.repository;

import com.aspyrio_app.backend.model.FitnessCenter;
import com.aspyrio_app.backend.model.Report;
import com.aspyrio_app.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByUser(User user);
    List<Report> findByFitnessCenter(FitnessCenter fitnessCenter);
    List<Report> findByUserAndFitnessCenter(User user, FitnessCenter fitnessCenter);
}


