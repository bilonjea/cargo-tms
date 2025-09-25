package fr.cargo.tms.mapper;

import fr.cargo.tms.contracts.model.MovementTypeDto;
import fr.cargo.tms.contracts.model.RefTypeDto;
import fr.cargo.tms.dao.bean.enums.MovementType;
import fr.cargo.tms.dao.bean.enums.RefType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnumMappers {
    // MovementType
    MovementType toEntity(MovementTypeDto dto);
    MovementTypeDto toDto(MovementType entity);

    // RefType
    RefType toEntity(RefTypeDto dto);
    RefTypeDto toDto(RefType entity);
}
