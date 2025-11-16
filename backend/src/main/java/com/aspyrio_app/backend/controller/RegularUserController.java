package com.aspyrio_app.backend.controller;

import com.aspyrio_app.backend.dto.UserRegisterRequest;
import com.aspyrio_app.backend.model.User;
import com.aspyrio_app.backend.service.regularUser.CreateRegularUser;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class RegularUserController {
    private final CreateRegularUser createRegularUser;

    @PostMapping("/register-regular-user")
    public ResponseEntity<User> registerRegularUser(@RequestBody UserRegisterRequest request){
        return ResponseEntity.ok(createRegularUser.registerRegularUser(request));
    }
}
