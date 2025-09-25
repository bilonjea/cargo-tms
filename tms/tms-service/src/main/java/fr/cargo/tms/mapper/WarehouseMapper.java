package fr.cargo.tms.mapper;

import fr.cargo.tms.contracts.model.WarehouseDto;
import fr.cargo.tms.dao.bean.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    // Pour les sorties (entity -> dto)
    WarehouseDto toDto(Warehouse w);

    // Pour les entrées (dto -> entity) : on ignore id/tech; on ne résout PAS ici (le service fera la résolution via repo)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Warehouse toEntity(WarehouseDto dto);
}

