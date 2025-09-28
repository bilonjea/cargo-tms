package fr.cargo.tms.mapper;

import fr.cargo.tms.contracts.model.WarehouseDto;
import fr.cargo.tms.dao.bean.Warehouse;
import fr.cargo.tms.dao.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WarehouseResolver {
    private final WarehouseRepository repo;

    public Warehouse find(WarehouseDto warehouseDto) {
        return repo.findByCode(warehouseDto.getCode())
                .orElseThrow(() -> new IllegalArgumentException("Unknown warehouse: " + warehouseDto.getCode()+"-"+warehouseDto.getLabel()));
    }
}
