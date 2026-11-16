package com.universidad.msb.dto;

import java.util.List;

public class EntityBResponse {
    private Long id;
    private String nombreB;
    private List<Long> entityAIds;

    public EntityBResponse(Long id, String nombreB, List<Long> entityAIds) {
        this.id = id;
        this.nombreB = nombreB;
        this.entityAIds = entityAIds;
    }
    public Long getId() { return id; }
    public String getNombreB() { return nombreB; }
    public List<Long> getEntityAIds() { return entityAIds; }
}
