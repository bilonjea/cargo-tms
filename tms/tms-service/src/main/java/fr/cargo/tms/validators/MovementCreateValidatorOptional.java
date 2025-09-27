package fr.cargo.tms.validators;

import fr.cargo.tms.contracts.model.GoodsDto;
import fr.cargo.tms.contracts.model.MovementCreateRequestDto;
import fr.cargo.tms.contracts.model.RefTypeDto;
import fr.cargo.tms.error.ValidationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class MovementCreateValidatorOptional {

    public void assertValidate(MovementCreateRequestDto dto) {
        Optional.ofNullable(dto)
                .orElseThrow(() -> new ValidationException("Request body is required"));

        this.validate(dto);
    }

    private void validate(MovementCreateRequestDto dto) {
        validateNotNull(dto.getMovementType(), "movementType is required");
        validateNotNull(dto.getMovementTime(), "movementTime is required");
        validateNotBlank(dto.getCreatedBy(), "createdBy is required");
        validateNotNull(dto.getDeclaredIn(), "declaredIn is required");
        validateNotNull(dto.getGoods(), "goods is required");
        validateNotBlank(dto.getCustomsStatus(), "customsStatus is required");

        validateGoods(dto.getGoods());

        // Validation spécifique au type de mouvement
        switch (dto.getMovementType()) {
            case IN -> validateInMovement(dto);
            case OUT -> validateOutMovement(dto);
            default -> throw new ValidationException("Unknown movement type: " + dto.getMovementType());
        }
    }

    private void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new ValidationException(message);
        }
    }

    private void validateNotBlank(String value, String message) {
        if (isBlank(value)) {
            throw new ValidationException(message);
        }
    }

    private void validateGoods(GoodsDto goods) {
        validateNotNull(goods.getRefType(), "goods.refType is required");
        validateNotBlank(goods.getRefCode(), "goods.refCode is required");
        validatePositive(goods.getQuantity(), "goods.quantity must be > 0");
        validatePositive(goods.getWeight(), "goods.weight must be > 0");
        validatePositive(goods.getTotalQuantity(), "goods.totalQuantity must be > 0");
        validatePositive(goods.getTotalWeight(), "goods.totalWeight must be > 0");

        if (goods.getQuantity() > goods.getTotalQuantity()) {
            throw new ValidationException("goods.quantity cannot exceed goods.totalQuantity");
        }
        if (goods.getWeight() > goods.getTotalWeight()) {
            throw new ValidationException("goods.weight cannot exceed goods.totalWeight");
        }

        if (goods.getRefType() == RefTypeDto.AWB && !goods.getRefCode().matches("\\d{11}")) {
            throw new ValidationException("goods.refCode must contain exactly 11 digits when refType=AWB");
        }
    }

    private void validatePositive(Number value, String message) {
        if (value == null || value.doubleValue() <= 0) {
            throw new ValidationException(message);
        }
    }

    private void validateInMovement(MovementCreateRequestDto dto) {
        validateNotNull(dto.getFromWarehouse(), "fromWarehouse is required for IN");
        if (dto.getToWarehouse() != null) {
            throw new ValidationException("toWarehouse must be null for IN");
        }
        if (dto.getCustomsDocument() != null) {
            throw new ValidationException("customsDocument must be null for IN");
        }
    }

    private void validateOutMovement(MovementCreateRequestDto dto) {
        validateNotNull(dto.getToWarehouse(), "toWarehouse is required for OUT");
        if (dto.getFromWarehouse() != null) {
            throw new ValidationException("fromWarehouse must be null for OUT");
        }
        validateNotNull(dto.getCustomsDocument(), "customsDocument is required for OUT");
        validateNotBlank(dto.getCustomsDocument().getType(), "customsDocument.type is required for OUT");
        validateNotBlank(dto.getCustomsDocument().getRef(), "customsDocument.ref is required for OUT");
    }
}

