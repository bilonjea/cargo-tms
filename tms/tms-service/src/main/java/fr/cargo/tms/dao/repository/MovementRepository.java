package fr.cargo.tms.dao.repository;

import fr.cargo.tms.dao.bean.Movement;
import fr.cargo.tms.dao.bean.Warehouse;
import fr.cargo.tms.dao.bean.enums.MovementType;
import fr.cargo.tms.dao.bean.enums.RefType;
import fr.cargo.tms.dao.repository.projections.MovementTotals;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovementRepository extends JpaRepository<Movement, Long> {

    // Derniers mouvements (timeline)
    List<Movement> findTop50ByOrderByMovementTimeDesc();

    Page<Movement> findAllByOrderByMovementTimeDesc(Pageable pageable);

    // Recherche par référence marchandise (refType + refCode)
    Page<Movement> findByGoodsRefTypeAndGoodsRefCodeOrderByMovementTimeDesc(
            RefType refType, String refCode, Pageable pageable);

    Optional<Movement> findFirstByGoodsRefTypeAndGoodsRefCodeOrderByMovementTimeDesc(
            RefType refType, String refCode);

    // Filtres fréquents
    Page<Movement> findByMovementType(MovementType type, Pageable pageable);

    @Query("""
        select m
        from Movement m
        where m.declaredIn = :declaredIn
          and m.movementTime between :from and :to
        order by m.movementTime desc
    """)
    Page<Movement> findByDeclaredInAndMovementTimeBetween(@Param("declaredIn") Warehouse declaredIn,
                                                          @Param("from") OffsetDateTime from,
                                                          @Param("to")  OffsetDateTime to,
                                                          Pageable pageable);

    // Agrégats pour disponibilité / reporting
    @Query("""
           select sum(m.goods.totalQuantity) as totalQuantity,
                  sum(m.goods.totalWeight)    as totalWeight
             from Movement m
            where m.goods.refType = :refType
              and m.goods.refCode = :refCode
              and m.movementType  = :movementType
           """)
    Optional<MovementTotals> sumTotalsByTypeAndRef(@Param("movementType") MovementType movementType,
                                                   @Param("refType") RefType refType,
                                                   @Param("refCode") String refCode);

    // Variante : sur une période
    @Query("""
           select sum(m.goods.totalQuantity) as totalQuantity,
                  sum(m.goods.totalWeight)    as totalWeight
             from Movement m
            where m.goods.refType = :refType
              and m.goods.refCode = :refCode
              and m.movementType  = :movementType
              and m.movementTime between :from and :to
           """)
    Optional<MovementTotals> sumTotalsByTypeAndRefBetween(@Param("movementType") MovementType movementType,
                                                          @Param("refType") RefType refType,
                                                          @Param("refCode") String refCode,
                                                          @Param("from") OffsetDateTime from,
                                                          @Param("to") OffsetDateTime to);
}

