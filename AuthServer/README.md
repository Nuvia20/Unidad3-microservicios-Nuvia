# AuthServer (Fase 1 — Elian)

Authorization Server OAuth2 (Spring Authorization Server) para el proyecto de microservicios.

- Puerto: `9000`
- Nombre en Eureka: `auth-server`
- Issuer JWT: `http://localhost:9000` (firma RSA, clave publicada en `/oauth2/jwks`)

## Arranque

1. Arrancar `EurekaServer` (puerto 8761).
2. Arrancar `AuthServer`:
   ```bash
   ./mvnw spring-boot:run
   ```

## Credenciales

| Dato | Valor |
|---|---|
| Usuario / contraseña | `user` / `pass123` (rol USER) |
| client_id / client_secret | `frontend` / `frontend-secret` |
| Scopes disponibles | `read` |
| Grant types | `password`, `authorization_code` |

## Pruebas

### 1. Página de login
Abrir `http://localhost:9000/login` y entrar con `user` / `pass123`.

### 2. Token con grant `password` (Postman)
```
POST http://localhost:9000/oauth2/token
Authorization: Basic base64(frontend:frontend-secret)
Content-Type: application/x-www-form-urlencoded

grant_type=password&username=user&password=pass123&scope=read
```

### 3. JWKS (para los resource servers)
```
GET http://localhost:9000/oauth2/jwks
```

### 4. Flujo authorization_code
```
GET http://localhost:9000/oauth2/authorize
    ?response_type=code
    &client_id=frontend
    &scope=read
    &redirect_uri=http://localhost:3000/callback
```
(Requiere sesión: iniciar sesión antes en `/login`.)

## Notas

- El grant type `password` NO es nativo de Spring Authorization Server; se implementa
  con un `TokenEndpoint` personalizado (`AuthorizationServerConfig#tokenEndpoint`) que
  valida el usuario y emite un JWT firmado con la misma clave RSA de `/oauth2/jwks`.
- El JWT incluye los claims estándar: `sub`, `iss`, `aud`, `exp`, `iat`, `scp`, `client_id`.
