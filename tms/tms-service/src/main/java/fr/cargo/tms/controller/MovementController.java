package fr.cargo.tms.controller;

import fr.cargo.tms.contracts.api.MovementsApi;
import fr.cargo.tms.contracts.model.MovementCreateRequestDto;
import fr.cargo.tms.contracts.model.MovementListItemDto;
import fr.cargo.tms.contracts.model.MovementResponseDto;
import fr.cargo.tms.service.MovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/")
public class MovementController implements MovementsApi {

    private final MovementService movementService;

    @Override
    public ResponseEntity<MovementResponseDto> createMovement(MovementCreateRequestDto movementCreateRequestDto) {
        return ResponseEntity.status(201).body(movementService.createMovement(movementCreateRequestDto));
    }

    @Override
    public ResponseEntity<List<MovementListItemDto>> listMovements(Integer limit) {
        return ResponseEntity.ok(movementService.listMovements(limit));
    }
}

