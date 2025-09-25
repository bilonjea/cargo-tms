package fr.cargo.tms.service;

import fr.cargo.tms.contracts.model.RefTypeDto;
import fr.cargo.tms.contracts.model.ReferenceAvailabilityResponseDto;
import fr.cargo.tms.dao.bean.enums.MovementType;
import fr.cargo.tms.dao.bean.enums.RefType;
import fr.cargo.tms.dao.repository.MovementRepository;
import fr.cargo.tms.dao.repository.projections.MovementTotals;
import fr.cargo.tms.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReferencesServiceImpl implements ReferencesService {

    private final MovementRepository movementRepository;

    @Override
    public ReferenceAvailabilityResponseDto getAvailability(RefTypeDto typeDto, String code) {
        RefType type = RefType.valueOf(typeDto.name());

        MovementTotals inTotals = movementRepository
                .sumTotalsByTypeAndRef(MovementType.IN, type, code)
                .orElse(null);

        MovementTotals outTotals = movementRepository
                .sumTotalsByTypeAndRef(MovementType.OUT, type, code)
                .orElse(null);

        // 404 si aucune trace de la référence (ni IN ni OUT)
        if (inTotals == null && outTotals == null) {
            throw new NotFoundException("Unknown reference: %s/%s".formatted(typeDto, code));
        }

        double inQty = inTotals != null && inTotals.getTotalQuantity() != null ? inTotals.getTotalQuantity() : 0d;
        double inWgt = inTotals != null && inTotals.getTotalWeight() != null ? inTotals.getTotalWeight() : 0d;
        double outQty = outTotals != null && outTotals.getTotalQuantity() != null ? outTotals.getTotalQuantity() : 0d;
        double outWgt = outTotals != null && outTotals.getTotalWeight() != null ? outTotals.getTotalWeight() : 0d;

        return new ReferenceAvailabilityResponseDto()
                .refType(typeDto)
                .refCode(code)
                .totalQuantity(inQty)
                .totalWeight(inWgt)
                .sumInQuantity(inQty)
                .sumOutQuantity(outQty)
                .availableQuantity(inQty - outQty)
                .availableWeight(inWgt - outWgt);
    }
}

