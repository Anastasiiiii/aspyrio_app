package com.aspyrio_app.backend.service.individualTrainingRequest;

import com.aspyrio_app.backend.dto.CreateIndividualTrainingRequestRequest;
import com.aspyrio_app.backend.dto.IndividualTrainingRequestResponse;
import com.aspyrio_app.backend.model.*;
import com.aspyrio_app.backend.repository.IndividualTrainingRequestRepository;
import com.aspyrio_app.backend.repository.SportsRepository;
import com.aspyrio_app.backend.repository.UserRepository;
import com.aspyrio_app.backend.security.AuthValidator;
import com.aspyrio_app.backend.security.RoleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateIndividualTrainingRequestService {
    private final IndividualTrainingRequestRepository individualTrainingRequestRepository;
    private final UserRepository userRepository;
    private final SportsRepository sportsRepository;
    private final AuthValidator authValidator;
    private final RoleValidator roleValidator;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public IndividualTrainingRequestResponse createIndividualTrainingRequest(
            CreateIndividualTrainingRequestRequest request) {
        authValidator.ensureAuthenticated();
        roleValidator.ensureHasRole("ROLE_USER");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        User coach = userRepository.findById(request.getCoachId())
                .orElseThrow(() -> new RuntimeException("Coach not found"));
        if (coach.getRole() != Role.COACH) {
            throw new RuntimeException("Selected user is not a coach");
        }

        Sports sport = sportsRepository.findById(request.getSportId())
                .orElseThrow(() -> new RuntimeException("Sport not found"));

        if (request.getRequestedStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Requested start time cannot be in the past");
        }
        if (request.getRequestedEndTime().isBefore(request.getRequestedStartTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        IndividualTrainingRequest trainingRequest = new IndividualTrainingRequest();
        trainingRequest.setUser(currentUser);
        trainingRequest.setCoach(coach);
        trainingRequest.setSport(sport);
        trainingRequest.setRequestedStartTime(request.getRequestedStartTime());
        trainingRequest.setRequestedEndTime(request.getRequestedEndTime());
        trainingRequest.setTrainingType(request.getTrainingType());
        trainingRequest.setMessage(request.getMessage());
        trainingRequest.setStatus(RequestStatus.PENDING);

        IndividualTrainingRequest savedRequest = individualTrainingRequestRepository.save(trainingRequest);

        IndividualTrainingRequestResponse response = mapToResponse(savedRequest);
        messagingTemplate.convertAndSendToUser(
                coach.getUsername(),
                "/queue/individual-training-requests",
                response
        );

        return response;
    }

    private IndividualTrainingRequestResponse mapToResponse(IndividualTrainingRequest request) {
        IndividualTrainingRequestResponse response = new IndividualTrainingRequestResponse();
        response.setId(request.getId());
        response.setUserId(request.getUser().getId());
        response.setUserName(request.getUser().getUsername());
        response.setCoachId(request.getCoach().getId());
        response.setCoachName(request.getCoach().getUsername());
        response.setSportId(request.getSport().getId());
        response.setSportName(request.getSport().getName());
        response.setRequestedStartTime(request.getRequestedStartTime());
        response.setRequestedEndTime(request.getRequestedEndTime());
        response.setTrainingType(request.getTrainingType());
        response.setMessage(request.getMessage());
        response.setStatus(request.getStatus().name());
        response.setCoachResponseAt(request.getCoachResponseAt());
        response.setCreatedAt(request.getCreatedAt());
        return response;
    }
}

