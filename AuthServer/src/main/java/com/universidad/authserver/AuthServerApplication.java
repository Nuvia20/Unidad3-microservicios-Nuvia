package com.universidad.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * AuthServer (Fase 1) - Elian
 *
 * Servidor de autorización OAuth2 (Spring Authorization Server) que emite
 * tokens JWT firmados con RSA. Se registra en Eureka automáticamente
 * (spring-cloud-starter-netflix-eureka-client, @EnableDiscoveryClient).
 *
 * Puerto: 9000
 * Issuer: http://localhost:9000
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }
}
