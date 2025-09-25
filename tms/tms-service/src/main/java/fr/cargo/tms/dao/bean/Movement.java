package fr.cargo.tms.dao.bean;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import fr.cargo.tms.dao.bean.enums.MovementType;

import java.time.OffsetDateTime;

@Entity
@Table(name = "movement", schema = "tms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Movement extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false, length = 8)
    private MovementType movementType; // IN / OUT

    @NotNull
    @Column(name = "movement_time", nullable = false)
    private OffsetDateTime movementTime;

    @NotBlank
    @Size(max = 64)
    @Column(name = "created_by", nullable = false, length = 64)
    private String createdBy;

    // Statut douanier (1 lettre)
    @NotBlank
    @Size(min = 1, max = 1)
    @Column(name = "customs_status", nullable = false, length = 1)
    private String customsStatus;

    // declaredIn / from / to
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "declared_in_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_movement_declared_in"))
    private Warehouse declaredIn;

    // Requis si IN (à valider côté service)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_warehouse_id",
            foreignKey = @ForeignKey(name = "fk_movement_from_wh"))
    private Warehouse fromWarehouse;

    // Requis si OUT (à valider côté service)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_warehouse_id",
            foreignKey = @ForeignKey(name = "fk_movement_to_wh"))
    private Warehouse toWarehouse;

    @Embedded
    private Goods goods;

    // Requis si OUT (à valider côté service)
    @Embedded
    private CustomsDocument customsDocument;
}

