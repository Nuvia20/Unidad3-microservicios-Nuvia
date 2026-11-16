package com.universidad.msb.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "entity_b") // Conserva la tabla previa de MS-B.
public class EntityB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombreB;

    @OneToMany(mappedBy = "entityB", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntityBEntityA> listaConEntityA = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreB() { return nombreB; }
    public void setNombreB(String nombreB) { this.nombreB = nombreB; }
    public List<EntityBEntityA> getListaConEntityA() { return listaConEntityA; }

    public void reemplazarRelaciones(List<Long> ids) {
        listaConEntityA.clear();
        if (ids == null) return;
        for (Long idA : ids) {
            EntityBEntityA relacion = new EntityBEntityA();
            relacion.setEntityAId(idA);
            relacion.setEntityB(this);
            listaConEntityA.add(relacion);
        }
    }
}
