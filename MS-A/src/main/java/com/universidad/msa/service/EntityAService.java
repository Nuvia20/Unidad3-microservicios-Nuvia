package com.universidad.msa.service;

import com.universidad.msa.dto.EntityADto;
import com.universidad.msa.dto.EntityARequest;
import com.universidad.msa.entity.EntityA;
import com.universidad.msa.repository.EntityARepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EntityAService {
    private final EntityARepository repository;

    public EntityAService(EntityARepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<EntityADto> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Optional<EntityADto> findById(Long id) {
        return repository.findById(id).map(this::toDto);
    }

    // Endpoint requerido por el profesor para consultar varios IDs en una petición.
    @Transactional(readOnly = true)
    public List<EntityADto> findAllByIDs(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return repository.findAllById(ids).stream().map(this::toDto).toList();
    }

    @Transactional
    public EntityADto create(EntityARequest request) {
        EntityA entity = new EntityA();
        entity.setNombreA(request.getNombreA());
        return toDto(repository.save(entity));
    }

    @Transactional
    public Optional<EntityADto> update(Long id, EntityARequest request) {
        return repository.findById(id).map(entity -> {
            entity.setNombreA(request.getNombreA());
            return toDto(repository.save(entity));
        });
    }

    @Transactional
    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }

    private EntityADto toDto(EntityA entity) {
        return new EntityADto(entity.getId(), entity.getNombreA());
    }
}
