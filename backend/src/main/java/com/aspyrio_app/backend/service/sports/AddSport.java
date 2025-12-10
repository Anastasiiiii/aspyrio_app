package com.aspyrio_app.backend.service.sports;

import com.aspyrio_app.backend.dto.AddSportRequest;
import com.aspyrio_app.backend.model.Sports;
import com.aspyrio_app.backend.repository.SportsRepository;
import com.aspyrio_app.backend.security.AuthValidator;
import com.aspyrio_app.backend.security.RoleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddSport {
    private final SportsRepository sportsRepository;
    private final AuthValidator authValidator;
    private final RoleValidator roleValidator;

    public Sports addSport(AddSportRequest request) {
        authValidator.ensureAuthenticated();
        roleValidator.ensureHasRole("ROLE_FITNESS_ADMIN");

        if (sportsRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Sport with this name already exists");
        }

        Sports sport = new Sports();
        sport.setName(request.getName().trim());

        return sportsRepository.save(sport);
    }
}
