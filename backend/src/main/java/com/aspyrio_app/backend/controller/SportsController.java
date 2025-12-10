package com.aspyrio_app.backend.controller;

import com.aspyrio_app.backend.dto.AddSportRequest;
import com.aspyrio_app.backend.model.Sports;
import com.aspyrio_app.backend.service.sports.AddSport;
import com.aspyrio_app.backend.service.sports.GetSportsService;
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
public class SportsController {
    private final AddSport addSport;
    private final GetSportsService getSportsService;

    @PostMapping("/sports/create")
    public ResponseEntity<Sports> createSport(@RequestBody AddSportRequest request) {
        return ResponseEntity.ok(addSport.addSport(request));
    }

    @GetMapping("/sports/list")
    public ResponseEntity<List<Sports>> getAllSports() {
        return ResponseEntity.ok(getSportsService.getAllSports());
    }
}

