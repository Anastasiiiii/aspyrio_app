package com.aspyrio_app.backend.repository;

import com.aspyrio_app.backend.model.CoachProfile;
import com.aspyrio_app.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CoachProfileRepository extends JpaRepository<CoachProfile, Long> {
    Optional<CoachProfile> findByUser(User user);

    @Query("SELECT DISTINCT cp FROM CoachProfile cp JOIN cp.sports s WHERE s.id = :sportId")
    List<CoachProfile> findBySportId(@Param("sportId") Long sportId);
}

