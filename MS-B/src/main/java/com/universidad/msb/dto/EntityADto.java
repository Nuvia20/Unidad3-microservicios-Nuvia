package com.universidad.msb.dto;

// Copia ligera del DTO que MS-A devuelve por HTTP.
public class EntityADto {
    private Long id;
    private String nombreA;
    public EntityADto() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreA() { return nombreA; }
    public void setNombreA(String nombreA) { this.nombreA = nombreA; }
}
