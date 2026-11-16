# MS-A - Microservicio A

Proyecto sencillo de Java, Spring Boot, Spring Data JPA, H2 y Eureka.

## Estructura
- `entity/EntidadA.java`: representa la tabla `entidad_a` con `id` y `nombreA`.
- `dto/`: define los datos de entrada (Request) y salida (Response).
- `repository/`: permite guardar, buscar y eliminar registros.
- `service/`: realiza las operaciones del CRUD.
- `controller/`: expone las rutas HTTP.

## Requisitos
- JDK 17 o superior.
- Maven instalado (o usar `mvnw.cmd` en Windows).
- Servidor Eureka ya ejecutándose en `http://localhost:8761`.

## Ejecutar en orden
1. Iniciar **Eureka Server** (proyecto separado del profesor). Confirmar `http://localhost:8761`.
2. Abrir la carpeta MS-A en VS Code.
3. Ejecutar desde PowerShell: `./mvnw.cmd spring-boot:run` (o `mvn spring-boot:run`).
4. Comprobar que MS-A aparezca registrado en Eureka.
5. Enviar peticiones desde Thunder Client o Postman.

## Rutas de la API
- `GET /api/ENTITYA` y `GET /api/ENTITYA/{id}` son las rutas que usa el **Gateway** (Fase 4) y las pruebas de la **Fase 2** (Nuvia). El controller también acepta `/api/entity-a` y `/api/a` por compatibilidad.

## URLs de pruebas (puerto 81)
- API: `http://localhost:81/api/a`
- Consola H2: `http://localhost:81/h2-console`
- Eureka: `http://localhost:8761`

**Ojo:** La consigna dice puerto 81 y también menciona la consola H2 en 8081. Ambos no son iguales: esta entrega usa **81**. Si el profesor requiere **8081**, cambiar `server.port=81` a `server.port=8081` en `application.properties`; entonces la API y H2 se consultan por 8081.

## Conexión a H2
En la consola H2 usar:
- JDBC URL: `jdbc:h2:file:./data/msa_db`
- User Name: `sa`
- Password: dejar vacío.

Consulta SQL: `SELECT * FROM ENTIDAD_A;`

## Pruebas CRUD en Thunder Client
Seleccionar `Body > JSON` y `Content-Type: application/json` para POST y PUT.

| Prueba | Método | URL | JSON |
|---|---|---|---|
| Listar | GET | `http://localhost:81/api/a` | — |
| Crear | POST | `http://localhost:81/api/a` | `{"nombreA":"Ejemplo A"}` |
| Buscar por ID | GET | `http://localhost:81/api/a/1` | — |
| Editar | PUT | `http://localhost:81/api/a/1` | `{"nombreA":"Ejemplo actualizado"}` |
| Eliminar | DELETE | `http://localhost:81/api/a/1` | — |

El ID `1` es un ejemplo: usar el que devuelva POST. Respuestas esperadas: GET 200, POST 201, PUT 200, DELETE 204; si no existe, 404.

## Evidencias para entregar
Tomar capturas separadas de POST, GET (lista y por ID), PUT y DELETE, mostrando la petición y la respuesta. Tras **cada prueba**, abrir H2, ejecutar `SELECT * FROM ENTIDAD_A;` y tomar otra captura de la tabla. Después de DELETE debe desaparecer el registro. Mostrar MS-A registrado en Eureka.

> Los registros se crean manualmente mediante la API; no se cargan datos automáticos. H2 utiliza archivo local y conserva registros tras reiniciar.
