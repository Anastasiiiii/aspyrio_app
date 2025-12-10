package com.aspyrio_app.backend.service.groupTrainingSlot;

import com.aspyrio_app.backend.dto.CreateGroupTrainingSlotRequest;
import com.aspyrio_app.backend.dto.GroupTrainingSlotResponse;
import com.aspyrio_app.backend.model.*;
import com.aspyrio_app.backend.repository.*;
import com.aspyrio_app.backend.security.AuthValidator;
import com.aspyrio_app.backend.security.RoleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateGroupTrainingSlotService {
    private final GroupTrainingSlotRepository groupTrainingSlotRepository;
    private final TrainingSlotRequestRepository trainingSlotRequestRepository;
    private final UserRepository userRepository;
    private final SportsRepository sportsRepository;
    private final StudioRepository studioRepository;
    private final AuthValidator authValidator;
    private final RoleValidator roleValidator;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public GroupTrainingSlotResponse createGroupTrainingSlot(CreateGroupTrainingSlotRequest request) {
        authValidator.ensureAuthenticated();
        roleValidator.ensureHasRole("ROLE_FITNESS_ADMIN");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentAdmin = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        User coach = userRepository.findById(request.getCoachId())
                .orElseThrow(() -> new RuntimeException("Coach not found"));
        if (coach.getRole() != Role.COACH) {
            throw new RuntimeException("User is not a coach");
        }

        Sports sport = sportsRepository.findById(request.getSportId())
                .orElseThrow(() -> new RuntimeException("Sport not found"));

        Studio studio = null;
        TrainingCategory category = TrainingCategory.valueOf(request.getTrainingCategory());
        if (category == TrainingCategory.GROUP) {
            if (request.getStudioId() == null) {
                throw new RuntimeException("Studio is required for group training");
            }
            studio = studioRepository.findById(request.getStudioId())
                    .orElseThrow(() -> new RuntimeException("Studio not found"));
        }

        TrainingType trainingType = TrainingType.valueOf(request.getTrainingType());

        if (request.getEndTime().isBefore(request.getStartTime()) ||
            request.getEndTime().isEqual(request.getStartTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        GroupTrainingSlot slot = new GroupTrainingSlot();
        slot.setCoach(coach);
        slot.setSport(sport);
        slot.setStudio(studio);
        slot.setTrainingCategory(category);
        slot.setTrainingType(trainingType);
        slot.setCreatedBy(currentAdmin);
        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        slot.setMaxParticipants(request.getMaxParticipants());
        slot.setStatus(TrainingSlotStatus.PENDING);

        slot = groupTrainingSlotRepository.save(slot);

        TrainingSlotRequest slotRequest = new TrainingSlotRequest();
        slotRequest.setGroupTrainingSlot(slot);
        slotRequest.setStatus(RequestStatus.PENDING);
        slotRequest = trainingSlotRequestRepository.save(slotRequest);

        GroupTrainingSlotResponse response = mapToResponse(slot);
        response.setRequestId(slotRequest.getId());
        
        String userDestination = "/user/" + coach.getUsername() + "/queue/training-requests";
        System.out.println("Sending WebSocket message to: " + userDestination);
        System.out.println("Message content: " + response);
        System.out.println("Coach username: " + coach.getUsername());
        
        try {
            messagingTemplate.convertAndSendToUser(
                    coach.getUsername(),
                    "/queue/training-requests",
                    response
            );
            System.out.println("WebSocket message sent successfully via convertAndSendToUser");
        } catch (Exception e) {
            System.err.println("Error sending WebSocket message: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    private GroupTrainingSlotResponse mapToResponse(GroupTrainingSlot slot) {
        GroupTrainingSlotResponse response = new GroupTrainingSlotResponse();
        response.setId(slot.getId());
        response.setCoachId(slot.getCoach().getId());
        response.setCoachName(slot.getCoach().getUsername());
        response.setSportId(slot.getSport().getId());
        response.setSportName(slot.getSport().getName());
        if (slot.getStudio() != null) {
            response.setStudioId(slot.getStudio().getId());
            response.setStudioName(slot.getStudio().getName());
        }
        response.setTrainingCategory(slot.getTrainingCategory().name());
        response.setTrainingType(slot.getTrainingType().name());
        response.setStartTime(slot.getStartTime());
        response.setEndTime(slot.getEndTime());
        response.setMaxParticipants(slot.getMaxParticipants());
        response.setStatus(slot.getStatus());
        response.setCreatedAt(slot.getCreatedAt());
        return response;
    }
}

