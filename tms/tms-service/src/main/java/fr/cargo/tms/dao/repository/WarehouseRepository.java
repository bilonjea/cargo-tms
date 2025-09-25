package fr.cargo.tms.dao.repository;

import fr.cargo.tms.dao.bean.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    Optional<Warehouse> findByCode(String code);

    boolean existsByCode(String code);

    List<Warehouse> findAllByCodeIn(Collection<String> codes);

    List<Warehouse> findByLabelContainingIgnoreCase(String labelPart);
}
