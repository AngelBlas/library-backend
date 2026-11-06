---
name: desarrollo-library-backend
description: Implementa funcionalidades del backend library-backend (CRUD Libro y Categoría, reservas con Booking/BookingDetail, validación con @Valid, excepciones globales y pruebas) replicando el patrón real de Cliente con Spring Boot 4.1.1, Java 25, MapStruct y PostgreSQL.
---

# Desarrollo backend library-backend

Skill para implementar funcionalidades del proyecto académico `library-backend` (grupo `com.lekasoft`)
replicando el patrón ya funcionante del mantenimiento de Cliente.

Antes de escribir código, lee `references/modelo-y-patrones.md` (catálogo real de entidades, DTOs,
endpoints objetivo y snippets de referencia). Los nombres de clases, atributos y relaciones de ese
archivo son los únicos válidos: no inventes ni renombres nada.

## Contexto verificado (no modificar sin justificación)

| Aspecto | Valor real |
|---|---|
| Java / Spring Boot | 25 / 4.1.1 (`spring-boot-starter-webmvc`, NO `starter-web`) |
| Persistencia | `spring-boot-starter-data-jpa`, PostgreSQL (`application.yaml`: `jdbc:postgresql://localhost:5432/library`, ddl-auto `update`) |
| Mapeo / POJO | MapStruct 1.6.3 (`@Mapper(componentModel="spring")`), Lombok (`@Data @Builder @AllArgsConstructor @NoArgsConstructor`, `@RequiredArgsConstructor`) |
| Pruebas | `spring-boot-starter-webmvc-test` (JUnit 6, Mockito, AssertJ, MockMvc) |
| Capas | `controller` → `service`(+`impl`) → `repository` → `model`; DTOs en `dto`, mappers en `mapper` |
| Patrón CRUD | `ICRUD<T,ID>` + `CRUDImpl` (abstract `getRepo()` → `IGenericRepository`) + `ICustomerService/CustomerServiceImpl` + `CustomerMapper` + `CustomerController` (`/v1/customers`) |
| Pendiente | `exception/` vacío; `BookMapper`/`CategoryMapper` vacíos; `CategoryServiceImpl.getRepo()` devuelve `null`; `CRUDImpl` lanza `RuntimeException("")`; sin controladores de Libro/Categoría/Reserva |

Única dependencia que se permite agregar a `pom.xml`:
`spring-boot-starter-validation` (necesaria para `@Valid` + `jakarta.validation`; justificar en el
informe final). No agregues Testcontainers, H2, Lombok Data JPA metamodel ni otras librerías.

## Alcance funcional

1. **Libro**: CRUD completo en `/v1/books` (`BookController`, completar `BookMapper`).
   `BookDTO.category` es `String` (nombre): el servicio resuelve la `Category` con
   `ICategoryRepository` antes de persistir; si no existe → excepción 404.
2. **Categoría**: CRUD completo en `/v1/categories` (`CategoryController`, completar `CategoryMapper`,
   inyectar `ICategoryRepository` en `CategoryServiceImpl` para corregir `getRepo()`).
3. **Reserva (Booking)**: en `/v1/bookings`
   - `POST` registrar reserva de un cliente con uno o varios libros vía `BookingDetail`:
     request con `customerId` + lista de ids de libros; verificar existencia del cliente y de
     **todos** los libros antes de guardar; `reservationDate = LocalDateTime.now()`; construir
     `BookingDetail` por libro con `booking` como dueño de la relación; guardar con `@Transactional`
     (cascada `ALL` de `Booking` ya cubre los detalles).
   - `GET` listado de reservas mostrando fecha, cliente y libros.
   - `GET /customer/{customerId}` reservas de un cliente.
   DTOs nuevos en `dto/` (p. ej. `BookingRequestDTO`, `BookingResponseDTO`) y mapper
   `BookingMapper` siguiendo el estilo de `CustomerMapper`.

## Reglas de implementación

- Copia el estilo real de `CustomerController`/`CustomerServiceImpl`/`CustomerMapper`: `@RestController`
  + `@RequiredArgsConstructor` + `ResponseEntity`, `var`, mapeo por mapper (nunca `ModelMapper`).
- Servicios: `XServiceImpl extends CRUDImpl<X, Long> implements IXService` con `getRepo()`.
- Repositorios: solo métodos derivados/queries derivadas necesarias (p. ej.
  `List<Booking> findByCustomerCustomerId(Long customerId)`); evita `LazyInitializationException`
  con `@Transactional(readOnly = true)` y fetch explícito (`@EntityGraph` o `JOIN FETCH`).
