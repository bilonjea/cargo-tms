package fr.cargo.tms.events;

import fr.cargo.tms.contracts.model.MovementDto;

public record MovementCreatedEvent(Long movementId, MovementDto movement) {}
