package com.aspyrio_app.backend.service.individualTrainingRequest;

import com.aspyrio_app.backend.dto.IndividualTrainingRequestResponse;
import com.aspyrio_app.backend.model.*;
import com.aspyrio_app.backend.repository.IndividualTrainingRequestRepository;
import com.aspyrio_app.backend.repository.UserRepository;
import com.aspyrio_app.backend.security.AuthValidator;
import com.aspyrio_app.backend.security.RoleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetIndividualTrainingRequestsService {
    private final IndividualTrainingRequestRepository individualTrainingRequestRepository;
    private final UserRepository userRepository;
    private final AuthValidator authValidator;
    private final RoleValidator roleValidator;

    @Transactional(readOnly = true)
    public List<IndividualTrainingRequestResponse> getIndividualTrainingRequests() {
        authValidator.ensureAuthenticated();
        roleValidator.ensureHasRole("ROLE_COACH");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentCoach = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<IndividualTrainingRequest> requests = individualTrainingRequestRepository.findByCoach(currentCoach);

        return requests.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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


