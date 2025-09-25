package fr.cargo.tms.service;

import fr.cargo.tms.contracts.model.RefTypeDto;
import fr.cargo.tms.contracts.model.ReferenceAvailabilityResponseDto;

public interface ReferencesService {
    ReferenceAvailabilityResponseDto getAvailability(RefTypeDto type, String code);
}

