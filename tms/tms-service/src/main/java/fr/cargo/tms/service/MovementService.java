package fr.cargo.tms.service;

import fr.cargo.tms.contracts.model.MovementCreateRequestDto;
import fr.cargo.tms.contracts.model.MovementListItemDto;
import fr.cargo.tms.contracts.model.MovementResponseDto;

import java.util.List;

public interface MovementService {
    MovementResponseDto createMovement(MovementCreateRequestDto dto);
    List<MovementListItemDto> listMovements(int limit);
}

