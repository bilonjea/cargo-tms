package fr.cargo.tms.controller;

import fr.cargo.tms.contracts.api.DevApi;
import fr.cargo.tms.service.FixtureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/")
public class DevController implements DevApi {

    private final FixtureService fixtureService;

    @Override
    public ResponseEntity<Void> loadFixtures(Integer count) {
        fixtureService.loadFixtures(count != null ? count : 10); // valeur par défaut
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> cleanFixtures() {
        fixtureService.cleanFixtures();
        return ResponseEntity.noContent().build();
    }
}

