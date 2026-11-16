package com.universidad.msa.dto;

// Datos de A que se comparten con MS-B.
public class EntityADto {
    private Long id;
    private String nombreA;

    public EntityADto() {}
    public EntityADto(Long id, String nombreA) {
        this.id = id;
        this.nombreA = nombreA;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreA() { return nombreA; }
    public void setNombreA(String nombreA) { this.nombreA = nombreA; }
}
