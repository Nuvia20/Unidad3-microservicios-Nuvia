package com.universidad.msb.client;

import com.universidad.msb.dto.EntityADto;
import com.universidad.msb.feignconfig.FeignTokenConfig;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
    name = "MS-A",
    configuration = FeignTokenConfig.class
)
public interface ClientFeignMSA {

    @PostMapping("/api/entity-a/by-ids")
    List<EntityADto> obtenerDTOsDelMSA(
        @RequestBody List<Long> ids
    );
}
