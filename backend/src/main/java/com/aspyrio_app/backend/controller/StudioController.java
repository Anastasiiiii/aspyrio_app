package com.aspyrio_app.backend.controller;

import com.aspyrio_app.backend.dto.CreateStudioRequest;
import com.aspyrio_app.backend.dto.StudioResponse;
import com.aspyrio_app.backend.service.studio.CreateStudioService;
import com.aspyrio_app.backend.service.studio.GetStudiosService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/studios")
public class StudioController {
    private final CreateStudioService createStudioService;
    private final GetStudiosService getStudiosService;

    @GetMapping
    public ResponseEntity<List<StudioResponse>> getStudios() {
        return ResponseEntity.ok(getStudiosService.getStudios());
    }

    @PostMapping
    public ResponseEntity<StudioResponse> createStudio(@RequestBody CreateStudioRequest request) {
        return ResponseEntity.ok(createStudioService.createStudio(request));
    }
}


