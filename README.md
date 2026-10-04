# Canchas Backend ⚽

API REST para un sistema de reservas de canchas de fútbol, desarrollada con **Spring Boot 3** y **Java 17**. Implementa el patrón de diseño **Observer** para notificar automáticamente a usuarios y administradores ante cambios en el estado de una reserva.

## Características

- Gestión de **usuarios** (registro, login básico y CRUD).
- Gestión de **canchas** (alta, baja, modificación y búsqueda por tipo).
- Gestión de **reservas** (creación, confirmación, cancelación y consultas por usuario o cancha).
- Notificaciones automáticas vía patrón **Observer** cuando una reserva se confirma o cancela (usuario y administrador).
- Carga de datos de ejemplo al iniciar la aplicación (`DataLoader`).
- Validaciones de entrada con Bean Validation (`jakarta.validation`).
- Manejo centralizado de errores (`GlobalExceptionHandler`).

## Tecnologías

- **Java 17**
- **Spring Boot 3.2.0** (Web, Data JPA, Validation, DevTools)
- **MySQL** (driver `mysql-connector-j`)
- **Maven** (con Maven Wrapper incluido)
- **Hibernate** como proveedor JPA

## Arquitectura del proyecto

```
src/main/java
├── canchas/            # Clase principal (CanchasFutbolApplication)
├── config/             # Configuración y carga de datos iniciales (DataLoader)
├── controller/         # Controladores REST (Auth, Cancha, Reserva, Usuario)
├── dto/                # Objetos de transferencia de datos (Login, Reserva)
├── exception/          # Manejo global de excepciones
├── model/              # Entidades JPA (Usuario, Cancha, Reserva)
├── observer/           # Patrón Observer (Observador, notificadores)
├── repository/         # Repositorios Spring Data JPA
└── service/            # Lógica de negocio
```

### Patrón Observer

`Reserva` actúa como **sujeto observable**: mantiene una lista de `Observador` y notifica eventos (`CONFIRMADA`, `CANCELADA`) cada vez que cambia su estado. Existen dos observadores concretos:

- `UsuarioNotificador`: simula el envío de un email al usuario que hizo la reserva.
- `AdministradorNotificador`: registra la actividad para el administrador (log/dashboard).

## Requisitos previos

- JDK 17 o superior
- MySQL Server en ejecución (local o remoto)
- Maven (opcional, el proyecto incluye Maven Wrapper `mvnw` / `mvnw.cmd`)

## Configuración

La configuración se encuentra en `src/main/resources/application.properties`:

```properties
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/reservas_canchas?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

> ⚠️ Ajustá `spring.datasource.username` y `spring.datasource.password` según tu entorno local. La base de datos `reservas_canchas` se crea automáticamente si no existe.

## Instalación y ejecución

1. Cloná el repositorio:
   ```bash
   git clone https://github.com/FedericoMartinV/canchas-backend.git
   cd canchas-backend
   ```

2. Asegurate de tener MySQL corriendo y accesible con las credenciales definidas en `application.properties`.

3. Ejecutá la aplicación con el wrapper de Maven:

   En Linux/Mac:
   ```bash
   ./mvnw spring-boot:run
   ```

   En Windows:
   ```bash
   mvnw.cmd spring-boot:run
   ```

4. La API quedará disponible en `http://localhost:8080`.

Al iniciar por primera vez, `DataLoader` cargará usuarios y canchas de ejemplo si la base de datos está vacía.

## Endpoints principales

### Autenticación — `/api/auth`

| Método | Endpoint         | Descripción                          |
|--------|------------------|---------------------------------------|
| POST   | `/api/auth/login`    | Inicia sesión con email (sin validación de password aún) |
| POST   | `/api/auth/register`  | Registra un nuevo usuario             |

### Usuarios — `/api/usuarios`

| Método | Endpoint              | Descripción                  |
|--------|-----------------------|-------------------------------|
| GET    | `/api/usuarios`         | Lista todos los usuarios      |
| GET    | `/api/usuarios/{id}`    | Obtiene un usuario por ID     |
| POST   | `/api/usuarios`         | Crea un usuario               |
| PUT    | `/api/usuarios/{id}`    | Actualiza un usuario          |
| DELETE | `/api/usuarios/{id}`    | Elimina un usuario            |

### Canchas — `/api/canchas`

| Método | Endpoint                   | Descripción                      |
|--------|-----------------------------|------------------------------------|
| GET    | `/api/canchas`                | Lista todas las canchas            |
| GET    | `/api/canchas/{id}`           | Obtiene una cancha por ID          |
| POST   | `/api/canchas`                | Crea una cancha                    |
| PUT    | `/api/canchas/{id}`           | Actualiza una cancha               |
| DELETE | `/api/canchas/{id}`           | Elimina una cancha                 |
| GET    | `/api/canchas/tipo/{tipo}`    | Busca canchas por tipo (ej: fútbol 5, fútbol 11) |

### Reservas — `/api/reservas`

| Método | Endpoint                              | Descripción                          |
|--------|-----------------------------------------|----------------------------------------|
| GET    | `/api/reservas`                          | Lista todas las reservas               |
| GET    | `/api/reservas/{id}`                     | Obtiene una reserva por ID             |
| POST   | `/api/reservas`                          | Crea una nueva reserva                 |
| PATCH  | `/api/reservas/{id}/confirmar`           | Confirma una reserva (dispara notificaciones) |
| PATCH  | `/api/reservas/{id}/cancelar`            | Cancela una reserva (dispara notificaciones) |
| DELETE | `/api/reservas/{id}`                     | Elimina una reserva                    |
| GET    | `/api/reservas/usuario/{usuarioId}`      | Lista reservas de un usuario           |
| GET    | `/api/reservas/cancha/{canchaId}`        | Lista reservas de una cancha           |

### Ejemplo: crear una reserva

```http
POST /api/reservas
Content-Type: application/json

{
  "usuarioId": "uuid-del-usuario",
  "canchaId": "uuid-de-la-cancha",
  "fecha": "2026-09-20",
  "horaInicio": "18:00",
  "horaFin": "19:00"
}
```

## Modelo de datos

- **Usuario**: `id`, `nombre`, `apellido`, `email` (único), `telefono`.
- **Cancha**: `id`, `nombre`, `ubicacion`, `tipo`, `precioPorHora`, `iluminacion`.
- **Reserva**: `id`, `usuario`, `cancha`, `fecha`, `horaInicio`, `horaFin`, `estado` (`PENDIENTE`, `CONFIRMADA`, `CANCELADA`).

## Estado del proyecto

Este proyecto está en desarrollo. Algunas funcionalidades pendientes o simplificadas:

- La autenticación de `login` aún no valida contraseñas (se hará con Spring Security más adelante).
- El rol de administrador se determina de forma temporal si el email contiene la palabra "admin".
- Las notificaciones del patrón Observer actualmente se registran por log/consola, simulando el envío real de emails.

## Testing

El proyecto incluye un test base de carga de contexto de Spring Boot en `src/test/java`. Podés ejecutarlo con:

```bash
./mvnw test
```

## Licencia

Este proyecto no especifica una licencia. Agregá una si pensás distribuirlo públicamente.