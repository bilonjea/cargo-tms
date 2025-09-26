package fr.cargo.tms.controller;

import fr.cargo.tms.contracts.api.ReferencesApi;
import fr.cargo.tms.contracts.model.RefTypeDto;
import fr.cargo.tms.contracts.model.ReferenceAvailabilityResponseDto;
import fr.cargo.tms.service.ReferencesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/")
public class ReferencesController implements ReferencesApi {

    private final ReferencesService referencesService;

    @Override
    public ResponseEntity<ReferenceAvailabilityResponseDto> getReferenceAvailability(
            RefTypeDto type,
            String code) {
        return ResponseEntity.ok(referencesService.getAvailability(type, code));
    }
}

