package com.aspyrio_app.backend.controller;

import com.aspyrio_app.backend.dto.UserRegisterRequest;
import com.aspyrio_app.backend.model.User;
import com.aspyrio_app.backend.service.coach.CreateCoachService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class CoachController {
    private final CreateCoachService createCoachService;

    @PostMapping("/register-coach")
    public ResponseEntity<User> registerCoach(@RequestBody UserRegisterRequest request){
        return ResponseEntity.ok(createCoachService.registerCoach(request));
    }
}
