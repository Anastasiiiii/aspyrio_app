package com.aspyrio_app.backend.service.individualTrainingRequest;

import com.aspyrio_app.backend.dto.ApproveRejectRequest;
import com.aspyrio_app.backend.model.*;
import com.aspyrio_app.backend.repository.GroupTrainingSlotRepository;
import com.aspyrio_app.backend.repository.IndividualTrainingRequestRepository;
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
public class ApproveRejectIndividualTrainingRequestService {
    private final IndividualTrainingRequestRepository individualTrainingRequestRepository;
    private final GroupTrainingSlotRepository groupTrainingSlotRepository;
    private final UserRepository userRepository;
    private final AuthValidator authValidator;
    private final RoleValidator roleValidator;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void approveOrRejectIndividualTrainingRequest(ApproveRejectRequest request) {
        authValidator.ensureAuthenticated();
        roleValidator.ensureHasRole("ROLE_COACH");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentCoach = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        IndividualTrainingRequest trainingRequest = individualTrainingRequestRepository.findById(request.getRequestId())
                .orElseThrow(() -> new RuntimeException("Individual training request not found"));

        if (!trainingRequest.getCoach().getId().equals(currentCoach.getId())) {
            throw new RuntimeException("You can only approve/reject your own training requests");
        }

        if (trainingRequest.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Request has already been processed");
        }

        if ("APPROVE".equalsIgnoreCase(request.getAction())) {
            trainingRequest.setStatus(RequestStatus.APPROVED);
            trainingRequest.setCoachResponseAt(LocalDateTime.now());

            GroupTrainingSlot slot = new GroupTrainingSlot();
            slot.setCoach(trainingRequest.getCoach());
            slot.setSport(trainingRequest.getSport());
            slot.setStudio(null);
            slot.setUser(trainingRequest.getUser());
            slot.setTrainingCategory(TrainingCategory.INDIVIDUAL);
            try {
                slot.setTrainingType(TrainingType.valueOf(trainingRequest.getTrainingType()));
            } catch (IllegalArgumentException e) {
                if ("BOTH_ONLINE_OFFLINE".equals(trainingRequest.getTrainingType())) {
                    slot.setTrainingType(TrainingType.BOTH_ONLINE_OFFLINE);
                } else {
                    throw new RuntimeException("Invalid training type: " + trainingRequest.getTrainingType());
                }
            }
            slot.setCreatedBy(currentCoach);
            slot.setStartTime(trainingRequest.getRequestedStartTime());
            slot.setEndTime(trainingRequest.getRequestedEndTime());
            slot.setMaxParticipants(1);
            slot.setStatus(TrainingSlotStatus.APPROVED);

            groupTrainingSlotRepository.save(slot);

            messagingTemplate.convertAndSendToUser(
                    trainingRequest.getUser().getUsername(),
                    "/queue/individual-training-responses",
                    "Your individual training request has been approved"
            );
        } else if ("REJECT".equalsIgnoreCase(request.getAction())) {
            trainingRequest.setStatus(RequestStatus.REJECTED);
            trainingRequest.setCoachResponseAt(LocalDateTime.now());

            messagingTemplate.convertAndSendToUser(
                    trainingRequest.getUser().getUsername(),
                    "/queue/individual-training-responses",
                    "Your individual training request has been rejected"
            );
        } else {
            throw new RuntimeException("Invalid action. Use 'APPROVE' or 'REJECT'");
        }

        individualTrainingRequestRepository.save(trainingRequest);
    }
}

