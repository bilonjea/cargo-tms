/*package fr.cargo.tms.validators;

import fr.cargo.tms.contracts.model.GoodsDto;
import fr.cargo.tms.contracts.model.MovementCreateRequestDto;
import fr.cargo.tms.contracts.model.MovementTypeDto;
import fr.cargo.tms.contracts.model.RefTypeDto;
import fr.cargo.tms.error.ValidationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class MovementValidatorPredicateStream {

    public void assertValidate(MovementCreateRequestDto dto) {
        Optional.ofNullable(dto)
                .orElseThrow(() -> new ValidationException("Request body is required"));
        this.validate(dto);
    }

    private void validate(MovementCreateRequestDto dto) {
        // Validation des champs obligatoires
        validateField(dto.getMovementType(), this::validateNotNull, "movementType is required");
        validateField(dto.getMovementTime(), this::validateNotNull, "movementTime is required");
        validateField(dto.getCreatedBy(), this::validateNotBlank, "createdBy is required");
        validateField(dto.getDeclaredIn(), this::validateNotNull, "declaredIn is required");
        validateField(dto.getGoods(), this::validateNotNull, "goods is required");
        validateField(dto.getCustomsStatus(), this::validateNotBlank, "customsStatus is required");

        validateGoods(dto.getGoods());

        // Validation spécifique au type de mouvement
        switch (dto.getMovementType()) {
            case IN -> validateInMovement(dto);
            case OUT -> validateOutMovement(dto);
            default -> throw new ValidationException("Unknown movement type: " + dto.getMovementType());
        }
    }

    private <T> void validateField(T value, Predicate<T> validator, String message) {
        if (!validator.test(value)) {
            throw new ValidationException(message);
        }
    }

    private Predicate<Object> validateNotNull() {
        return obj -> obj != null;
    }

    private Predicate<String> validateNotBlank() {
        return str -> !isBlank(str);
    }

    private void validateGoods(@NotNull @Valid GoodsDto goods) {
        validateField(goods.getRefType(), this::validateNotNull, "goods.refType is required");
        validateField(goods.getRefCode(), this::validateNotBlank, "goods.refCode is required");
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
        validateField(dto.getFromWarehouse(), this::validateNotNull, "fromWarehouse is required for IN");
        if (dto.getToWarehouse() != null) {
            throw new ValidationException("toWarehouse must be null for IN");
        }
        if (dto.getCustomsDocument() != null) {
            throw new ValidationException("customsDocument must be null for IN");
        }
    }

    private void validateOutMovement(MovementCreateRequestDto dto) {
        validateField(dto.getToWarehouse(), this::validateNotNull, "toWarehouse is required for OUT");
        if (dto.getFromWarehouse() != null) {
            throw new ValidationException("fromWarehouse must be null for OUT");
        }
        validateField(dto.getCustomsDocument(), this::validateNotNull, "customsDocument is required for OUT");
        validateField(dto.getCustomsDocument().getType(), this::validateNotBlank, "customsDocument.type is required for OUT");
        validateField(dto.getCustomsDocument().getRef(), this::validateNotBlank, "customsDocument.ref is required for OUT");
    }
}
 */
