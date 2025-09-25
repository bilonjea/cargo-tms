package fr.cargo.tms.mapper;

import fr.cargo.tms.contracts.model.GoodsDto;
import fr.cargo.tms.dao.bean.Goods;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { EnumMappers.class })
public interface GoodsMapper {
    // MapStruct fera le mapping refType <-> refType via EnumMappers
    Goods toEntity(GoodsDto dto);
    GoodsDto toDto(Goods entity);
}
