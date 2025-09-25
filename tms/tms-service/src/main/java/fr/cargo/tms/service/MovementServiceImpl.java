package fr.cargo.tms.service;

import fr.cargo.tms.contracts.model.*;
import fr.cargo.tms.dao.bean.Movement;
import fr.cargo.tms.dao.bean.enums.MovementType;
import fr.cargo.tms.dao.bean.enums.RefType;
import fr.cargo.tms.dao.repository.MovementRepository;
import fr.cargo.tms.dao.repository.projections.MovementTotals;
import fr.cargo.tms.error.BusinessRuleException;
import fr.cargo.tms.events.MovementCreatedEvent;
import fr.cargo.tms.mapper.MovementMapper;
import fr.cargo.tms.mapper.WarehouseResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovementServiceImpl implements MovementService {

    private final MovementRepository movementRepository;
    private final MovementMapper movementMapper;
    private final WarehouseResolver warehouseResolver;
    private final ApplicationEventPublisher publisher;

    @Override
    @Transactional
    public MovementResponseDto createMovement(MovementCreateRequestDto dto) {
        validateBusinessRules(dto);

        Movement entity = movementMapper.toEntity(dto, warehouseResolver);
        Movement saved = movementRepository.save(entity);

        publisher.publishEvent(new MovementCreatedEvent(saved.getId(), movementMapper.toDto(saved)));

        return movementMapper.toResponseDto(saved);
    }

    @Override
    public List<MovementListItemDto> listMovements(int limit) {
        int effective = Math.min(Math.max(limit, 1), 200);
        return movementRepository
                .findAllByOrderByMovementTimeDesc(PageRequest.of(0, effective))
                .map(movementMapper::toListItemDto)
                .getContent();
    }

    private void validateBusinessRules(MovementCreateRequestDto dto) {
        if (dto.getGoods() != null && dto.getGoods().getRefType() == RefTypeDto.AWB) {
            String ref = dto.getGoods().getRefCode();
            if (ref == null || !ref.matches("\\d{11}")) {
                throw new BusinessRuleException("AWB reference must contain exactly 11 digits");
            }
        }
        if (dto.getMovementType() == MovementTypeDto.IN) {
            double q  = dto.getGoods().getQuantity();
            double tq = dto.getGoods().getTotalQuantity();
            double w  = dto.getGoods().getWeight();
            double tw = dto.getGoods().getTotalWeight();
            if (q > tq) throw new BusinessRuleException("IN quantity cannot exceed total quantity");
            if (w > tw) throw new BusinessRuleException("IN weight cannot exceed total weight");
        }
        if (dto.getMovementType() == MovementTypeDto.OUT) {
            var refType = RefType.valueOf(dto.getGoods().getRefType().name());
            var refCode = dto.getGoods().getRefCode();

            MovementTotals inTotals = movementRepository
                    .sumTotalsByTypeAndRef(MovementType.IN, refType, refCode)
                    .orElse(null);
            MovementTotals outTotals = movementRepository
                    .sumTotalsByTypeAndRef(MovementType.OUT, refType, refCode)
                    .orElse(null);

            double inQty = inTotals != null && inTotals.getTotalQuantity() != null ? inTotals.getTotalQuantity() : 0d;
            double outQty = outTotals != null && outTotals.getTotalQuantity() != null ? outTotals.getTotalQuantity() : 0d;
            double inWgt = inTotals != null && inTotals.getTotalWeight() != null ? inTotals.getTotalWeight() : 0d;
            double outWgt = outTotals != null && outTotals.getTotalWeight() != null ? outTotals.getTotalWeight() : 0d;

            if (dto.getGoods().getQuantity() > (inQty - outQty)) {
                throw new BusinessRuleException("OUT quantity exceeds available stock");
            }
            if (dto.getGoods().getWeight() > (inWgt - outWgt)) {
                throw new BusinessRuleException("OUT weight exceeds available stock");
            }
        }
    }
}
