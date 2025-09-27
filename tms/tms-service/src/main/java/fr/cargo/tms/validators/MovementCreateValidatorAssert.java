package fr.cargo.tms.validators;

import fr.cargo.tms.contracts.model.GoodsDto;
import fr.cargo.tms.contracts.model.MovementCreateRequestDto;
import fr.cargo.tms.contracts.model.RefTypeDto;
import fr.cargo.tms.error.ValidationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Component
public class MovementCreateValidatorAssert {

    public void assertValidate(MovementCreateRequestDto dto) {
        Assert.notNull(dto, "Request body is required");

        // Champs communs obligatoires
        Assert.notNull(dto.getMovementType(), "movementType is required");
        Assert.notNull(dto.getMovementTime(), "movementTime is required");
        Assert.isTrue(StringUtils.hasText(dto.getCreatedBy()), "createdBy is required");
        Assert.notNull(dto.getDeclaredIn(), "declaredIn is required");
        Assert.notNull(dto.getGoods(), "goods is required");
        Assert.isTrue(StringUtils.hasText(dto.getCustomsStatus()), "customsStatus is required");

        // Validation des biens (goods)
        validateGoods(dto.getGoods());

        // Validation spécifique au type de mouvement
        switch (dto.getMovementType()) {
            case IN -> validateInMovement(dto);
            case OUT -> validateOutMovement(dto);
            default -> throw new ValidationException("Unknown movement type: " + dto.getMovementType());
        }
    }

    private void validateGoods(GoodsDto goods) {
        Assert.notNull(goods.getRefType(), "goods.refType is required");
        Assert.isTrue(StringUtils.hasText(goods.getRefCode()), "goods.refCode is required");
        Assert.notNull(goods.getQuantity(), "goods.quantity is required");
        Assert.isTrue(goods.getQuantity() > 0, "goods.quantity must be > 0");
        Assert.notNull(goods.getWeight(), "goods.weight is required");
        Assert.isTrue(goods.getWeight() > 0, "goods.weight must be > 0");
        Assert.notNull(goods.getTotalQuantity(), "goods.totalQuantity is required");
        Assert.isTrue(goods.getTotalQuantity() > 0, "goods.totalQuantity must be > 0");
        Assert.notNull(goods.getTotalWeight(), "goods.totalWeight is required");
        Assert.isTrue(goods.getTotalWeight() > 0, "goods.totalWeight must be > 0");

        Assert.isTrue(goods.getQuantity() <= goods.getTotalQuantity(), "goods.quantity cannot exceed goods.totalQuantity");
        Assert.isTrue(goods.getWeight() <= goods.getTotalWeight(), "goods.weight cannot exceed goods.totalWeight");

        if (goods.getRefType() == RefTypeDto.AWB) {
            Assert.isTrue(goods.getRefCode().matches("\\d{11}"), "goods.refCode must contain exactly 11 digits when refType=AWB");
        }
    }

    private void validateInMovement(MovementCreateRequestDto dto) {
        Assert.notNull(dto.getFromWarehouse(), "fromWarehouse is required for IN");
        Assert.isNull(dto.getToWarehouse(), "toWarehouse must be null for IN");
        Assert.isNull(dto.getCustomsDocument(), "customsDocument must be null for IN");
    }

    private void validateOutMovement(MovementCreateRequestDto dto) {
        Assert.notNull(dto.getToWarehouse(), "toWarehouse is required for OUT");
        Assert.isNull(dto.getFromWarehouse(), "fromWarehouse must be null for OUT");
        Assert.notNull(dto.getCustomsDocument(), "customsDocument is required for OUT");
        Assert.isTrue(StringUtils.hasText(dto.getCustomsDocument().getType()), "customsDocument.type is required for OUT");
        Assert.isTrue(StringUtils.hasText(dto.getCustomsDocument().getRef()), "customsDocument.ref is required for OUT");
    }
}


