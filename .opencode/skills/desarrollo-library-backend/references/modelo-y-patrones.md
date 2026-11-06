# Modelo y patrones reales de library-backend

Fuente de verdad. No inventes clases ni atributos fuera de esta lista.

## Dominio (paquete `com.lekasoft.model`)

| Entidad | PK | Atributos | Relaciones |
|---|---|---|---|
| `Customer` | `customerId` | `firstName`, `lastName`, `cardId`, `email` | — |
| `Category` | `categoryId` | `name` (req, ≤100), `description` (≤500), `status` (req, `String`) | — |
| `Book` | `bookId` | `title` (req, ≤200), `author` (req, ≤150), `isbn` (≤150), `available` (req, `String`) | `@ManyToOne` `category` → FK `id_category` (req) |
| `Booking` | `id` | `reservationDate` (`LocalDateTime`) | `@ManyToOne(LAZY)` `customer` (FK `customer_id`); `@OneToMany(mappedBy="booking", cascade=ALL, orphanRemoval=true)` `bookingDetails` (`List<BookingDetail>`, inicializada en `ArrayList`) |
| `BookingDetail` | `id` | — | `@ManyToOne(LAZY)` `booking` (FK `booking_id`); `@ManyToOne(LAZY)` `book` (FK `book_id`) |

Todas usan Lombok `@Data @Builder @AllArgsConstructor @NoArgsConstructor`
(`Booking` añade `@EqualsAndHashCode(onlyExplicitlyIncluded = true)` con `id` incluido).

## DTOs existentes (`com.lekasoft.dto`)

- `CustomerDTO`: `customerId`, `firstName`, `lastName`, `cardId`, `email`.
- `CategoryDTO`: `categoryId`, `name`, `description`, `status`.
- `BookDTO`: `bookId`, `title`, `author`, `isbn`, `available`, `category` (**`String`**, nombre de la categoría).

Faltan (crear siguiendo este estilo): `BookingRequestDTO` (p. ej. `customerId` + `List<Long> bookIds`),
`BookingResponseDTO` (`id`, `reservationDate`, datos del cliente, títulos de libros) y, si aporta,
`BookingDetailDTO`. Lombok igual que los existentes.

## Infraestructura

- `IGenericRepository<T, ID> extends JpaRepository<T, ID>` con `@NoRepositoryBean`.
- Repos vacíos ya creados: `IBookRepository`, `ICategoryRepository`, `ICustomerRepository`,
  `IBookingRepository`, `IBookingDetailRepository`.
- `ICRUD<T, ID>`: `save`, `update(ID, T)`, `findAll`, `findById(ID)`, `delete(ID)`.
  `CRUDImpl<T, ID>` lo implementa vía `protected abstract IGenericRepository<T, ID> getRepo();`.
- Servicios: `ICustomerService`, `IBookService`, `ICategoryService` (solo extienden `ICRUD`);
  impls `CustomerServiceImpl`, `BookServiceImpl`, `CategoryServiceImpl` (`getRepo()` devuelve `null`: corregir).
- Mappers: `CustomerMapper` completo (`toCustomer`/`toCustomerDto`); `BookMapper`, `CategoryMapper` y repos vacíos.
- `exception/` existe y está vacío.
- Único controlador: `CustomerController` (`/v1/customers`).

## Snippets de referencia (patrón a copiar)

Controlador (`CustomerController`):

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/customers")
public class CustomerController {
    private final ICustomerService service;
    private final CustomerMapper mapper;

