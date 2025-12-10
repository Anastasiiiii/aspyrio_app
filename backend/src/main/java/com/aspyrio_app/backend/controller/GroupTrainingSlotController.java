package com.aspyrio_app.backend.controller;

import com.aspyrio_app.backend.dto.AllTrainingRequestsResponse;
import com.aspyrio_app.backend.dto.ApproveRejectRequest;
import com.aspyrio_app.backend.dto.BookGroupTrainingSlotRequest;
import com.aspyrio_app.backend.dto.CoachWithSportsResponse;
import com.aspyrio_app.backend.dto.CreateGroupTrainingSlotRequest;
import com.aspyrio_app.backend.dto.GroupTrainingSlotResponse;
import com.aspyrio_app.backend.dto.TrainingSlotRequestResponse;
import com.aspyrio_app.backend.service.groupTrainingSlot.ApproveRejectTrainingSlotService;
import com.aspyrio_app.backend.service.groupTrainingSlot.BookGroupTrainingSlotService;
import com.aspyrio_app.backend.service.groupTrainingSlot.CreateGroupTrainingSlotService;
import com.aspyrio_app.backend.service.groupTrainingSlot.GetAvailableGroupTrainingSlotsService;
import com.aspyrio_app.backend.service.groupTrainingSlot.GetCoachesForUserService;
import com.aspyrio_app.backend.service.groupTrainingSlot.GetCoachesWithSportsService;
import com.aspyrio_app.backend.service.groupTrainingSlot.GetGroupTrainingSlotsForCalendarService;
import com.aspyrio_app.backend.service.groupTrainingSlot.GetTrainingSlotRequestsService;
import com.aspyrio_app.backend.service.groupTrainingSlot.GetUserBookingsService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/group-training-slots")
public class GroupTrainingSlotController {
    private final CreateGroupTrainingSlotService createGroupTrainingSlotService;
    private final GetCoachesWithSportsService getCoachesWithSportsService;
    private final GetCoachesForUserService getCoachesForUserService;
    private final ApproveRejectTrainingSlotService approveRejectTrainingSlotService;
    private final GetTrainingSlotRequestsService getTrainingSlotRequestsService;
    private final GetGroupTrainingSlotsForCalendarService getGroupTrainingSlotsForCalendarService;
    private final GetAvailableGroupTrainingSlotsService getAvailableGroupTrainingSlotsService;
    private final BookGroupTrainingSlotService bookGroupTrainingSlotService;
    private final GetUserBookingsService getUserBookingsService;

    @GetMapping("/coaches")
    public ResponseEntity<List<CoachWithSportsResponse>> getCoachesWithSports() {
        return ResponseEntity.ok(getCoachesWithSportsService.getCoachesWithSports());
    }

    @GetMapping("/coaches-for-user")
    public ResponseEntity<List<CoachWithSportsResponse>> getCoachesForUser() {
        return ResponseEntity.ok(getCoachesForUserService.getCoachesForUser());
    }

    @PostMapping
    public ResponseEntity<GroupTrainingSlotResponse> createGroupTrainingSlot(
            @RequestBody CreateGroupTrainingSlotRequest request) {
        return ResponseEntity.ok(createGroupTrainingSlotService.createGroupTrainingSlot(request));
    }

    @PostMapping("/approve-reject")
    public ResponseEntity<Void> approveOrRejectTrainingSlot(
            @RequestBody ApproveRejectRequest request) {
        approveRejectTrainingSlotService.approveOrRejectTrainingSlot(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/requests")
    public ResponseEntity<List<TrainingSlotRequestResponse>> getTrainingSlotRequests() {
        return ResponseEntity.ok(getTrainingSlotRequestsService.getTrainingSlotRequests());
    }

    @GetMapping("/all-requests")
    public ResponseEntity<AllTrainingRequestsResponse> getAllTrainingSlotRequests() {
        return ResponseEntity.ok(getTrainingSlotRequestsService.getAllTrainingSlotRequests());
    }

    @GetMapping("/calendar")
    public ResponseEntity<List<GroupTrainingSlotResponse>> getGroupTrainingSlotsForCalendar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(getGroupTrainingSlotsForCalendarService.getGroupTrainingSlotsForCalendar(startDate, endDate));
    }

    @GetMapping("/available")
    public ResponseEntity<List<GroupTrainingSlotResponse>> getAvailableGroupTrainingSlots(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(getAvailableGroupTrainingSlotsService.getAvailableGroupTrainingSlots(startDate, endDate));
    }

    @PostMapping("/book")
    public ResponseEntity<Void> bookGroupTrainingSlot(
            @RequestBody BookGroupTrainingSlotRequest request) {
        bookGroupTrainingSlotService.bookGroupTrainingSlot(request.getSlotId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<GroupTrainingSlotResponse>> getUserBookings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(getUserBookingsService.getUserBookings(startDate, endDate));
    }
}

