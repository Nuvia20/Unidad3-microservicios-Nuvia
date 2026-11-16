package com.universidad.msb.service;

import com.universidad.msb.client.ClientFeignMSA;
import com.universidad.msb.dto.EntityADto;
import com.universidad.msb.dto.EntityBDtoList;
import com.universidad.msb.dto.EntityBRequest;
import com.universidad.msb.dto.EntityBResponse;
import com.universidad.msb.entity.EntityB;
import com.universidad.msb.entity.EntityBEntityA;
import com.universidad.msb.repository.EntityBRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EntityBService {
    private final EntityBRepository repository;
    private final ClientFeignMSA clientFeignMSA;

    public EntityBService(EntityBRepository repository, ClientFeignMSA clientFeignMSA) {
        this.repository = repository;
        this.clientFeignMSA = clientFeignMSA;
    }

    // Lee MS-B y pide a MS-A los nombres asociados, usando Eureka + Feign.
    @Transactional(readOnly = true)
    public List<EntityBDtoList> findAll() {
        List<EntityB> datosB = repository.findAll();
        List<Long> idsRelacionados = datosB.stream()
                .flatMap(b -> b.getListaConEntityA().stream())
                .map(EntityBEntityA::getEntityAId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> nombresA = new HashMap<>();
        if (!idsRelacionados.isEmpty()) {
            List<EntityADto> listaA = clientFeignMSA.obtenerDTOsDelMSA(idsRelacionados);
            if (listaA != null) {
                for (EntityADto dto : listaA) {
                    if (dto != null && dto.getId() != null) {
                        nombresA.put(dto.getId(), dto.getNombreA());
                    }
                }
            }
        }

        return datosB.stream().map(b -> {
            EntityBDtoList dto = new EntityBDtoList();
            dto.setId(b.getId());
            dto.setNombreB(b.getNombreB());
            if (!b.getListaConEntityA().isEmpty()) {
                Long primerIdA = b.getListaConEntityA().get(0).getEntityAId();
                dto.setNombreA(nombresA.getOrDefault(primerIdA, "Sin dato de MSA"));
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<EntityBResponse> findById(Long id) {
        return repository.findById(id).map(this::toDto);
    }

    @Transactional
    public EntityBResponse create(EntityBRequest request) {
        EntityB b = new EntityB();
        b.setNombreB(request.getNombreB());
        b.reemplazarRelaciones(request.getEntityAIds());
        return toDto(repository.save(b));
    }

    @Transactional
    public Optional<EntityBResponse> update(Long id, EntityBRequest request) {
        return repository.findById(id).map(b -> {
            b.setNombreB(request.getNombreB());
            if (request.getEntityAIds() != null) {
                b.reemplazarRelaciones(request.getEntityAIds());
            }
            return toDto(repository.save(b));
        });
    }

    @Transactional
    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }

    private EntityBResponse toDto(EntityB b) {
        List<Long> ids = b.getListaConEntityA().stream()
                .map(EntityBEntityA::getEntityAId).toList();
        return new EntityBResponse(b.getId(), b.getNombreB(), ids);
    }
}
