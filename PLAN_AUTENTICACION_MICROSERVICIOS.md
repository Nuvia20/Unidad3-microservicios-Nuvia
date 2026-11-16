# Planeación — Autenticación con Spring Authorization Server + Resource Server

## 1. Objetivo
Proteger los microservicios **MS-A** y **MS-B** (y el frontend) usando **Spring Authorization Server** (emisor de tokens) y **Resource Server** (validación de JWT), integrados con el **Gateway** existente y **Eureka**.

**Entregable:** pantallazos de las configuraciones y de la ejecución de la protección, subidos al trabajo del equipo.

## 2. Integrantes del equipo (6) y reparto de trabajo
| # | Integrante | Fase asignada |
|---|---|---|
| 1 | **Elian** | Fase 1 — Crear el AuthServer |
| 2 | **Nuvia** | Fase 2 — MS-A como Resource Server |
| 3 | **Aldair** | Fase 3 — MS-B como Resource Server + Feign con token |
| 4 | **Fernanda** | Fase 4 — Gateway: rutas, CORS y propagación |
| 5 | **Ariadna** | Fase 5 — Frontend con OAuth2 |
| 6 | **Daniel** | Fase 6 — Integración end-to-end, pantallazos y reporte |

## 3. Arquitectura resultante
```
Frontend (login + llamadas)  [Ariadna]
        │  (OAuth2, vía Gateway)
        ▼
API Gateway (webmvc, puerto 8080)  [Fernanda]
   ├─ /oauth2/**  ──────────► AuthServer (NUEVO, puerto 9000)  [Elian]
   ├─ /api/ENTITYA/** ──────► MS-A (8081)  Resource Server  [Nuvia]
   └─ /api/ENTITYB/** ──────► MS-B (8082)  Resource Server  [Aldair]
                                   ▲
        Eureka (8761) ← registran todos los servicios
                                   ▲
        Daniel: arranca todo, valida el flujo completo y documenta
```

- **AuthServer** (proyecto NUEVO): emite JWT (grant types `password` y `authorization_code`).
- **MS-A y MS-B**: se convierten en **Resource Servers** (validan el JWT, exigen autenticación).
- **Gateway**: punto único de entrada; reenvía el header `Authorization` y habilita CORS para el frontend.
- **Feign (MS-B → MS-A)**: propaga el token del request entrante al request saliente (`RequestInterceptor`).

## 4. Requisitos técnicos
- Spring Boot **3.x** (Spring Authorization Server lo exige) + Java 17/21.
- Dependencias a agregar:
  - **AuthServer:** `spring-boot-starter-oauth2-authorization-server`, `spring-boot-starter-web`, `spring-boot-starter-security`
  - **MS-A / MS-B:** `spring-boot-starter-oauth2-resource-server`
- Reglas de seguridad:
  - Endpoints CRUD de MS-A/MS-B: `authenticated` (rol `USER` mínimo).
  - Permitidos sin token: `/actuator/health`, `/h2-console` (solo MS-B).
  - AuthServer: usuarios en memoria (`InMemoryUserDetailsManager`) + `PasswordEncoder`.

## 5. Fases de trabajo (carga equivalente, ~2–2.5 h c/u)

### Fase 1 — ELIAN: Crear el AuthServer (~2 h)
1. Nuevo proyecto Maven `AuthServer` (puerto **9000**, `spring.application.name=auth-server`).
2. Configurar `OAuth2AuthorizationServerConfiguration`:
   - `JwtDecoder` (issuer `http://localhost:9000`), firma RSA.
   - `InMemoryUserDetailsManager` (usuario `user/pass123`, rol `USER`).
   - Cliente OAuth2 `frontend` (grant types: `password`, `authorization_code`; scope `read`).
3. Registrar en Eureka: `@EnableDiscoveryClient` + `eureka.client.service-url.defaultZone=http://localhost:8761/eureka/`.
4. **Entregables (pantallazos):**
   - Consola de arranque del AuthServer.
   - Página de login de Spring Security.
   - Token obtenido con Postman (`POST /oauth2/token`).

### Fase 2 — NUVIA: MS-A como Resource Server (~2 h)
1. Agregar `spring-boot-starter-oauth2-resource-server` al `pom.xml` de MS-A.
2. Configurar validación de JWT: `spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:9000` (o `SecurityFilterChain` en Java con `JwtAuthenticationConverter`).
3. Probar en Postman:
   - `GET http://localhost:8081/api/ENTITYA` **sin token** → **401**.
   - `GET http://localhost:8081/api/ENTITYA` **con token** → **200**.
4. **Entregables (pantallazos):**
   - `application.properties` / clase de seguridad modificadas.
   - Resultado 401 y resultado 200.

