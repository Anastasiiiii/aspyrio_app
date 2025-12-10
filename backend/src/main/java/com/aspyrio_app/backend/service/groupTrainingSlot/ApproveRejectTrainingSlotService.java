package com.aspyrio_app.backend.service.groupTrainingSlot;

import com.aspyrio_app.backend.dto.ApproveRejectRequest;
import com.aspyrio_app.backend.model.*;
import com.aspyrio_app.backend.repository.GroupTrainingSlotRepository;
import com.aspyrio_app.backend.repository.TrainingSlotRequestRepository;
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
public class ApproveRejectTrainingSlotService {
    private final TrainingSlotRequestRepository trainingSlotRequestRepository;
    private final GroupTrainingSlotRepository groupTrainingSlotRepository;
    private final UserRepository userRepository;
    private final AuthValidator authValidator;
    private final RoleValidator roleValidator;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void approveOrRejectTrainingSlot(ApproveRejectRequest request) {
        authValidator.ensureAuthenticated();
        roleValidator.ensureHasRole("ROLE_COACH");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentCoach = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        TrainingSlotRequest slotRequest = trainingSlotRequestRepository.findById(request.getRequestId())
                .orElseThrow(() -> new RuntimeException("Training slot request not found"));

        // Verify that the request belongs to the current coach
        if (!slotRequest.getGroupTrainingSlot().getCoach().getId().equals(currentCoach.getId())) {
            throw new RuntimeException("You can only approve/reject your own training slot requests");
        }

        // Check if already processed
        if (slotRequest.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Request has already been processed");
        }

        GroupTrainingSlot slot = slotRequest.getGroupTrainingSlot();

        if ("APPROVE".equalsIgnoreCase(request.getAction())) {
            slotRequest.setStatus(RequestStatus.APPROVED);
            slot.setStatus(TrainingSlotStatus.APPROVED);
            slotRequest.setCoachResponseAt(LocalDateTime.now());
            
            // Notify fitness admin
            messagingTemplate.convertAndSendToUser(
                    slot.getCreatedBy().getUsername(),
                    "/queue/training-responses",
                    "Training slot approved by coach"
            );
        } else if ("REJECT".equalsIgnoreCase(request.getAction())) {
            slotRequest.setStatus(RequestStatus.REJECTED);
            slot.setStatus(TrainingSlotStatus.REJECTED);
            slotRequest.setCoachResponseAt(LocalDateTime.now());
            
            messagingTemplate.convertAndSendToUser(
                    slot.getCreatedBy().getUsername(),
                    "/queue/training-responses",
                    "Training slot rejected by coach"
            );
        } else {
            throw new RuntimeException("Invalid action. Use 'APPROVE' or 'REJECT'");
        }

        trainingSlotRequestRepository.save(slotRequest);
        groupTrainingSlotRepository.save(slot);
    }
}

