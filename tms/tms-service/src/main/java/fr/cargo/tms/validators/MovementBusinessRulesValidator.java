package fr.cargo.tms.validators;

import fr.cargo.tms.contracts.model.MovementCreateRequestDto;
import fr.cargo.tms.dao.bean.enums.MovementType;
import fr.cargo.tms.dao.bean.enums.RefType;
import fr.cargo.tms.dao.repository.MovementRepository;
import fr.cargo.tms.dao.repository.projections.MovementTotals;
import fr.cargo.tms.error.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovementBusinessRulesValidator {

    private final MovementRepository movementRepository;

    public void validate(MovementCreateRequestDto dto) {
        switch (dto.getMovementType()) {
            case IN -> validateIn(dto);
            case OUT -> validateOut(dto);
            default -> throw new BusinessRuleException("Unknown movementType");
        }
    }

    private void validateIn(MovementCreateRequestDto dto) {
        var g = dto.getGoods();
        if (g.getQuantity() > g.getTotalQuantity())
            throw new BusinessRuleException("IN quantity cannot exceed total quantity");
        if (g.getWeight() > g.getTotalWeight())
            throw new BusinessRuleException("IN weight cannot exceed total weight");
    }

    private void validateOut(MovementCreateRequestDto dto) {
        var refType = RefType.valueOf(dto.getGoods().getRefType().name());
        var refCode = dto.getGoods().getRefCode();

        MovementTotals inTotals  = movementRepository
                .sumTotalsByTypeAndRef(MovementType.IN,  refType, refCode)
                .orElse(null);

        MovementTotals outTotals = movementRepository
                .sumTotalsByTypeAndRef(MovementType.OUT, refType, refCode)
                .orElse(null);

        double inQty = inTotals != null && inTotals.getTotalQuantity() != null ? inTotals.getTotalQuantity() : 0d;
        double outQty = outTotals != null && outTotals.getTotalQuantity() != null ? outTotals.getTotalQuantity() : 0d;
        double inWgt = inTotals != null && inTotals.getTotalWeight() != null ? inTotals.getTotalWeight() : 0d;
        double outWgt = outTotals != null && outTotals.getTotalWeight() != null ? outTotals.getTotalWeight() : 0d;

        if (dto.getGoods().getQuantity() > (inQty - outQty))
            throw new BusinessRuleException("OUT quantity exceeds available stock");
        if (dto.getGoods().getWeight() > (inWgt - outWgt))
            throw new BusinessRuleException("OUT weight exceeds available stock");
    }
}
