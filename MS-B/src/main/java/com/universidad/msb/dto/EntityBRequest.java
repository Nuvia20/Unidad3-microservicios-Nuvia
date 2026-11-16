package com.universidad.msb.dto;

import java.util.List;

// IDs de MS-A a asociar con este registro B.
public class EntityBRequest {
    private String nombreB;
    private List<Long> entityAIds;

    public String getNombreB() { return nombreB; }
    public void setNombreB(String nombreB) { this.nombreB = nombreB; }
    public List<Long> getEntityAIds() { return entityAIds; }
    public void setEntityAIds(List<Long> entityAIds) { this.entityAIds = entityAIds; }
}
