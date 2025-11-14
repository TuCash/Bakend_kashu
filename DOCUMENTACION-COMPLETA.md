# TuCash Platform - Documentación Completa del Backend

## Tabla de Contenido

1. [Información General](#información-general)
2. [Arquitectura del Proyecto](#arquitectura-del-proyecto)
3. [Stack Tecnológico](#stack-tecnológico)
4. [Configuración y Despliegue](#configuración-y-despliegue)
5. [Autenticación y Seguridad](#autenticación-y-seguridad)
6. [Modelos de Datos](#modelos-de-datos)
7. [API Endpoints](#api-endpoints)
8. [Guía de Integración para Frontend](#guía-de-integración-para-frontend)

---

## Información General

**TuCash Platform** es una API REST completa para gestión financiera personal diseñada para estudiantes. Permite a los usuarios rastrear ingresos, gastos, metas de ahorro, presupuestos y generar reportes analíticos.

### Características Principales

- Gestión de usuarios con autenticación JWT
- Múltiples cuentas financieras (efectivo, banco, tarjetas)
- Seguimiento de transacciones (ingresos, gastos, transferencias)
- Categorías personalizables
- Metas de ahorro con tracking de progreso
- Presupuestos con alertas automáticas
- Dashboard analítico con tendencias
- Transacciones recurrentes automatizadas
- Sistema de notificaciones
- Recordatorios de pagos

### URLs del Servicio

- **Local Development**: `http://localhost:8080`
- **API Base Path**: `/api/v1`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI Docs**: `http://localhost:8080/v3/api-docs`

---

## Arquitectura del Proyecto

### Patrón de Arquitectura: Domain-Driven Design (DDD)

El proyecto implementa **DDD con patrones tácticos** y **CQRS** (Command Query Responsibility Segregation).

### Bounded Contexts

```
com.kashu.tucash/
├── iam/                          # Identity & Access Management
├── transactions/                 # Gestión de transacciones
├── savings/                      # Metas y presupuestos
├── automation/                   # Transacciones recurrentes
├── dashboard/                    # Analíticas y reportes
├── notifications/                # Sistema de notificaciones
├── reminders/                    # Recordatorios
├── sharedexpenses/              # Gastos compartidos
└── shared/                      # Componentes compartidos
```

### Estructura de Capas por Bounded Context

Cada bounded context sigue esta estructura:

```
bounded-context/
├── domain/
│   ├── model/
│   │   ├── aggregates/          # Raíces de agregado (entidades principales)
│   │   ├── entities/            # Entidades secundarias
│   │   ├── valueobjects/        # Objetos de valor (enums, etc.)
│   │   ├── commands/            # Comandos para operaciones de escritura
│   │   └── queries/             # Queries para operaciones de lectura
│   └── services/                # Interfaces de servicios del dominio
│
├── application/
│   └── internal/
│       ├── commandservices/     # Implementación de servicios de comando
│       └── queryservices/       # Implementación de servicios de consulta
│
├── infrastructure/
│   └── persistence/
│       └── jpa/
│           └── repositories/    # Repositorios JPA
│
└── interfaces/
    └── rest/
        ├── resources/           # DTOs (Request/Response)
        ├── transform/           # Assemblers (conversión DTO ↔ Domain)
        └── *Controller.java     # Controladores REST
```

### Patrones Implementados

1. **CQRS (Command Query Responsibility Segregation)**
   - Comandos: `Create*Command`, `Update*Command`, `Delete*Command`
   - Queries: `Get*Query`, `GetAll*Query`
   - Servicios separados: `*CommandService`, `*QueryService`

2. **Repository Pattern**
   - Interfaces de repositorio en la capa de infraestructura
   - Uso de Spring Data JPA

3. **Assembler Pattern**
   - `*FromResourceAssembler`: Convierte DTOs a comandos/queries
   - `*FromEntityAssembler`: Convierte entidades a DTOs de respuesta

4. **Aggregate Pattern**
   - Todas las entidades principales extienden `AuditableAbstractAggregateRoot<T>`
   - Incluye auditoría automática (createdAt, updatedAt)

---

## Stack Tecnológico

### Backend Framework
- **Java 21**
- **Spring Boot 3.5.6**
- **Spring Data JPA** - Persistencia de datos
- **Spring Security** - Autenticación y autorización
- **Spring Validation** - Validación de datos

### Base de Datos
- **PostgreSQL** (Producción y Desarrollo)
- **JPA/Hibernate** - ORM
- **Naming Strategy**: Snake case con pluralización automática

### Seguridad
- **JWT (JSON Web Tokens)** - `io.jsonwebtoken:jjwt 0.12.3`
- **BCrypt** - Hash de contraseñas
- **Spring Security** - Filtros y configuración

### Documentación
- **SpringDoc OpenAPI 3** - `springdoc-openapi-starter-webmvc-ui 2.8.13`
- Swagger UI integrado

### Utilidades
- **Lombok** - Reducción de boilerplate
- **Pluralize** - `io.github.encryptorcode:pluralize 1.0.0`
- **Apache Commons Lang3**

### Herramientas de Desarrollo
- **Spring Boot DevTools** - Hot reload
- **Maven** - Gestión de dependencias

---

## Configuración y Despliegue

### Requisitos Previos

- JDK 21 o superior
- PostgreSQL 14+
- Maven 3.8+

### Configuración de Base de Datos

Archivo: `src/main/resources/application.properties`

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5434/tucash_db
spring.datasource.username=postgres
spring.datasource.password=1234
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.naming.physical-strategy=com.kashu.tucash.shared.infrastructure.persistence.jpa.strategy.SnakeCasePhysicalNamingStrategy

# JWT Configuration
jwt.secret=TuCashSecretKeyForJWTTokenGenerationAndValidation2025MustBeLongEnough
jwt.expiration=604800000  # 7 días en milisegundos

# CORS Configuration
cors.allowed.origins=http://localhost:4200,http://localhost:5173
```

### Convenciones de Naming en Base de Datos

- **Tablas**: Nombres en plural y snake_case
  - `User.java` → `users`
  - `Transaction.java` → `transactions`
  - `RecurringTransaction.java` → `recurring_transactions`

- **Columnas**: snake_case
  - `displayName` → `display_name`
  - `transactionDate` → `transaction_date`

### Ejecutar la Aplicación Localmente

```bash
# Clonar repositorio
cd kashu_backend

# Compilar y ejecutar
./mvnw spring-boot:run

# O en Windows
mvnw.cmd spring-boot:run

# La aplicación estará disponible en http://localhost:8080
```

### Comandos Maven Útiles

```bash
# Compilar sin ejecutar
./mvnw clean compile

# Ejecutar tests
./mvnw test

# Empaquetar (JAR)
./mvnw clean package

# Limpiar y construir
./mvnw clean install
```

### Despliegue en Heroku

El proyecto incluye configuración para Heroku:

**Archivos de configuración:**
- `Procfile`: Define el comando de inicio
- `system.properties`: Especifica la versión de Java

```bash
# Login en Heroku
heroku login

# Crear aplicación
heroku create tu-app-name

# Agregar PostgreSQL
heroku addons:create heroku-postgresql:mini

# Configurar variables de entorno
heroku config:set JWT_SECRET=your-secret-key
heroku config:set CORS_ALLOWED_ORIGINS=https://your-frontend.com

# Deploy
git push heroku main

# Ver logs
heroku logs --tail
```

---

## Autenticación y Seguridad

### Arquitectura de Seguridad

El sistema utiliza **JWT (JSON Web Tokens)** con **Spring Security** para autenticación stateless.

### Componentes de Seguridad

#### 1. JWT Authentication Filter
**Archivo**: `iam/infrastructure/authorization/jwt/JwtAuthenticationFilter.java`

- Intercepta todas las peticiones HTTP
- Extrae el token del header `Authorization`
- Valida el token y carga el usuario en el contexto de seguridad

#### 2. Web Security Configuration
**Archivo**: `iam/infrastructure/authorization/sfs/configuration/WebSecurityConfiguration.java`

**Endpoints públicos (sin autenticación):**
```java
/api/v1/auth/register
/api/v1/auth/login
/api/v1/auth/forgot-password
/api/v1/auth/reset-password
/swagger-ui/**
/v3/api-docs/**
```

**Endpoints protegidos:**
- Todos los demás endpoints requieren autenticación JWT

#### 3. Password Hashing
**Servicio**: `BCryptHashingService`

- Usa BCrypt para hashear contraseñas
- Verificación segura de contraseñas

#### 4. Token Service
**Servicio**: `BearerTokenService`

- Genera tokens JWT con expiración de 7 días
- Extrae información del usuario del token
- Valida tokens

### Configuración CORS

```java
// Orígenes permitidos
http://localhost:4200  // Angular
http://localhost:5173  // Vite (Vue/React)

// Métodos permitidos
GET, POST, PUT, PATCH, DELETE, OPTIONS

// Headers: Todos (*)
// Credentials: true
```

### Modelo de Usuario

**Archivo**: `iam/domain/model/aggregates/User.java`

```java
@Entity
@Table(name = "users")
public class User {
    private Long id;
    private String email;           // Único, requerido
    private String password;        // BCrypt hash, min 8 caracteres
    private String displayName;     // Requerido
    private String photoUrl;        // Opcional
    private String currency;        // Default: "PEN"
    private String theme;           // Default: "light"
    private String locale;          // Default: "es"
    private Boolean notificationsEnabled;
    private Boolean emailNotifications;
    private Boolean pushNotifications;
    private Set<String> roles;      // Default: ["ROLE_USER"]
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### Flujo de Autenticación

#### 1. Registro de Usuario
```
POST /api/v1/auth/register
→ Valida email único
→ Hashea contraseña con BCrypt
→ Crea usuario con rol ROLE_USER
→ Retorna datos del usuario (sin token)
```

#### 2. Login
```
POST /api/v1/auth/login
→ Valida credenciales
→ Genera JWT token (válido 7 días)
→ Retorna usuario + token
```

#### 3. Peticiones Autenticadas
```
Header: Authorization: Bearer {token}
→ Filter extrae token
→ Valida token
→ Carga usuario en SecurityContext
→ Controller puede acceder al usuario autenticado
```

### Reset de Contraseña

El sistema implementa reset de contraseña con tokens:

**Entidad**: `PasswordResetToken`
```java
- token: String (UUID)
- user: User
- expiryDate: LocalDateTime (1 hora)
```

**Flujo:**
1. Usuario solicita reset → se genera token
2. En producción: enviar email con link
3. Usuario usa token para resetear contraseña
4. Token se invalida después del uso

---

## Modelos de Datos

### Diagrama de Entidades Principales

```
User (users)
├── 1:N → Account (accounts)
├── 1:N → Transaction (transactions)
├── 1:N → Category (categories)
├── 1:N → Goal (goals)
├── 1:N → Budget (budgets)
├── 1:N → RecurringTransaction (recurring_transactions)
├── 1:N → Notification (notifications)
└── 1:N → Reminder (reminders)

Account
└── 1:N → Transaction

Category
├── 1:N → Transaction
└── 1:N → Budget
```

### 1. User (users)

**Bounded Context**: IAM

| Campo | Tipo | Descripción | Default |
|-------|------|-------------|---------|
| id | Long | Primary Key | Auto |
| email | String | Email único | Required |
| password | String | BCrypt hash | Required |
| display_name | String | Nombre público | Required |
| photo_url | String | URL foto perfil | null |
| currency | String | Moneda preferida | "PEN" |
| theme | String | Tema UI | "light" |
| locale | String | Idioma | "es" |
| notifications_enabled | Boolean | Habilitar notifs | true |
| email_notifications | Boolean | Notifs por email | true |
| push_notifications | Boolean | Notifs push | true |
| created_at | LocalDateTime | Fecha creación | Auto |
| updated_at | LocalDateTime | Última actualización | Auto |

**Tabla relacionada**: `user_roles` (Many-to-Many)

### 2. Account (accounts)

**Bounded Context**: Transactions

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Primary Key |
| user_id | Long | FK → users |
| name | String | Nombre cuenta |
| currency | String | Moneda |
| balance | BigDecimal | Saldo actual |
| created_at | LocalDateTime | Fecha creación |
| updated_at | LocalDateTime | Última actualización |

**Validaciones**:
- `name`: Requerido, no vacío
- `currency`: Requerido
- `balance`: Se actualiza automáticamente con transacciones

### 3. Category (categories)

**Bounded Context**: Transactions

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Primary Key |
| user_id | Long | FK → users |
| name | String | Nombre categoría |
| type | Enum | INCOME / EXPENSE |
| icon | String | Icono (ej: "pi-shopping-cart") |
| color | String | Color hex (ej: "#FF6B6B") |
| is_system_category | Boolean | Categoría del sistema |
| created_at | LocalDateTime | Fecha creación |
| updated_at | LocalDateTime | Última actualización |

**Categorías del Sistema**:
- Se crean automáticamente al registrar usuario
- No pueden eliminarse (`is_system_category = true`)
- Pueden personalizarse (icon, color)

### 4. Transaction (transactions)

**Bounded Context**: Transactions

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Primary Key |
| user_id | Long | FK → users |
| account_id | Long | FK → accounts |
| category_id | Long | FK → categories |
| type | Enum | INCOME / EXPENSE / TRANSFER |
| amount | BigDecimal | Monto (precision: 19,2) |
| description | String | Descripción (max 500) |
| transaction_date | LocalDate | Fecha transacción |
| shared_split_metadata | String | JSON metadata gastos compartidos |
| created_at | LocalDateTime | Fecha creación |
| updated_at | LocalDateTime | Última actualización |

**Lógica de Negocio**:
- Al crear INCOME: `account.balance += amount`
- Al crear EXPENSE: `account.balance -= amount`
- Al actualizar: revierte balance anterior, aplica nuevo
- Al eliminar: revierte balance

### 5. Goal (goals)

**Bounded Context**: Savings

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Primary Key |
| user_id | Long | FK → users |
| name | String | Nombre meta |
| description | String | Descripción (max 500) |
| target_amount | BigDecimal | Monto objetivo |
| current_amount | BigDecimal | Monto actual |
| deadline | LocalDate | Fecha límite |
| status | Enum | ACTIVE / COMPLETED / CANCELLED |
| celebrated_at | Date | Fecha celebración |
| created_at | LocalDateTime | Fecha creación |
| updated_at | LocalDateTime | Última actualización |

**Campo Calculado**:
```java
progressPercentage = (currentAmount / targetAmount) * 100
```

**Estados**:
- `ACTIVE`: Meta en progreso
- `COMPLETED`: currentAmount >= targetAmount
- `CANCELLED`: Meta cancelada

### 6. Budget (budgets)

**Bounded Context**: Savings

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Primary Key |
| user_id | Long | FK → users |
| category_id | Long | FK → categories |
| limit_amount | BigDecimal | Límite presupuesto |
| spent_amount | BigDecimal | Monto gastado (calculado) |
| period | Enum | WEEKLY / MONTHLY / YEARLY |
| start_date | LocalDate | Fecha inicio |
| end_date | LocalDate | Fecha fin |
| created_at | LocalDateTime | Fecha creación |
| updated_at | LocalDateTime | Última actualización |

**Campos Calculados**:
```java
remainingAmount = limitAmount - spentAmount
spentPercentage = (spentAmount / limitAmount) * 100
isWarning = spentPercentage >= 80
isExceeded = spentAmount > limitAmount
isActive = currentDate between startDate and endDate
```

### 7. RecurringTransaction (recurring_transactions)

**Bounded Context**: Automation

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Primary Key |
| user_id | Long | FK → users |
| account_id | Long | FK → accounts |
| category_id | Long | FK → categories |
| type | Enum | INCOME / EXPENSE |
| amount | BigDecimal | Monto |
| description | String | Descripción |
| frequency | Enum | DAILY / WEEKLY / MONTHLY / YEARLY |
| start_date | LocalDate | Fecha inicio |
| end_date | LocalDate | Fecha fin (opcional) |
| next_execution_date | LocalDate | Próxima ejecución |
| is_active | Boolean | Activa o no |
| created_at | LocalDateTime | Fecha creación |
| updated_at | LocalDateTime | Última actualización |

**Automatización**:
- Scheduler ejecuta cada día
- Crea transacciones automáticamente
- Actualiza `next_execution_date`

### 8. Notification (notifications)

**Bounded Context**: Notifications

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Primary Key |
| user_id | Long | FK → users |
| type | Enum | INFO / WARNING / GOAL / BUDGET |
| title | String | Título |
| message | String | Mensaje |
| is_read | Boolean | Leída o no |
| created_at | LocalDateTime | Fecha creación |

**Tipos de Notificaciones**:
- `INFO`: Información general
- `WARNING`: Advertencias (ej: presupuesto > 80%)
- `GOAL`: Metas completadas
- `BUDGET`: Alertas de presupuesto

### 9. Reminder (reminders)

**Bounded Context**: Reminders

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | Primary Key |
| user_id | Long | FK → users |
| title | String | Título |
| description | String | Descripción |
| due_date | LocalDate | Fecha vencimiento |
| is_completed | Boolean | Completado |
| created_at | LocalDateTime | Fecha creación |

---

## API Endpoints

### Base URL
```
http://localhost:8080/api/v1
```

### Índice de Endpoints

1. [Autenticación](#autenticación-iam)
2. [Usuarios](#usuarios)
3. [Cuentas](#cuentas-accounts)
4. [Categorías](#categorías-categories)
5. [Transacciones](#transacciones-transactions)
6. [Metas](#metas-goals)
7. [Presupuestos](#presupuestos-budgets)
8. [Dashboard](#dashboard-analytics)
9. [Transacciones Recurrentes](#transacciones-recurrentes)
10. [Notificaciones](#notificaciones)
11. [Recordatorios](#recordatorios)

---

### Autenticación (IAM)

#### POST /auth/register
Registra un nuevo usuario.

**Request Body:**
```json
{
  "email": "usuario@ejemplo.com",
  "password": "Password123!",
  "displayName": "Juan Pérez"
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "email": "usuario@ejemplo.com",
  "displayName": "Juan Pérez",
  "photoUrl": null,
  "currency": "PEN",
  "theme": "light",
  "locale": "es",
  "createdAt": "2025-01-15T10:30:00Z",
  "updatedAt": "2025-01-15T10:30:00Z"
}
```

**Validaciones:**
- Email único
- Password mínimo 8 caracteres
- DisplayName requerido

---

#### POST /auth/login
Autentica un usuario y devuelve un token JWT.

**Request Body:**
```json
{
  "email": "usuario@ejemplo.com",
  "password": "Password123!"
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "email": "usuario@ejemplo.com",
  "displayName": "Juan Pérez",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Token Expiration:** 7 días

---

#### POST /auth/forgot-password
Solicita reset de contraseña.

**Request Body:**
```json
{
  "email": "usuario@ejemplo.com"
}
```

**Response:** `200 OK`
```json
{
  "message": "Token de reseteo: abc123xyz (válido por 1 hora)"
}
```

**Nota:** En producción, el token se envía por email.

---

#### POST /auth/reset-password
Resetea la contraseña usando un token.

**Request Body:**
```json
{
  "token": "abc123xyz",
  "newPassword": "NewPassword123!"
}
```

**Response:** `200 OK`
```json
{
  "message": "Contraseña reseteada exitosamente. Ya puedes iniciar sesión con tu nueva contraseña."
}
```

---

### Usuarios

#### GET /users/{id}
Obtiene el perfil de un usuario.

**Headers:** `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
{
  "id": 1,
  "email": "usuario@ejemplo.com",
  "displayName": "Juan Pérez",
  "photoUrl": null,
  "currency": "PEN",
  "theme": "light",
  "locale": "es",
  "createdAt": "2025-01-15T10:30:00Z",
  "updatedAt": "2025-01-15T10:30:00Z"
}
```

---

#### PATCH /users/{id}
Actualiza el perfil del usuario.

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "displayName": "Juan Carlos Pérez",
  "photoUrl": "https://example.com/photo.jpg",
  "currency": "USD",
  "theme": "dark",
  "locale": "en"
}
```

**Response:** `200 OK`

---

#### PATCH /users/{id}/preferences
Actualiza preferencias del usuario.

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "currency": "USD",
  "theme": "dark",
  "locale": "en",
  "notificationsEnabled": true,
  "emailNotifications": false,
  "pushNotifications": true
}
```

**Response:** `200 OK`

---

### Cuentas (Accounts)

#### GET /accounts
Obtiene todas las cuentas del usuario.

**Headers:** `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "Cuenta Principal",
    "currency": "PEN",
    "balance": 1500.00,
    "createdAt": "2025-01-15T10:30:00Z",
    "updatedAt": "2025-01-20T14:20:00Z"
  },
  {
    "id": 2,
    "name": "Ahorros",
    "currency": "PEN",
    "balance": 5000.00,
    "createdAt": "2025-01-16T09:00:00Z",
    "updatedAt": "2025-01-20T14:20:00Z"
  }
]
```

---

#### POST /accounts
Crea una nueva cuenta.

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "name": "Tarjeta de Crédito",
  "currency": "PEN"
}
```

**Response:** `201 Created`
```json
{
  "id": 3,
  "name": "Tarjeta de Crédito",
  "currency": "PEN",
  "balance": 0.00,
  "createdAt": "2025-01-21T10:00:00Z",
  "updatedAt": "2025-01-21T10:00:00Z"
}
```

---

#### DELETE /accounts/{id}
Elimina una cuenta.

**Headers:** `Authorization: Bearer {token}`

**Response:** `204 No Content`

---

### Categorías (Categories)

#### GET /categories
Obtiene todas las categorías del usuario.

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `type` (opcional): `INCOME` o `EXPENSE`

**Ejemplos:**
```
GET /categories
GET /categories?type=EXPENSE
GET /categories?type=INCOME
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "Alimentación",
    "type": "EXPENSE",
    "icon": "pi-shopping-cart",
    "color": "#FF6B6B",
    "isSystemCategory": true
  },
  {
    "id": 2,
    "name": "Transporte",
    "type": "EXPENSE",
    "icon": "pi-car",
    "color": "#4ECDC4",
    "isSystemCategory": true
  }
]
```

---

#### POST /categories
Crea una nueva categoría personalizada.

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "name": "Entretenimiento",
  "type": "EXPENSE",
  "icon": "pi-play",
  "color": "#95E1D3"
}
```

**Response:** `201 Created`

---

#### DELETE /categories/{id}
Elimina una categoría personalizada.

**Headers:** `Authorization: Bearer {token}`

**Response:** `204 No Content`

**Restricción:** No se pueden eliminar categorías del sistema.

---

### Transacciones (Transactions)

#### GET /transactions
Obtiene transacciones con filtros y paginación.

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `type` (opcional): `INCOME`, `EXPENSE`, `TRANSFER`
- `categoryId` (opcional): ID de categoría
- `fromDate` (opcional): Fecha desde (formato: `yyyy-MM-dd`)
- `toDate` (opcional): Fecha hasta (formato: `yyyy-MM-dd`)
- `page` (default: 0): Número de página
- `size` (default: 20): Tamaño de página

**Ejemplos:**
```
GET /transactions
GET /transactions?type=EXPENSE&page=0&size=20
GET /transactions?categoryId=1&fromDate=2025-01-01&toDate=2025-01-31
```

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": 100,
      "accountId": 1,
      "accountName": "Cuenta Principal",
      "categoryId": 1,
      "categoryName": "Alimentación",
      "categoryIcon": "pi-shopping-cart",
      "type": "EXPENSE",
      "amount": 45.50,
      "description": "Almuerzo en campus",
      "transactionDate": "2025-01-20",
      "createdAt": "2025-01-20T13:30:00Z",
      "updatedAt": "2025-01-20T13:30:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": false,
      "unsorted": true,
      "empty": true
    }
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true,
  "size": 20,
  "number": 0,
  "numberOfElements": 1,
  "empty": false
}
```

---

#### GET /transactions/{id}
Obtiene una transacción por ID.

**Headers:** `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
{
  "id": 100,
  "accountId": 1,
  "accountName": "Cuenta Principal",
  "categoryId": 1,
  "categoryName": "Alimentación",
  "categoryIcon": "pi-shopping-cart",
  "type": "EXPENSE",
  "amount": 45.50,
  "description": "Almuerzo en campus",
  "transactionDate": "2025-01-20",
  "createdAt": "2025-01-20T13:30:00Z",
  "updatedAt": "2025-01-20T13:30:00Z"
}
```

---

#### POST /transactions
Crea una nueva transacción.

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "accountId": 1,
  "categoryId": 1,
  "type": "EXPENSE",
  "amount": 45.50,
  "description": "Almuerzo en campus",
  "transactionDate": "2025-01-20"
}
```

**Response:** `201 Created`

**Efecto Secundario:**
- Si `type = EXPENSE`: `account.balance -= amount`
- Si `type = INCOME`: `account.balance += amount`

---

#### PUT /transactions/{id}
Actualiza una transacción existente.

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "accountId": 1,
  "categoryId": 1,
  "amount": 50.00,
  "description": "Almuerzo + postre",
  "transactionDate": "2025-01-20"
}
```

**Response:** `200 OK`

**Lógica:**
1. Revierte el balance anterior de la cuenta
2. Aplica los nuevos valores
3. Actualiza el balance de la cuenta

---

#### DELETE /transactions/{id}
Elimina una transacción.

**Headers:** `Authorization: Bearer {token}`

**Response:** `204 No Content`

**Efecto Secundario:**
- Revierte el balance de la cuenta

---

### Metas (Goals)

#### GET /goals
Obtiene todas las metas del usuario.

**Headers:** `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "Laptop nueva",
    "description": "Para estudios",
    "targetAmount": 2500.00,
    "currentAmount": 850.00,
    "progressPercentage": 34.00,
    "deadline": "2025-06-30",
    "status": "ACTIVE",
    "celebratedAt": null,
    "createdAt": "2025-01-10T10:00:00Z",
    "updatedAt": "2025-01-20T15:00:00Z"
  }
]
```

---

#### POST /goals
Crea una nueva meta.

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "name": "Laptop nueva",
  "description": "Para estudios",
  "targetAmount": 2500.00,
  "deadline": "2025-06-30"
}
```

**Response:** `201 Created`

---

#### PATCH /goals/{id}/progress
Actualiza el progreso de una meta.

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "currentAmount": 1050.00
}
```

**Response:** `200 OK`

**Lógica:**
- Si `currentAmount >= targetAmount`: `status = COMPLETED`

---

#### POST /goals/{id}/celebrate
Marca una meta completada como celebrada.

**Headers:** `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
{
  "id": 1,
  "name": "Laptop nueva",
  "status": "COMPLETED",
  "celebratedAt": "2025-05-15T18:00:00Z"
}
```

---

#### DELETE /goals/{id}
Elimina una meta.

**Headers:** `Authorization: Bearer {token}`

**Response:** `204 No Content`

---

### Presupuestos (Budgets)

#### GET /budgets
Obtiene todos los presupuestos del usuario.

**Headers:** `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "categoryId": 1,
    "categoryName": "Alimentación",
    "limitAmount": 400.00,
    "spentAmount": 340.00,
    "remainingAmount": 60.00,
    "spentPercentage": 85.00,
    "period": "MONTHLY",
    "startDate": "2025-01-01",
    "endDate": "2025-01-31",
    "isWarning": true,
    "isExceeded": false,
    "isActive": true,
    "createdAt": "2025-01-01T00:00:00Z",
    "updatedAt": "2025-01-20T14:00:00Z"
  }
]
```

**Campos Calculados:**
- `spentAmount`: Suma de transacciones EXPENSE en la categoría
- `remainingAmount`: `limitAmount - spentAmount`
- `spentPercentage`: `(spentAmount / limitAmount) * 100`
- `isWarning`: `spentPercentage >= 80`
- `isExceeded`: `spentAmount > limitAmount`
- `isActive`: Fecha actual entre `startDate` y `endDate`

---

#### POST /budgets
Crea un nuevo presupuesto.

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "categoryId": 1,
  "limitAmount": 400.00,
  "period": "MONTHLY",
  "startDate": "2025-02-01",
  "endDate": "2025-02-28"
}
```

**Response:** `201 Created`

---

#### DELETE /budgets/{id}
Elimina un presupuesto.

**Headers:** `Authorization: Bearer {token}`

**Response:** `204 No Content`

---

### Dashboard (Analytics)

#### GET /dashboard/pulse
Obtiene el pulso financiero del período.

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `fromDate` (opcional): Fecha desde (default: inicio del mes)
- `toDate` (opcional): Fecha hasta (default: hoy)

**Ejemplo:**
```
GET /dashboard/pulse
GET /dashboard/pulse?fromDate=2025-01-01&toDate=2025-01-31
```

**Response:** `200 OK`
```json
{
  "currency": "PEN",
  "periodLabel": "JANUARY 2025",
  "totalIncome": 1200.00,
  "totalExpenses": 850.00,
  "balance": 350.00,
  "savingsRate": 29.17
}
```

**Cálculos:**
- `balance = totalIncome - totalExpenses`
- `savingsRate = (balance / totalIncome) * 100`

---

#### GET /dashboard/trends
Obtiene tendencias mensuales de ingresos y gastos.

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `months` (default: 6): Número de meses hacia atrás

**Ejemplo:**
```
GET /dashboard/trends?months=6
```

**Response:** `200 OK`
```json
{
  "currency": "PEN",
  "series": [
    {
      "month": "2024-08",
      "income": 1000.00,
      "expenses": 750.00,
      "balance": 250.00
    },
    {
      "month": "2024-09",
      "income": 1100.00,
      "expenses": 820.00,
      "balance": 280.00
    },
    {
      "month": "2024-10",
      "income": 1150.00,
      "expenses": 900.00,
      "balance": 250.00
    }
  ]
}
```

---

#### GET /dashboard/leaks
Obtiene las categorías con mayor gasto (gastos hormiga).

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `fromDate` (opcional): Fecha desde (default: inicio del mes)
- `toDate` (opcional): Fecha hasta (default: hoy)
- `top` (default: 5): Número de categorías top

**Ejemplo:**
```
GET /dashboard/leaks?top=5
GET /dashboard/leaks?fromDate=2025-01-01&toDate=2025-01-31&top=3
```

**Response:** `200 OK`
```json
{
  "currency": "PEN",
  "period": "JANUARY 2025",
  "leaks": [
    {
      "categoryId": 1,
      "categoryName": "Alimentación",
      "categoryIcon": "pi-shopping-cart",
      "amount": 340.00,
      "percentage": 40.00,
      "color": "#FF6B6B"
    },
    {
      "categoryId": 2,
      "categoryName": "Transporte",
      "categoryIcon": "pi-car",
      "amount": 250.00,
      "percentage": 29.41,
      "color": "#4ECDC4"
    }
  ]
}
```

---

### Transacciones Recurrentes

#### GET /recurring-transactions
Obtiene transacciones recurrentes del usuario.

**Headers:** `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "accountId": 1,
    "accountName": "Cuenta Principal",
    "categoryId": 5,
    "categoryName": "Sueldo",
    "type": "INCOME",
    "amount": 1200.00,
    "description": "Sueldo mensual",
    "frequency": "MONTHLY",
    "startDate": "2025-01-05",
    "endDate": null,
    "nextExecutionDate": "2025-02-05",
    "isActive": true
  }
]
```

---

#### POST /recurring-transactions
Crea una transacción recurrente.

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "accountId": 1,
  "categoryId": 5,
  "type": "INCOME",
  "amount": 1200.00,
  "description": "Sueldo mensual",
  "frequency": "MONTHLY",
  "startDate": "2025-01-05",
  "endDate": null
}
```

**Valores de `frequency`:**
- `DAILY`
- `WEEKLY`
- `MONTHLY`
- `YEARLY`

**Response:** `201 Created`

---

#### PATCH /recurring-transactions/{id}/status
Activa o desactiva una transacción recurrente.

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "isActive": false
}
```

**Response:** `200 OK`

---

#### DELETE /recurring-transactions/{id}
Elimina una transacción recurrente.

**Headers:** `Authorization: Bearer {token}`

**Response:** `204 No Content`

---

### Notificaciones

#### GET /notifications
Obtiene todas las notificaciones del usuario.

**Headers:** `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "type": "WARNING",
    "title": "Presupuesto al 85%",
    "message": "Tu presupuesto de Alimentación está al 85% de su límite",
    "isRead": false,
    "createdAt": "2025-01-20T10:00:00Z"
  },
  {
    "id": 2,
    "type": "GOAL",
    "title": "Meta completada",
    "message": "¡Felicidades! Completaste la meta 'Laptop nueva'",
    "isRead": true,
    "createdAt": "2025-01-18T15:30:00Z"
  }
]
```

---

#### GET /notifications/unread
Obtiene notificaciones no leídas.

**Headers:** `Authorization: Bearer {token}`

**Response:** `200 OK`

---

#### PATCH /notifications/{id}/read
Marca una notificación como leída.

**Headers:** `Authorization: Bearer {token}`

**Response:** `200 OK`

---

#### DELETE /notifications/{id}
Elimina una notificación.

**Headers:** `Authorization: Bearer {token}`

**Response:** `204 No Content`

---

### Recordatorios

#### GET /reminders
Obtiene todos los recordatorios del usuario.

**Headers:** `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "title": "Pagar tarjeta de crédito",
    "description": "Vence el 25 de cada mes",
    "dueDate": "2025-01-25",
    "isCompleted": false,
    "createdAt": "2025-01-15T10:00:00Z"
  }
]
```

---

#### POST /reminders
Crea un nuevo recordatorio.

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "title": "Pagar tarjeta de crédito",
  "description": "Vence el 25 de cada mes",
  "dueDate": "2025-01-25"
}
```

**Response:** `201 Created`

---

#### PATCH /reminders/{id}/complete
Marca un recordatorio como completado.

**Headers:** `Authorization: Bearer {token}`

**Response:** `200 OK`

---

#### DELETE /reminders/{id}
Elimina un recordatorio.

**Headers:** `Authorization: Bearer {token}`

**Response:** `204 No Content`

---

## Guía de Integración para Frontend

### 1. Configuración Inicial

#### URLs del Backend

```typescript
// config/api.config.ts
export const API_CONFIG = {
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
};
```

**Producción:**
- Configurar variable de entorno `REACT_APP_API_URL` con la URL de Heroku
- Ejemplo: `https://tucash-api.herokuapp.com/api/v1`

---

### 2. Configuración de CORS

El backend acepta peticiones de:
- `http://localhost:4200` (Angular)
- `http://localhost:5173` (Vite - React/Vue)

**Para producción:**
Agregar tu dominio frontend en Heroku:
```bash
heroku config:set CORS_ALLOWED_ORIGINS="https://tu-frontend.com"
```

---

### 3. Autenticación con JWT

#### Flujo de Autenticación

**1. Login:**
```typescript
// services/auth.service.ts
async function login(email: string, password: string) {
  const response = await fetch(`${API_CONFIG.baseURL}/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ email, password })
  });

  if (!response.ok) {
    throw new Error('Credenciales inválidas');
  }

  const data = await response.json();

  // Guardar token en localStorage o sessionStorage
  localStorage.setItem('token', data.token);
  localStorage.setItem('user', JSON.stringify({
    id: data.id,
    email: data.email,
    displayName: data.displayName
  }));

  return data;
}
```

**2. Registro:**
```typescript
async function register(email: string, password: string, displayName: string) {
  const response = await fetch(`${API_CONFIG.baseURL}/auth/register`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ email, password, displayName })
  });

  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || 'Error al registrar usuario');
  }

  return await response.json();
}
```

---

### 4. Hacer Peticiones Autenticadas

**TODAS las peticiones (excepto login/register) deben incluir el token JWT.**

#### Opción A: Fetch API

```typescript
// utils/api.utils.ts
export async function authenticatedFetch(url: string, options: RequestInit = {}) {
  const token = localStorage.getItem('token');

  if (!token) {
    throw new Error('No autenticado');
  }

  const response = await fetch(`${API_CONFIG.baseURL}${url}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
      ...options.headers
    }
  });

  if (response.status === 401) {
    // Token expirado o inválido
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/login';
    throw new Error('Sesión expirada');
  }

  if (!response.ok) {
    throw new Error(`Error: ${response.status}`);
  }

  return await response.json();
}

// Uso:
const transactions = await authenticatedFetch('/transactions');
```

#### Opción B: Axios (Recomendado)

```typescript
// utils/axios.config.ts
import axios from 'axios';

const api = axios.create({
  baseURL: API_CONFIG.baseURL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Interceptor para agregar token automáticamente
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor para manejar errores 401
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;

// Uso:
import api from './utils/axios.config';

const getTransactions = async () => {
  const response = await api.get('/transactions');
  return response.data;
};
```

---

### 5. Servicios de API por Módulo

#### Servicio de Autenticación

```typescript
// services/auth.service.ts
import api from '../utils/axios.config';

export const authService = {
  login: async (email: string, password: string) => {
    const { data } = await api.post('/auth/login', { email, password });
    localStorage.setItem('token', data.token);
    return data;
  },

  register: async (email: string, password: string, displayName: string) => {
    const { data } = await api.post('/auth/register', {
      email,
      password,
      displayName
    });
    return data;
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  getCurrentUser: () => {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  },

  isAuthenticated: () => {
    return !!localStorage.getItem('token');
  }
};
```

#### Servicio de Transacciones

```typescript
// services/transaction.service.ts
import api from '../utils/axios.config';

export interface TransactionFilter {
  type?: 'INCOME' | 'EXPENSE' | 'TRANSFER';
  categoryId?: number;
  fromDate?: string;  // formato: yyyy-MM-dd
  toDate?: string;
  page?: number;
  size?: number;
}

export interface CreateTransactionDTO {
  accountId: number;
  categoryId: number;
  type: 'INCOME' | 'EXPENSE' | 'TRANSFER';
  amount: number;
  description?: string;
  transactionDate: string;  // formato: yyyy-MM-dd
}

export const transactionService = {
  getAll: async (filters: TransactionFilter = {}) => {
    const params = new URLSearchParams();

    if (filters.type) params.append('type', filters.type);
    if (filters.categoryId) params.append('categoryId', filters.categoryId.toString());
    if (filters.fromDate) params.append('fromDate', filters.fromDate);
    if (filters.toDate) params.append('toDate', filters.toDate);
    params.append('page', (filters.page || 0).toString());
    params.append('size', (filters.size || 20).toString());

    const { data } = await api.get(`/transactions?${params.toString()}`);
    return data;
  },

  getById: async (id: number) => {
    const { data } = await api.get(`/transactions/${id}`);
    return data;
  },

  create: async (transaction: CreateTransactionDTO) => {
    const { data } = await api.post('/transactions', transaction);
    return data;
  },

  update: async (id: number, transaction: Partial<CreateTransactionDTO>) => {
    const { data } = await api.put(`/transactions/${id}`, transaction);
    return data;
  },

  delete: async (id: number) => {
    await api.delete(`/transactions/${id}`);
  }
};
```

#### Servicio de Cuentas

```typescript
// services/account.service.ts
import api from '../utils/axios.config';

export interface CreateAccountDTO {
  name: string;
  currency: string;
}

export const accountService = {
  getAll: async () => {
    const { data } = await api.get('/accounts');
    return data;
  },

  create: async (account: CreateAccountDTO) => {
    const { data } = await api.post('/accounts', account);
    return data;
  },

  delete: async (id: number) => {
    await api.delete(`/accounts/${id}`);
  }
};
```

#### Servicio de Categorías

```typescript
// services/category.service.ts
import api from '../utils/axios.config';

export interface CreateCategoryDTO {
  name: string;
  type: 'INCOME' | 'EXPENSE';
  icon?: string;
  color?: string;
}

export const categoryService = {
  getAll: async (type?: 'INCOME' | 'EXPENSE') => {
    const url = type ? `/categories?type=${type}` : '/categories';
    const { data } = await api.get(url);
    return data;
  },

  create: async (category: CreateCategoryDTO) => {
    const { data } = await api.post('/categories', category);
    return data;
  },

  delete: async (id: number) => {
    await api.delete(`/categories/${id}`);
  }
};
```

#### Servicio de Metas

```typescript
// services/goal.service.ts
import api from '../utils/axios.config';

export interface CreateGoalDTO {
  name: string;
  description?: string;
  targetAmount: number;
  deadline: string;  // formato: yyyy-MM-dd
}

export const goalService = {
  getAll: async () => {
    const { data } = await api.get('/goals');
    return data;
  },

  getById: async (id: number) => {
    const { data } = await api.get(`/goals/${id}`);
    return data;
  },

  create: async (goal: CreateGoalDTO) => {
    const { data } = await api.post('/goals', goal);
    return data;
  },

  updateProgress: async (id: number, currentAmount: number) => {
    const { data } = await api.patch(`/goals/${id}/progress`, { currentAmount });
    return data;
  },

  celebrate: async (id: number) => {
    const { data } = await api.post(`/goals/${id}/celebrate`);
    return data;
  },

  delete: async (id: number) => {
    await api.delete(`/goals/${id}`);
  }
};
```

#### Servicio de Dashboard

```typescript
// services/dashboard.service.ts
import api from '../utils/axios.config';

export const dashboardService = {
  getPulse: async (fromDate?: string, toDate?: string) => {
    const params = new URLSearchParams();
    if (fromDate) params.append('fromDate', fromDate);
    if (toDate) params.append('toDate', toDate);

    const url = params.toString() ? `/dashboard/pulse?${params}` : '/dashboard/pulse';
    const { data } = await api.get(url);
    return data;
  },

  getTrends: async (months: number = 6) => {
    const { data } = await api.get(`/dashboard/trends?months=${months}`);
    return data;
  },

  getLeaks: async (fromDate?: string, toDate?: string, top: number = 5) => {
    const params = new URLSearchParams();
    if (fromDate) params.append('fromDate', fromDate);
    if (toDate) params.append('toDate', toDate);
    params.append('top', top.toString());

    const { data } = await api.get(`/dashboard/leaks?${params}`);
    return data;
  }
};
```

---

### 6. Manejo de Fechas

**IMPORTANTE:** El backend espera fechas en formato `yyyy-MM-dd` (ISO 8601 date).

```typescript
// utils/date.utils.ts
export function formatDateForAPI(date: Date): string {
  return date.toISOString().split('T')[0];
}

export function parseAPIDate(dateString: string): Date {
  return new Date(dateString);
}

// Ejemplo de uso:
const today = new Date();
const formattedDate = formatDateForAPI(today);  // "2025-01-20"

const transaction = {
  accountId: 1,
  categoryId: 2,
  type: 'EXPENSE',
  amount: 50.00,
  description: 'Compra',
  transactionDate: formattedDate  // Usar fecha formateada
};
```

---

### 7. Manejo de Errores

```typescript
// utils/error.handler.ts
export function handleAPIError(error: any): string {
  if (error.response) {
    // Error de respuesta del servidor
    const status = error.response.status;
    const message = error.response.data?.message;

    switch (status) {
      case 400:
        return message || 'Datos inválidos';
      case 401:
        return 'No autenticado. Inicia sesión nuevamente';
      case 404:
        return 'Recurso no encontrado';
      case 409:
        return message || 'Conflicto de datos';
      case 500:
        return 'Error interno del servidor';
      default:
        return `Error: ${status}`;
    }
  } else if (error.request) {
    // No hay respuesta del servidor
    return 'No se pudo conectar con el servidor';
  } else {
    // Error en la configuración de la petición
    return error.message || 'Error desconocido';
  }
}

// Uso en componentes:
try {
  await transactionService.create(newTransaction);
  toast.success('Transacción creada');
} catch (error) {
  const errorMessage = handleAPIError(error);
  toast.error(errorMessage);
}
```

---

### 8. Tipos TypeScript (Interfaces)

```typescript
// types/api.types.ts

export interface User {
  id: number;
  email: string;
  displayName: string;
  photoUrl?: string;
  currency: string;
  theme: string;
  locale: string;
  createdAt: string;
  updatedAt: string;
}

export interface AuthResponse {
  id: number;
  email: string;
  displayName: string;
  token: string;
}

export interface Account {
  id: number;
  name: string;
  currency: string;
  balance: number;
  createdAt: string;
  updatedAt: string;
}

export interface Category {
  id: number;
  name: string;
  type: 'INCOME' | 'EXPENSE';
  icon: string;
  color: string;
  isSystemCategory: boolean;
}

export interface Transaction {
  id: number;
  accountId: number;
  accountName: string;
  categoryId: number;
  categoryName: string;
  categoryIcon: string;
  type: 'INCOME' | 'EXPENSE' | 'TRANSFER';
  amount: number;
  description?: string;
  transactionDate: string;
  createdAt: string;
  updatedAt: string;
}

export interface Goal {
  id: number;
  name: string;
  description?: string;
  targetAmount: number;
  currentAmount: number;
  progressPercentage: number;
  deadline: string;
  status: 'ACTIVE' | 'COMPLETED' | 'CANCELLED';
  celebratedAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Budget {
  id: number;
  categoryId: number;
  categoryName: string;
  limitAmount: number;
  spentAmount: number;
  remainingAmount: number;
  spentPercentage: number;
  period: 'WEEKLY' | 'MONTHLY' | 'YEARLY';
  startDate: string;
  endDate: string;
  isWarning: boolean;
  isExceeded: boolean;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface DashboardPulse {
  currency: string;
  periodLabel: string;
  totalIncome: number;
  totalExpenses: number;
  balance: number;
  savingsRate: number;
}

export interface MonthlyTrend {
  month: string;
  income: number;
  expenses: number;
  balance: number;
}

export interface TrendSeries {
  currency: string;
  series: MonthlyTrend[];
}

export interface CategoryLeak {
  categoryId: number;
  categoryName: string;
  categoryIcon: string;
  amount: number;
  percentage: number;
  color: string;
}

export interface CategoryLeaks {
  currency: string;
  period: string;
  leaks: CategoryLeak[];
}

export interface Notification {
  id: number;
  type: 'INFO' | 'WARNING' | 'GOAL' | 'BUDGET';
  title: string;
  message: string;
  isRead: boolean;
  createdAt: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      sorted: boolean;
      unsorted: boolean;
      empty: boolean;
    };
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
  size: number;
  number: number;
  numberOfElements: number;
  empty: boolean;
}
```

---

### 9. Ejemplo de Uso en React

```typescript
// components/TransactionList.tsx
import { useState, useEffect } from 'react';
import { transactionService } from '../services/transaction.service';
import { Transaction } from '../types/api.types';
import { handleAPIError } from '../utils/error.handler';

export function TransactionList() {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadTransactions();
  }, []);

  const loadTransactions = async () => {
    try {
      setLoading(true);
      const response = await transactionService.getAll({
        page: 0,
        size: 20
      });
      setTransactions(response.content);
      setError(null);
    } catch (err) {
      setError(handleAPIError(err));
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await transactionService.delete(id);
      await loadTransactions();  // Recargar lista
    } catch (err) {
      alert(handleAPIError(err));
    }
  };

  if (loading) return <div>Cargando...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div>
      {transactions.map(transaction => (
        <div key={transaction.id}>
          <span>{transaction.categoryName}</span>
          <span>{transaction.amount}</span>
          <button onClick={() => handleDelete(transaction.id)}>
            Eliminar
          </button>
        </div>
      ))}
    </div>
  );
}
```

---

### 10. Rutas Protegidas (React Router)

```typescript
// components/ProtectedRoute.tsx
import { Navigate } from 'react-router-dom';
import { authService } from '../services/auth.service';

interface Props {
  children: React.ReactNode;
}

export function ProtectedRoute({ children }: Props) {
  if (!authService.isAuthenticated()) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
}

// App.tsx
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { ProtectedRoute } from './components/ProtectedRoute';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        <Route path="/" element={
          <ProtectedRoute>
            <Dashboard />
          </ProtectedRoute>
        } />

        <Route path="/transactions" element={
          <ProtectedRoute>
            <TransactionsPage />
          </ProtectedRoute>
        } />
      </Routes>
    </BrowserRouter>
  );
}
```

---

### 11. Consideraciones de Seguridad

**DO's:**
- ✅ Siempre incluir el token JWT en peticiones autenticadas
- ✅ Guardar el token en `localStorage` o `sessionStorage`
- ✅ Validar respuestas del servidor antes de procesar
- ✅ Manejar errores 401 y redirigir a login
- ✅ Usar HTTPS en producción
- ✅ Sanitizar inputs del usuario

**DON'Ts:**
- ❌ No guardar el token en cookies sin `httpOnly`
- ❌ No exponer el token en URLs
- ❌ No enviar contraseñas en texto plano
- ❌ No confiar en validaciones solo del frontend
- ❌ No ignorar errores de CORS

---

### 12. Testing de la Integración

```typescript
// __tests__/transaction.service.test.ts
import { transactionService } from '../services/transaction.service';

describe('Transaction Service', () => {
  beforeEach(() => {
    // Mock token
    localStorage.setItem('token', 'mock-jwt-token');
  });

  it('should fetch transactions', async () => {
    const transactions = await transactionService.getAll();
    expect(transactions).toBeDefined();
    expect(Array.isArray(transactions.content)).toBe(true);
  });

  it('should create a transaction', async () => {
    const newTransaction = {
      accountId: 1,
      categoryId: 2,
      type: 'EXPENSE' as const,
      amount: 50.00,
      description: 'Test',
      transactionDate: '2025-01-20'
    };

    const created = await transactionService.create(newTransaction);
    expect(created.id).toBeDefined();
    expect(created.amount).toBe(50.00);
  });
});
```

---

### 13. Variables de Entorno

**Archivo `.env` (desarrollo):**
```env
REACT_APP_API_URL=http://localhost:8080/api/v1
REACT_APP_ENV=development
```

**Archivo `.env.production`:**
```env
REACT_APP_API_URL=https://tucash-api.herokuapp.com/api/v1
REACT_APP_ENV=production
```

---

### 14. Checklist de Integración

- [ ] Configurar `axios` con interceptors
- [ ] Implementar servicio de autenticación
- [ ] Guardar y cargar token de localStorage
- [ ] Implementar rutas protegidas
- [ ] Crear servicios para cada módulo (transactions, accounts, etc.)
- [ ] Definir tipos TypeScript
- [ ] Implementar manejo de errores
- [ ] Configurar CORS en backend para tu dominio
- [ ] Probar login/logout
- [ ] Probar CRUD de transacciones
- [ ] Probar dashboard endpoints
- [ ] Implementar refresh de token (opcional)
- [ ] Agregar loading states
- [ ] Agregar error boundaries

---

### 15. Recursos Adicionales

**Documentación del Backend:**
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

**Testing con cURL:**
```bash
# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"Password123!"}'

# Obtener transacciones
curl -X GET http://localhost:8080/api/v1/transactions \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

**Testing con Postman:**
1. Importar colección desde Swagger
2. Configurar variable de entorno `baseURL`
3. Configurar token en Authorization > Bearer Token

---

## Contacto y Soporte

Para dudas o problemas de integración:
- Revisar la documentación de Swagger UI
- Consultar logs del backend: `heroku logs --tail` (producción)
- Verificar configuración de CORS
- Validar formato de fechas y tipos de datos

---

**Última actualización:** 2025-01-15
**Versión del Backend:** 0.0.1-SNAPSHOT
**Versión de Spring Boot:** 3.5.6
