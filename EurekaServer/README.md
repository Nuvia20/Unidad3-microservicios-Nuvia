# Eureka Server para MS-A

Proyecto Spring Boot compatible con **MS-A** (Spring Boot 3.5.16 / Spring Cloud 2025.0.3). No contiene CRUD ni H2: su función es registrar microservicios.

## Ejecución en Windows (PowerShell)

1. Descomprime `EurekaServer` junto a `MS-A` dentro de `WebIntegral`.
2. Abre una terminal situada en la carpeta `EurekaServer`.
3. Comprueba `./mvnw.cmd -version` (debe usar un **JDK**, no JRE). Si Maven apunta a JRE, configura en la terminal:

   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.12.8-hotspot"
   $env:Path = "$env:JAVA_HOME\bin;$env:Path"
   ```

4. Ejecuta:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

5. Abre http://localhost:8761 y deja la terminal abierta.
6. En **otra terminal**, desde `MS-A`, inicia `.\mvnw.cmd spring-boot:run`.
7. Actualiza Eureka: debe aparecer **MS-A** en la lista con estado **UP**.

## Puertos

- `8761`: panel Eureka Server.
- `81`: API y consola H2 de MS-A.

## Importante

No ejecutar este comando desde la carpeta `MS-A` cuando quieras iniciar Eureka. La línea de Maven deberá mostrar `Building EurekaServer`, no `Building MS-A`.
