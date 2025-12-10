package com.aspyrio_app.backend.repository;

import com.aspyrio_app.backend.model.FitnessCenter;
import com.aspyrio_app.backend.model.Role;
import com.aspyrio_app.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
    
    List<User> findByRole(Role role);

    List<User> findByRoleAndCenter(Role role, FitnessCenter center);
}
