package com.universidad.msb.client;

import com.universidad.msb.dto.EntityADto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@FeignClient(name = "MS-A") // Eureka resuelve el nombre MS-A.
public interface ClientFeignMSA {
    @PostMapping("/api/entity-a/by-ids")
    List<EntityADto> obtenerDTOsDelMSA(@RequestBody List<Long> ids);
}
