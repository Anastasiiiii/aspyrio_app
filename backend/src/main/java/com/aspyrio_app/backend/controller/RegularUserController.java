package com.aspyrio_app.backend.controller;

import com.aspyrio_app.backend.dto.UserCreateResponse;
import com.aspyrio_app.backend.dto.UserRegisterRequest;
import com.aspyrio_app.backend.model.User;
import com.aspyrio_app.backend.service.regularUser.CreateRegularUser;
import com.aspyrio_app.backend.service.regularUser.GetRegularUsersService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class RegularUserController {
    private final CreateRegularUser createRegularUser;
    private final GetRegularUsersService getRegularUsersService;

    @PostMapping("/regular-user/create")
    public ResponseEntity<UserCreateResponse> registerRegularUser(@RequestBody UserRegisterRequest request){
        return ResponseEntity.ok(createRegularUser.registerRegularUser(request));
    }

    @GetMapping("/regular-user/list")
    public ResponseEntity<List<User>> getAllRegularUsers() {
        return ResponseEntity.ok(getRegularUsersService.getAllRegularUsers());
    }
}
