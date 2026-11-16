package com.universidad.msa.controller;

import com.universidad.msa.dto.EntityADto;
import com.universidad.msa.dto.EntityARequest;
import com.universidad.msa.service.EntityAService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/entity-a", "/api/a", "/api/ENTITYA"}) // "/api/ENTITYA" coincide con la ruta del Gateway (Fase 4) y con las pruebas de la Fase 2.
public class EntityAController {
    private final EntityAService service;

    public EntityAController(EntityAService service) { this.service = service; }

    @GetMapping
    public List<EntityADto> getAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<EntityADto> getById(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/by-ids")
    public ResponseEntity<List<EntityADto>> getAllByIds(@RequestBody List<Long> ids) {
        return ResponseEntity.ok(service.findAllByIDs(ids));
    }

    @PostMapping
    public ResponseEntity<EntityADto> create(@RequestBody EntityARequest request) {
        if (invalid(request)) return ResponseEntity.badRequest().build();
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityADto> update(@PathVariable Long id, @RequestBody EntityARequest request) {
        if (invalid(request)) return ResponseEntity.badRequest().build();
        return service.update(id, request).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!service.delete(id)) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    private boolean invalid(EntityARequest request) {
        return request == null || request.getNombreA() == null || request.getNombreA().isBlank();
    }
}
