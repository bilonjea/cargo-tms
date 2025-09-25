package fr.cargo.tms.dao.repository.projections;

/** Projection d’agrégats pour la dispo / reporting. */
public interface MovementTotals {
    Double getTotalQuantity();
    Double getTotalWeight();
}
