package fr.cargo.tms.dao.bean;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "warehouse", schema = "tms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Warehouse extends BaseEntity {

    @NotBlank
    @Size(max = 16)
    @Column(nullable = false, length = 16)
    private String code;

    @NotBlank
    @Size(max = 128)
    @Column(nullable = false, length = 128)
    private String label;
}

