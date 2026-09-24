## Sprint Goal

> "Habilitar el registro inicial de agricultores y compradores del Valle del Cauca, junto con la publicación de cosechas, validando la persistencia en PostgreSQL y la arquitectura REST."

**Capacidad del equipo:** 11 Story Points (equipo con nivel de madurez bajo, velocity inicial 8-11 SP)

El objetivo del Sprint 1 no es tener todo el sistema funcionando, sino lograr que la arquitectura base (**Spring Boot + PostgreSQL**) responda a endpoints reales con pruebas unitarias.

## Historias Seleccionadas para el Sprint 1

| ID | Historia | Story Points | Prioridad |
|----|----------|---------------|-----------|
| HU-01 | Registro de Agricultores | 3 | Must Have |
| HU-02 | Publicación de Productos | 5 | Must Have |
| HU-12 | Registro de Usuarios | 3 | Must Have |

**Total: 3 + 5 + 3 = 11 Story Points**

### ¿Por qué estas historias?

- **HU-01:** Es la puerta de entrada. Enseña a crear la tabla en PostgreSQL, la `@Entity`, el `@Repository`, el `@Service` y el `@RestController` en `/api/v1/auth/register` con respuesta `201 Created`.
- **HU-02:** Permite al agricultor publicar sus cosechas. Enseña relaciones JPA (`ManyToOne`), consultas derivadas y el endpoint `POST /api/v1/productos`.
- **HU-12:** Completa el ciclo de registro de compradores. Reutiliza la arquitectura de HU-01 y refuerza la encriptación de contraseñas y la validación de datos.



## 🔧 Desglose Técnico del Sprint 1

### HU-01: Registro de Agricultores

| ID Tarea | Descripción técnica | Componente / Tecnología | Atributo ISO 25010 |
|----------|---------------------|--------------------------|----------------------|
| T01.01 | Crear tabla y entidad Agricultor (nombre, cédula/NIT, municipio, predio, email, contraseña) con unicidad de email y cédula | PostgreSQL + JPA / `@Entity` | Integridad |
| T01.02 | Implementar `AgricultorRepository` con consulta por email | Spring Data JPA / Repository | Mantenibilidad |
| T01.03 | Crear `AgricultorService` con encriptación de contraseña (BCrypt) | Spring Security / BCrypt | Seguridad |
| T01.04 | Crear `AuthController` con `POST /api/v1/auth/register` que responda `201 Created` con DTO | Spring MVC / `@RestController` | Funcionalidad |
| T01.05 | Validar campos del formulario (email válido, municipio, campos obligatorios) | Jakarta Validation | Usabilidad |
| T01.06 | Escribir pruebas unitarias del registro con cobertura ≥60% | JUnit 5 + JaCoCo | Fiabilidad |


### HU-02: Publicación de Productos

| ID Tarea | Descripción técnica | Componente / Tecnología | Atributo ISO 25010 |
|----------|---------------------|--------------------------|----------------------|
| T02.01 | Crear entidad Producto (nombre, categoría, cantidad kg, precio, municipio, fecha recolección) | PostgreSQL + JPA / `@Entity` | Integridad |
| T02.02 | Implementar `ProductoRepository` con consultas por categoría y municipio | Spring Data JPA / Repository | Portabilidad |
| T02.03 | Crear `ProductoController` con `POST /api/v1/productos` y `GET /api/v1/productos` | Spring MVC / `@RestController` | Funcionalidad |
| T02.04 | Asociar cada producto a un agricultor autenticado (relación `ManyToOne`) | JPA + Spring Security | Seguridad |
| T02.05 | Validar que precio y cantidad sean positivos y categoría existente | Jakarta Validation | Usabilidad |
| T02.06 | Escribir pruebas unitarias de publicación y consulta con cobertura ≥60% | JUnit 5 + JaCoCo | Fiabilidad |


### HU-12: Registro de Usuarios

| ID Tarea | Descripción técnica | Componente / Tecnología | Atributo ISO 25010 |
|----------|---------------------|--------------------------|----------------------|
| T12.01 | Crear entidad Usuario (nombre, email, contraseña, municipio) con unicidad de email | PostgreSQL + JPA / `@Entity` | Integridad |
| T12.02 | Implementar `UsuarioRepository` con búsqueda por email | Spring Data JPA / Repository | Mantenibilidad |
| T12.03 | Crear `AuthController` con `POST /api/v1/auth/register-usuario` que responda `201 Created` | Spring MVC / `@RestController` | Funcionalidad |
| T12.04 | Encriptar contraseña con BCrypt antes de persistir | Spring Security / BCrypt | Seguridad |
| T12.05 | Validar campos del formulario (email, campos obligatorios) | Jakarta Validation | Usabilidad |
| T12.06 | Escribir pruebas unitarias del registro con cobertura ≥60% | JUnit 5 + JaCoCo | Fiabilidad |

**Total Sprint 1: 3 + 5 + 3 = 11 Story Points**