### Fase 3 — ALDAIR: MS-B como Resource Server + Feign con token (~2.5 h)
1. Misma configuración de Resource Server que en la Fase 2 (aplicada a MS-B).
2. **Propagación del token en Feign:** crear `RequestInterceptor` en `ClientFeignMSA` que copia el header `Authorization` del request actual (`RequestContextHolder`) al request saliente hacia MS-A.
3. Probar:
   - Endpoint de MS-B **sin token** → **401**.
   - Endpoint de MS-B **con token** → **200** (incluye la llamada interna Feign a MS-A, que también debe autorizarse).
4. **Entregables (pantallazos):**
   - Config de seguridad de MS-B.
   - Código del `RequestInterceptor`.
   - Resultados 401 y 200 vía MS-B.

### Fase 4 — FERNANDA: Gateway: rutas, CORS y propagación (~2 h)
1. Agregar la ruta al AuthServer en `application.yml`:
   ```yaml
   - id: auth-server
     uri: lb://AUTH-SERVER
     predicates:
       - Path=/oauth2/**
   ```
2. Verificar/documentar que el Gateway reenvía el header `Authorization` (por defecto lo hace).
3. Configurar **CORS** para el frontend (origen permitido, header `Authorization`).
4. Probar el flujo completo **por Gateway** (puerto 8080):
   - `GET http://localhost:8080/api/ENTITYA` **con token** → **200**.
   - `GET http://localhost:8080/api/ENTITYA` **sin token** → **401**.
5. **Entregables (pantallazos):**
   - `application.yml` del Gateway actualizado.
   - Prueba autorizada y denegada vía Gateway.

### Fase 5 — ARIADNA: Frontend con OAuth2 (~2.5 h)
1. Frontend mínimo (HTML/JS o SPA):
   - Botón "Login" → redirige a `http://localhost:8080/oauth2/authorize` (Authorization Code + PKCE) o flujo `password` para simplificar.
   - Guarda el token y lo envía en `Authorization: Bearer <token>` en las llamadas al Gateway.
   - Muestra la lista de ENTITYA y ENTITYB.
2. **Entregables (pantallazos):**
   - Pantalla de login.
   - Token obtenido.
   - Datos listados desde el frontend.
   - Error 401 sin token / token expirado.

### Fase 6 — DANIEL: Integración end-to-end + reporte (~2 h)
1. Arrancar en orden: **Eureka → AuthServer → MS-A → MS-B → Gateway → Frontend**.
2. Verificar en el dashboard de Eureka (`http://localhost:8761`) que los 4 servicios aparecen registrados.
3. Capturar **todas** las pantallas del flujo completo: login → token → datos vía Gateway → 401 sin token.
4. Redactar el reporte final:
   - Arquitectura diagramada.
   - Tabla de dependencias agregadas por proyecto.
   - Listado numerado de todos los pantallazos.
   - Conclusiones.

## 6. Orden de ejecución y dependencias
```
Elian (F1) ──┬───────────────┐
             │                │
Nuvia (F2) ──┼──► Fernanda (F4) ──► Ariadna (F5) ──► Daniel (F6)
Aldair (F3) ─┘
```
- **F2 y F3 dependen de F1** (necesitan el issuer para validar los JWT).
- **F4, F5 y F6** son secuenciales al final.
- **Paralelizable:** mientras Elian termina F1, Nuvia y Aldair preparan dependencias y código en F2 y F3.

## 7. Criterio de aceptación (checklist final)
- [ ] AuthServer emite JWT (pantallazo de token).
- [ ] MS-A y MS-B rechazan peticiones sin token: 401 (pantallazo).
- [ ] MS-A y MS-B aceptan peticiones con token válido: 200 (pantallazo).
- [ ] Feign de MS-B → MS-A funciona propagando el token (pantallazo).
- [ ] Todo funciona a través del Gateway con una sola URL (pantallazo).
- [ ] Frontend logra login y consulta datos (pantallazos).
- [ ] Los 4 servicios visibles en el dashboard de Eureka (pantallazo).
- [ ] Reporte con configuraciones y pantallazos subido.

## 8. Riesgos y mitigaciones
| Riesgo | Mitigación |
|---|---|
| MS-A/MS-B en Spring Boot 2.x (no soporta Authorization Server) | Actualizar a Spring Boot 3.x y ajustar compatibilidad (H2, MySQL, Feign) |
| Feign no propaga el token → 401 en MS-A | `RequestInterceptor` (Fase 3, Aldair) |
| CORS bloquea al frontend | Configurar `CorsConfigurationSource` en Gateway (Fase 4, Fernanda) |
| Puerto 9000 en uso | Cambiar puerto del AuthServer y actualizar `issuer-uri` en MS-A/MS-B |
| Eureka de MS-A apunta a `10.13.29.99` | Unificar a `localhost:8761` para la práctica local |
