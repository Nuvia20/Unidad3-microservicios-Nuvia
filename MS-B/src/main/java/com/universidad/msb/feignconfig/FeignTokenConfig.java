package com.universidad.msb.feignconfig;

import feign.RequestInterceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class FeignTokenConfig {

    @Bean
    public RequestInterceptor tokenInterceptor() {

        return template -> {

            RequestAttributes attributes =
                RequestContextHolder.getRequestAttributes();

            if (attributes instanceof ServletRequestAttributes servletAttrs) {

                String authorization = servletAttrs
                    .getRequest()
                    .getHeader("Authorization");

                if (authorization != null &&
                    authorization.startsWith("Bearer ")) {

                    template.header("Authorization", authorization);
                }
            }
        };
    }
}
