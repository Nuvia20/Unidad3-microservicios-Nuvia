package com.universidad.msb.entity;

import jakarta.persistence.*;

// Tabla intermedia: guarda IDs de A sin conectar directamente a su base de datos.
@Entity
@Table(name = "entity_b_entity_a")
public class EntityBEntityA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_a_id", nullable = false)
    private Long entityAId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_b_id", nullable = false)
    private EntityB entityB;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEntityAId() { return entityAId; }
    public void setEntityAId(Long entityAId) { this.entityAId = entityAId; }
    public EntityB getEntityB() { return entityB; }
    public void setEntityB(EntityB entityB) { this.entityB = entityB; }
}
