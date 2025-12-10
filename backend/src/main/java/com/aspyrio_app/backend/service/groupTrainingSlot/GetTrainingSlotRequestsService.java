package com.aspyrio_app.backend.service.groupTrainingSlot;

import com.aspyrio_app.backend.dto.AllTrainingRequestsResponse;
import com.aspyrio_app.backend.dto.GroupTrainingSlotResponse;
import com.aspyrio_app.backend.dto.IndividualTrainingRequestResponse;
import com.aspyrio_app.backend.dto.TrainingSlotRequestResponse;
import com.aspyrio_app.backend.model.*;
import com.aspyrio_app.backend.repository.IndividualTrainingRequestRepository;
import com.aspyrio_app.backend.repository.TrainingSlotRequestRepository;
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
public class GetTrainingSlotRequestsService {
    private final TrainingSlotRequestRepository trainingSlotRequestRepository;
    private final IndividualTrainingRequestRepository individualTrainingRequestRepository;
    private final UserRepository userRepository;
    private final AuthValidator authValidator;
    private final RoleValidator roleValidator;

    @Transactional(readOnly = true)
    public AllTrainingRequestsResponse getAllTrainingSlotRequests() {
        authValidator.ensureAuthenticated();
        roleValidator.ensureHasRole("ROLE_COACH");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentCoach = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<TrainingSlotRequest> groupRequests = trainingSlotRequestRepository.findAll().stream()
                .filter(request -> request.getGroupTrainingSlot().getCoach().getId().equals(currentCoach.getId()))
                .collect(Collectors.toList());

        List<TrainingSlotRequestResponse> groupRequestResponses = groupRequests.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        List<IndividualTrainingRequest> individualRequests = individualTrainingRequestRepository.findByCoach(currentCoach);
        List<IndividualTrainingRequestResponse> individualRequestResponses = individualRequests.stream()
                .map(this::mapIndividualToResponse)
                .collect(Collectors.toList());

        return new AllTrainingRequestsResponse(groupRequestResponses, individualRequestResponses);
    }

    @Transactional(readOnly = true)
    public List<TrainingSlotRequestResponse> getTrainingSlotRequests() {
        return getAllTrainingSlotRequests().getGroupTrainingRequests();
    }

    private TrainingSlotRequestResponse mapToResponse(TrainingSlotRequest request) {
        GroupTrainingSlot slot = request.getGroupTrainingSlot();
        TrainingSlotRequestResponse response = new TrainingSlotRequestResponse();
        response.setId(request.getId());
        response.setGroupTrainingSlotId(slot.getId());
        response.setStatus(request.getStatus().name());
        response.setCoachResponseAt(request.getCoachResponseAt());
        response.setCreatedAt(request.getCreatedAt());

        GroupTrainingSlotResponse slotResponse = new GroupTrainingSlotResponse();
        slotResponse.setId(slot.getId());
        slotResponse.setCoachId(slot.getCoach().getId());
        slotResponse.setCoachName(slot.getCoach().getUsername());
        slotResponse.setSportId(slot.getSport().getId());
        slotResponse.setSportName(slot.getSport().getName());
        if (slot.getStudio() != null) {
            slotResponse.setStudioId(slot.getStudio().getId());
            slotResponse.setStudioName(slot.getStudio().getName());
        }
        slotResponse.setTrainingCategory(slot.getTrainingCategory().name());
        slotResponse.setTrainingType(slot.getTrainingType().name());
        slotResponse.setStartTime(slot.getStartTime());
        slotResponse.setEndTime(slot.getEndTime());
        slotResponse.setMaxParticipants(slot.getMaxParticipants());
        slotResponse.setStatus(slot.getStatus());
        slotResponse.setCreatedAt(slot.getCreatedAt());
        slotResponse.setRequestId(request.getId());

        response.setSlotDetails(slotResponse);
        return response;
    }

    private IndividualTrainingRequestResponse mapIndividualToResponse(IndividualTrainingRequest request) {
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
