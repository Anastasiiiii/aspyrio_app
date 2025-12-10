package com.aspyrio_app.backend.controller;

import com.aspyrio_app.backend.dto.PassResponse;
import com.aspyrio_app.backend.service.pass.GeneratePassService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/passes")
public class PassController {
    private final GeneratePassService generatePassService;

    @PostMapping("/generate")
    public ResponseEntity<PassResponse> generatePass() {
        PassResponse pass = generatePassService.generatePass();
        return ResponseEntity.ok(pass);
    }
}


