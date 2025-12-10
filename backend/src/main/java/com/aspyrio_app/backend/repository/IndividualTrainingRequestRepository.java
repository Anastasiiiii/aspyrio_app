package com.aspyrio_app.backend.repository;

import com.aspyrio_app.backend.model.IndividualTrainingRequest;
import com.aspyrio_app.backend.model.RequestStatus;
import com.aspyrio_app.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IndividualTrainingRequestRepository extends JpaRepository<IndividualTrainingRequest, Long> {
    List<IndividualTrainingRequest> findByCoach(User coach);
    List<IndividualTrainingRequest> findByUser(User user);
}


