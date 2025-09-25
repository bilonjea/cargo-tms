package fr.cargo.tms.mapper;

import fr.cargo.tms.contracts.model.CustomsDocumentDto;
import fr.cargo.tms.dao.bean.CustomsDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomsDocumentMapper {
    CustomsDocument toEntity(CustomsDocumentDto dto);
    CustomsDocumentDto toDto(CustomsDocument entity);
}


