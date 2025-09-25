package fr.cargo.tms.mapper;

import fr.cargo.tms.dao.bean.Warehouse;
import fr.cargo.tms.dao.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WarehouseResolver {
    private final WarehouseRepository repo;

    public Warehouse find(String code) {
        return repo.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Unknown warehouse: " + code));
    }
}
