package fr.cargo.tms.dao.bean;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.OffsetDateTime;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Version
    protected Long version;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    protected OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    protected OffsetDateTime updatedAt = OffsetDateTime.now();

    @PreUpdate
    protected void touch() { this.updatedAt = OffsetDateTime.now(); }
}

