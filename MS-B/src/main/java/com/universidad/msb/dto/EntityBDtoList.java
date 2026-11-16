package com.universidad.msb.dto;

// Resultado combinado de MS-B y MS-A para el listado.
public class EntityBDtoList {
    private Long id;
    private String nombreB;
    private String nombreA;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreB() { return nombreB; }
    public void setNombreB(String nombreB) { this.nombreB = nombreB; }
    public String getNombreA() { return nombreA; }
    public void setNombreA(String nombreA) { this.nombreA = nombreA; }
}