    @PostMapping
    public ResponseEntity<CustomerDTO> save(@RequestBody CustomerDTO customerRequest){
        var customerDto = mapper.toCustomerDto(service.save(mapper.toCustomer(customerRequest)));
        return ResponseEntity.ok(customerDto);
    }
    // PUT /{id}, GET, GET /{id}, DELETE /{id} → 200 / 204
}
```

Servicio (`CustomerServiceImpl`):

```java
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl extends CRUDImpl<Customer, Long> implements ICustomerService {
    private final ICustomerRepository repository;
    @Override protected IGenericRepository<Customer, Long> getRepo() { return repository; }
}
```

Mapper (`CustomerMapper`):

```java
@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer toCustomer(CustomerDTO customerDTO);
    CustomerDTO toCustomerDto(Customer customer);
}
```

## Mapa `BookDTO.category` (String) ↔ `Book.category` (entidad)

`BookDTO.category` es el nombre. Patrón recomendado: en `BookMapper` declarar métodos con
`@Mapping(target = "category", source = "category.name")` (entidad → DTO) y, en el servicio de
guardar/actualizar, resolver la entidad antes de mapear:

```java
Category category = categoryRepository.findByName(dto.getCategory())   // añadir en ICategoryRepository
        .orElseThrow(() -> new NotFoundException("Categoría no encontrada: " + dto.getCategory()));
```

## Registro de reserva (flujo objetivo)

```java
@Transactional
public BookingResponseDTO register(BookingRequestDTO request) {
    Customer customer = customerRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new NotFoundException("Cliente no encontrado: " + request.getCustomerId()));
    List<Book> books = request.getBookIds().stream()
            .map(id -> bookRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Libro no encontrado: " + id)))
            .toList();
    var booking = Booking.builder()
            .reservationDate(LocalDateTime.now())
            .customer(customer)
            .bookingDetails(new ArrayList<>())
            .build();
    books.forEach(book -> booking.getBookingDetails()
            .add(BookingDetail.builder().booking(booking).book(book).build()));
    return mapper.toBookingResponseDto(bookingRepository.save(booking));
}
```

Consulta por cliente (en `IBookingRepository`): `List<Booking> findByCustomerCustomerId(Long customerId);`
y en el servicio `@Transactional(readOnly = true)` con `@EntityGraph(attributePaths = {"customer","bookingDetails.book"})`
o `JOIN FETCH` para evitar `LazyInitializationException` al serializar.

## Excepciones globales (paquete `exception`, a crear)

```java
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(NotFoundException ex, HttpServletRequest req) { ... 404 ... }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> badRequest(BadRequestException ex, HttpServletRequest req) { ... 400 ... }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        // 400 con mensajes de FieldError
    }
}
```

`ErrorResponse`: `timestamp`, `status`, `error`, `message`, `path`.
En `CRUDImpl.findById`: `.orElseThrow(() -> new NotFoundException(...))` en lugar de `RuntimeException("")`.

## Validación (`jakarta.validation`, requiere `spring-boot-starter-validation` en `pom.xml`)

- DTOs: `@NotBlank` en `title`, `author`, `available`, `name`, `status`; `@NotNull` en `customerId`;
  `@NotEmpty`/`@NotNull` en `bookIds`.
- Controlador: `public ResponseEntity<XDTO> save(@Valid @RequestBody XDTO request)`.

## Spring Boot 4.1.1 (cambios de paquete en pruebas)

| Anotación/clase | Paquete correcto |
|---|---|
| `@SpringBootTest` | `org.springframework.boot.test.context.SpringBootTest` |
| `@WebMvcTest` | `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest` |
| `@AutoConfigureMockMvc` | `org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc` |
| `@MockitoBean` (reemplaza a `@MockBean`) | `org.springframework.test.context.bean.override.mockito.MockitoBean` |
| `MockMvc` | `org.springframework.test.web.servlet.MockMvc` |

Ejemplo de test de controlador:

```java
@WebMvcTest(BookController.class)
class BookControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockitoBean private IBookService service;
    @MockitoBean private BookMapper mapper;
    // given/when/then con mockMvc.perform(...).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Clean Code"));
}
```

## Configuración PostgreSQL (`src/main/resources/application.yaml`)

```yaml
spring:
  jpa:
    database: postgresql
    show-sql: true
    hibernate: { ddl-auto: update }
  datasource:
    url: jdbc:postgresql://localhost:5432/library
    username: postgres
    password: 1234
    driver-class-name: org.postgresql.Driver
```

Las pruebas unitarias/debe de controlador no deben depender de esta base.
