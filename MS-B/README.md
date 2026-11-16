# MS-B — Microservicio B (Spring Boot + H2 + Eureka)

Proyecto equivalente a MS-A. Cambios principales:
- Entidad **EntityB** con campos `id` y `nombreB`.
- Paquetes `controller`, `dto`, `entity`, `repository`, `service` bajo `com.universidad.msb`.
- Nombre en Eureka: `MS-B`.
- Puerto: **82** (MS-A usa 81, Eureka 8761).
- Base de datos H2: **msbDB**, URL `jdbc:h2:file:./data/msbDB`.
- API: `http://localhost:82/api/b`.

## Rutas de la API
- `GET /api/ENTITYB` y `GET /api/ENTITYB/{id}` son las rutas que usa el **Gateway** (Fase 4). El controller también acepta `/api/entity-b` y `/api/b` por compatibilidad.

## Ejecución en Windows (PowerShell)

1. Inicia Eureka Server (`http://localhost:8761`).
2. Mantén MS-A si deseas probar ambos servicios a la vez.
3. Abre PowerShell dentro de esta carpeta MS-B:

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.12.8-hotspot"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\mvnw.cmd spring-boot:run
```

La ruta del JDK corresponde al equipo usado con MS-A. Si cambia tu instalación, ajusta `JAVA_HOME`.

## Pruebas Thunder Client

| Operación | Método | Ruta | JSON |
|---|---|---|---|
| Crear | POST | `/api/b` | `{ "nombreB": "Prueba B" }` |
| Listar | GET | `/api/b` | sin body |
| Buscar | GET | `/api/b/1` | sin body |
| Editar | PUT | `/api/b/1` | `{ "nombreB": "B actualizado" }` |
| Eliminar | DELETE | `/api/b/1` | sin body |

## Consola H2

Abre `http://localhost:82/h2-console`:
- JDBC URL: `jdbc:h2:file:./data/msbDB`
- Username: `sa`
- Password: vacío
- SQL: `SELECT * FROM ENTITY_B;`

Conecta usando **la consola del propio MS-B** mientras el proceso está corriendo.

## Estructura

```text
MS-B/
├─ pom.xml
├─ src/main/resources/application.properties
└─ src/main/java/com/universidad/msb/
   ├─ MsBApplication.java
   ├─ controller/EntityBController.java
   ├─ dto/EntityBRequest.java
   ├─ dto/EntityBResponse.java
   ├─ entity/EntityB.java
   ├─ repository/EntityBRepository.java
   └─ service/EntityBService.java
```

Nota: MS-B está preparado para futura comunicación con MS-A, pero esta entrega solo incluye su CRUD y registro en Eureka; no implementa aún llamadas entre microservicios.
