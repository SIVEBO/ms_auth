# ms_auth

Microservicio de autenticación del sistema **SIVEBO** (Sistema de Gestión de Envíos y Bodega).

---

## Descripción

Gestiona el registro de usuarios, login y control de roles dentro del sistema. Utiliza BCrypt para el cifrado de contraseñas y está preparado para la integración futura con JWT.

---

## Tecnologías

- Java 25
- Spring Boot 4.0.6
- Spring Security
- Spring Data JPA
- MariaDB
- Lombok
- Maven

---

## Base de datos

```
db_ms_auth
```

---

## Configuración

Editar `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/db_ms_auth?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=root
server.port=8086
```

---

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/auth/register` | Registra un nuevo usuario |
| POST | `/auth/login` | Autentica un usuario |
| GET | `/usuarios` | Lista todos los usuarios |

### Ejemplo — Registro

```json
POST /auth/register
{
  "username": "admin1",
  "password": "admin123",
  "email": "admin@sivebo.cl",
  "rol": "ADMIN"
}
```

### Ejemplo — Login

```json
POST /auth/login
{
  "username": "admin1",
  "password": "admin123"
}
```

---

## Roles disponibles

| Rol | Descripción |
|-----|-------------|
| `ADMIN` | Acceso total al sistema |
| `CAJERO` | Acceso operativo (default) |

---

## Estructura del proyecto

```
src/main/java/com/sivebo/ms_auth/
├── config/
│   ├── SecurityConfig.java
│   └── GlobalExceptionHandler.java
├── controller/
│   ├── AuthController.java
│   └── UsuarioController.java
├── dto/
│   ├── AuthResponse.java
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   └── UsuarioResponse.java
├── model/
│   ├── Rol.java
│   └── Usuario.java
├── repository/
│   ├── RolRepository.java
│   └── UsuarioRepository.java
└── service/
    └── UsuarioService.java
```

---

## Ejecución

```bash
./mvnw spring-boot:run
```

---

## Notas

- El campo `rol` en el registro acepta `"ADMIN"` o `"CAJERO"`. Si no se envía, se asigna `CAJERO` por defecto.
- El token retornado en `/auth/login` es un placeholder. La integración con JWT se implementará en una iteración futura.