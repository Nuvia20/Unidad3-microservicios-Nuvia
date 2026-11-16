package com.universidad.msa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "entidad_a") // Conserva la tabla existente de H2.
public class EntityA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombreA;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreA() { return nombreA; }
    public void setNombreA(String nombreA) { this.nombreA = nombreA; }
}
