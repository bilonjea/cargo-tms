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
import fr.cargo.tms.validators.MovementBusinessRulesValidator;
import fr.cargo.tms.validators.MovementCreateValidator;
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
    private final MovementCreateValidator validator;
    private final MovementBusinessRulesValidator businessValidator;


    @Override
    @Transactional
    public MovementResponseDto createMovement(MovementCreateRequestDto dto) {
        validator.validate(dto);
        businessValidator.validate(dto);

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

}
