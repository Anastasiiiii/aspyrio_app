package com.aspyrio_app.backend.repository;

import com.aspyrio_app.backend.model.User;
import com.aspyrio_app.backend.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUser(User user);
}


