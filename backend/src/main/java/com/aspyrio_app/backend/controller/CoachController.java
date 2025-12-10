package com.aspyrio_app.backend.controller;

import com.aspyrio_app.backend.dto.CoachCreateResponse;
import com.aspyrio_app.backend.dto.UserRegisterRequest;
import com.aspyrio_app.backend.model.User;
import com.aspyrio_app.backend.service.coach.CreateCoachService;
import com.aspyrio_app.backend.service.coach.GetCoachesService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class CoachController {
    private final CreateCoachService createCoachService;
    private final GetCoachesService getCoachesService;

    @PostMapping("/coach/create")
    public ResponseEntity<CoachCreateResponse> registerCoach(@RequestBody UserRegisterRequest request){
        return ResponseEntity.ok(createCoachService.registerCoach(request));
    }

    @GetMapping("/coach/list")
    public ResponseEntity<List<User>> getAllCoaches(@RequestParam(required = false) Long sportId) {
        if (sportId != null) {
            return ResponseEntity.ok(getCoachesService.getAllCoachesBySport(sportId));
        }
        return ResponseEntity.ok(getCoachesService.getAllCoaches());
    }
}
