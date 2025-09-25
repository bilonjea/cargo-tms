package fr.cargo.tms.dao.bean;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import fr.cargo.tms.dao.bean.enums.RefType;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goods {

    @Enumerated(EnumType.STRING)
    @Column(name = "goods_ref_type", nullable = false, length = 16)
    private RefType refType;

    @NotBlank
    @Size(max = 64)
    // Note: la règle AWB=11 chiffres est revalidée côté service.
    @Pattern(regexp = "^(\\d{11})|(.+)$",
            message = "Si refType=AWB alors refCode doit comporter exactement 11 chiffres")
    @Column(name = "goods_ref_code", nullable = false, length = 64)
    private String refCode;

    @NotNull
    @PositiveOrZero
    @Column(name = "goods_quantity", nullable = false)
    private Double quantity;

    @NotNull
    @PositiveOrZero
    @Column(name = "goods_weight", nullable = false)
    private Double weight;

    @NotNull
    @PositiveOrZero
    @Column(name = "goods_total_quantity", nullable = false)
    private Double totalQuantity;

    @NotNull
    @PositiveOrZero
    @Column(name = "goods_total_weight", nullable = false)
    private Double totalWeight;

    @Size(max = 256)
    @Column(name = "goods_description", length = 256)
    private String description;
}

