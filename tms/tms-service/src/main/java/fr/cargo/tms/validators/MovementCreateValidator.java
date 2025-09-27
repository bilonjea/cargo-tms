package fr.cargo.tms.validators;

import fr.cargo.tms.contracts.model.*;
import fr.cargo.tms.error.ValidationException;
import fr.cargo.tms.error.Violation;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class MovementCreateValidator {

    private static final Pattern AWB_11_DIGITS = Pattern.compile("\\d{11}");

    public void validate(MovementCreateRequestDto dto) {
        List<Violation> errors = new ArrayList<>();

        // présence globale
        if (dto == null) {
            throw problem(errors);
        }
        require(dto.getMovementType() != null, "movementType", "is required", errors);
        require(dto.getMovementTime() != null, "movementTime", "is required", errors);
        require(!isBlank(dto.getCreatedBy()), "createdBy", "is required", errors);
        require(dto.getDeclaredIn() != null, "declaredIn", "is required", errors);
        require(dto.getGoods() != null, "goods", "is required", errors);
        require(!isBlank(dto.getCustomsStatus()), "customsStatus", "is required", errors);

        // goods
        GoodsDto g = dto.getGoods();
        if (g != null) {
            require(g.getRefType() != null, "goods.refType", "is required", errors);
            require(!isBlank(g.getRefCode()), "goods.refCode", "is required", errors);
            require(g.getQuantity() != null && g.getQuantity() > 0, "goods.quantity", "must be > 0", errors);
            require(g.getWeight() != null && g.getWeight() > 0, "goods.weight", "must be > 0", errors);
            require(g.getTotalQuantity() != null && g.getTotalQuantity() > 0, "goods.totalQuantity", "must be > 0", errors);
            require(g.getTotalWeight() != null && g.getTotalWeight() > 0, "goods.totalWeight", "must be > 0", errors);
            if (g.getQuantity() != null && g.getTotalQuantity() != null && g.getQuantity() > g.getTotalQuantity()) {
                errors.add(new Violation("goods.quantity", "cannot exceed goods.totalQuantity"));
            }
            if (g.getWeight() != null && g.getTotalWeight() != null && g.getWeight() > g.getTotalWeight()) {
                errors.add(new Violation("goods.weight", "cannot exceed goods.totalWeight"));
            }
            if (g.getRefType() == RefTypeDto.AWB && !isBlank(g.getRefCode())
                    && !AWB_11_DIGITS.matcher(g.getRefCode()).matches()) {
                errors.add(new Violation("goods.refCode", "must contain exactly 11 digits when refType=AWB"));
            }
        }

        // customsStatus: une lettre (ajuste si tu passes en enum)
        if (!isBlank(dto.getCustomsStatus()) && !dto.getCustomsStatus().matches("^[A-Za-z]$")) {
            errors.add(new Violation("customsStatus", "must be a single letter"));
        }

        // Cohérence selon le type (switch Java 21)
        MovementTypeDto type = dto.getMovementType();
        if (type != null) {
            switch (type) {
                case IN -> {
                    require(dto.getFromWarehouse() != null, "fromWarehouse", "is required for IN", errors);
                    require(dto.getToWarehouse() == null, "toWarehouse", "must be null for IN", errors);
                    require(dto.getCustomsDocument() == null, "customsDocument", "must be null for IN", errors);
                }
                case OUT -> {
                    require(dto.getToWarehouse() != null, "toWarehouse", "is required for OUT", errors);
                    require(dto.getFromWarehouse() == null, "fromWarehouse", "must be null for OUT", errors);
                    require(dto.getCustomsDocument() != null, "customsDocument", "is required for OUT", errors);
                    CustomsDocumentDto cd = dto.getCustomsDocument();
                    if (cd != null) {
                        require(!isBlank(cd.getType()), "customsDocument.type", "is required for OUT", errors);
                        require(!isBlank(cd.getRef()), "customsDocument.ref", "is required for OUT", errors);
                    }
                }
                default -> errors.add(new Violation("movementType", "unknown value"));
            }
        }

        // lever toutes les erreurs d’un coup
        if (!errors.isEmpty()) {
            var problem = new ProblemDto()
                    .status(400)
                    .title("Validation Error")
                    .detail("Validation failed")
                    .type(URI.create("about:blank"))
                    .putAdditionalProperty("errors", errors);
            throw new ValidationException(problem);
        }
    }

    private static void require(boolean condition, String field, String message, List<Violation> bag) {
        if (!condition) bag.add(new Violation(field, message));
    }

    private static ValidationException problem(List<Violation> errors) {
        var p = new ProblemDto()
                .status(400)
                .title("Validation Error")
                .detail("Request body is required")
                .type(URI.create("about:blank"))
                .putAdditionalProperty("errors", errors);
        return new ValidationException(p);
    }
}

