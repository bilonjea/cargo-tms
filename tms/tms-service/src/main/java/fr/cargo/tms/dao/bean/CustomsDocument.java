package fr.cargo.tms.dao.bean;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomsDocument {

    @NotBlank
    @Size(max = 16)
    @Column(name = "customs_doc_type", length = 16)
    private String type;

    @NotBlank
    @Size(max = 64)
    @Column(name = "customs_doc_ref", length = 64)
    private String ref;
}

