package com.aspyrio_app.backend.controller;

import com.aspyrio_app.backend.dto.FitnessAdminCreateResponse;
import com.aspyrio_app.backend.dto.FitnessAdminRegisterRequest;
import com.aspyrio_app.backend.model.User;
import com.aspyrio_app.backend.service.fitnessCenterAdmin.CreateFitnessAdminService;
import com.aspyrio_app.backend.service.fitnessCenterAdmin.GetFitnessAdminsService;
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
public class FitnessAdminController {
    private final CreateFitnessAdminService fitnessAdminService;
    private final GetFitnessAdminsService getFitnessAdminsService;

    @PostMapping("/fitness-admin/create")
    public ResponseEntity<FitnessAdminCreateResponse> registerFitnessAdmin(@RequestBody FitnessAdminRegisterRequest request) {
        return ResponseEntity.ok(fitnessAdminService.createFitnessAdmin(request));
    }

    @GetMapping("/fitness-admin/list")
    public ResponseEntity<List<User>> getAllFitnessAdmins() {
        return ResponseEntity.ok(getFitnessAdminsService.getAllFitnessAdmins());
    }
}
