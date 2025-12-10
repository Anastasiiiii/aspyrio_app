package com.aspyrio_app.backend.service.sports;

import com.aspyrio_app.backend.model.Sports;
import com.aspyrio_app.backend.repository.SportsRepository;
import com.aspyrio_app.backend.security.AuthValidator;
import com.aspyrio_app.backend.security.RoleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetSportsService {
    private final SportsRepository sportsRepository;
    private final AuthValidator authValidator;
    private final RoleValidator roleValidator;

    @Transactional(readOnly = true)
    public List<Sports> getAllSports() {
        authValidator.ensureAuthenticated();

        return sportsRepository.findAll();
    }
}

