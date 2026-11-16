package com.universidad.msb.controller;

import com.universidad.msb.dto.EntityBDtoList;
import com.universidad.msb.dto.EntityBRequest;
import com.universidad.msb.dto.EntityBResponse;
import com.universidad.msb.service.EntityBService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping({"/api/entity-b", "/api/b"})
public class EntityBController {
    private final EntityBService service;
    public EntityBController(EntityBService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<EntityBDtoList>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityBResponse> getById(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntityBResponse> create(@RequestBody EntityBRequest request) {
        if (invalid(request)) return ResponseEntity.badRequest().build();
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityBResponse> update(@PathVariable Long id, @RequestBody EntityBRequest request) {
        if (invalid(request)) return ResponseEntity.badRequest().build();
        return service.update(id, request).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!service.delete(id)) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    private boolean invalid(EntityBRequest request) {
        return request == null || request.getNombreB() == null || request.getNombreB().isBlank()
                || (request.getEntityAIds() != null && request.getEntityAIds().stream()
                        .anyMatch(id -> id == null || id <= 0));
    }
}
