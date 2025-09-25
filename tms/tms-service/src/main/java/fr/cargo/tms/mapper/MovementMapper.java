package fr.cargo.tms.mapper;

import fr.cargo.tms.contracts.model.*;
import fr.cargo.tms.dao.bean.Movement;
import fr.cargo.tms.dao.bean.enums.MovementType;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = { EnumMappers.class, WarehouseMapper.class, GoodsMapper.class, CustomsDocumentMapper.class }
)
public interface MovementMapper {

    // -------- DTO -> Entity (create) --------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    // movementType: MovementTypeDto -> MovementType via EnumMappers
    @Mapping(target = "movementType", source = "movementType")
    @Mapping(target = "declaredIn", ignore = true)
    @Mapping(target = "fromWarehouse", ignore = true)
    @Mapping(target = "toWarehouse", ignore = true)
    Movement toEntity(MovementCreateRequestDto dto, @Context WarehouseResolver resolver);

    @AfterMapping
    default void resolveWarehouses(MovementCreateRequestDto dto,
                                   @MappingTarget Movement target,
                                   @Context WarehouseResolver resolver) {
        if (dto.getDeclaredIn() != null && dto.getDeclaredIn().getCode() != null) {
            target.setDeclaredIn(resolver.find(dto.getDeclaredIn().getCode()));
        }
        if (dto.getFromWarehouse() != null && dto.getFromWarehouse().getCode() != null) {
            target.setFromWarehouse(resolver.find(dto.getFromWarehouse().getCode()));
        }
        if (dto.getToWarehouse() != null && dto.getToWarehouse().getCode() != null) {
            target.setToWarehouse(resolver.find(dto.getToWarehouse().getCode()));
        }
    }

    // -------- Entity -> Response/ListItem --------
    @Mapping(target = "id", expression = "java(src.getId() != null ? String.valueOf(src.getId()) : null)")
    // movementType: MovementType -> MovementTypeDto via EnumMappers
    @Mapping(target = "movementType", source = "movementType")
    MovementResponseDto toResponseDto(Movement src);

    @Mapping(target = "id", expression = "java(src.getId() != null ? String.valueOf(src.getId()) : null)")
    @Mapping(target = "movementType", source = "movementType")
    MovementListItemDto toListItemDto(Movement src);

    @Mapping(target = "movementType", source = "movementType")
    MovementDto toDto(Movement entity);

    List<MovementDto> toDto(List<Movement> entities);
}