- Excepciones: crea en `com.lekasoft.exception` una jerarquía mínima (p. ej.
  `NotFoundException`, `BadRequestException`) y un `@RestControllerAdvice`
  `GlobalExceptionHandler` que devuelva 404/400 con un body tipo
  `{timestamp, status, error, message, path}`. Sustituye el `new RuntimeException("")` de
  `CRUDImpl.findById` por `NotFoundException`, y haz que `update` cargue la entidad existente por
  `id` antes de guardar (sin ella, 404).
- Validación: anota los DTOs de request (`@NotBlank`, `@NotNull`, `@NotEmpty`) y usa `@Valid`
  en el `@RequestBody` del controlador; el `GlobalExceptionHandler` debe mapear
  `MethodArgumentNotValidException` a 400 con los mensajes de campo.
- Persistencia: usa la configuración PostgreSQL existente; no dupliques entidades ni cambies
  relaciones, columnas ni nombres de tablas.
- Limpieza mínima permitida: elimina imports innecesarios/erróneos en archivos que ya toques
  (p. ej. `CustomerMapper`, `ICustomerService`) y los imports sin usar que dejes en tus archivos.

## Procedimiento obligatorio

1. Analiza el requerimiento y localiza implementaciones similares (`CustomerController` es la
   referencia). Lee `references/modelo-y-patrones.md` y los archivos implicados antes de editar.
2. Lista los archivos a crear/modificar y muéstralos brevemente antes de escribir.
3. Reutiliza el patrón de Cliente en todos los aspectos (nombres, anotaciones, estructura, status codes).
4. Modifica el código fuente directamente (no sugieras diffs sin aplicar).
5. Crea/actualiza pruebas: `src/test/java/com/lekasoft/...` en paquetes espejo.
6. Ejecuta las validaciones disponibles y corrige los errores introducidos por ti.
7. Informa al final: archivos creados/modificados, comando de pruebas ejecutado y resultados reales
   (número de tests, fallos). No inventes resultados.

Comandos de validación (raíz del proyecto):

```bash
./mvnw -q compile      # compilación rápida
./mvnw test            # toda la suite
./mvnw test -Dtest=BookingControllerTest   # una clase
```

En Windows usar `mvnw.cmd`. Requiere `JAVA_HOME` apuntando a JDK 25. Si el entorno no tiene
Java/Maven, dilo explícitamente en el informe en lugar de afirmar que las pruebas pasaron.

## Pruebas exigidas

- **Servicio** (Mockito puro): `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`, AssertJ.
  Caso feliz + error (cliente inexistente, libro inexistente, lista vacía).
- **Controlador** (Web MVC): `@WebMvcTest(XController.class)` con `@MockitoBean` (en Spring Boot 4
  `@MockBean` ya no existe; la anotación es
  `org.springframework.test.context.bean.override.mockito.MockitoBean` y `@WebMvcTest` está en
  `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`). Verifica JSON con
  `jsonPath` y códigos 200/201/204/400/404.
- Cubre también éxito y error del `GlobalExceptionHandler`.
- No elimines ni rompas `LibraryBackendApplicationTests`. Evita `@SpringBootTest`/`@DataJpaTest`
  salvo que confirmes que PostgreSQL local está disponible; las pruebas deben correr sin base de datos.

## Criterios de aceptación

- `/v1/books` y `/v1/categories` responden CRUD completo con la misma semántica que `/v1/customers`.
- Registrar una reserva con N libros crea un `Booking` con N `BookingDetail` en una sola
  transacción; cliente o libro inexistente devuelve 404 y no persiste nada.
- El listado y la consulta por cliente muestran fecha, cliente y títulos de libros sin errores de lazy loading.
- Requests inválidos devuelven 400 vía `@Valid` + `GlobalExceptionHandler`.
- `./mvnw test` compila y las nuevas pruebas pasan; el resumen final refleja resultados reales.

## Límites (prohibido)

- Duplicar entidades, renombrar clases/atributos o cambiar relaciones existentes.
- Agregar dependencias distintas de `spring-boot-starter-validation` o modificar versiones de `pom.xml` sin justificación explícita.
- Eliminar pruebas existentes, sobrescribir cambios del usuario o implementar funcionalidad fuera del alcance pedido.

## Ejemplos de solicitudes que activan este skill

- "Implementa el CRUD completo de Libro y sus pruebas."
- "Agrega el mantenimiento de Categoría."
- "Registra reservas de un cliente con varios libros y lista las reservas por cliente."
- "Añade validación de requests y manejo global de excepciones al backend."
