# 🚀 Plataforma de E-Commerce (Microservicios)

Este proyecto es una plataforma de comercio electrónico basada en una arquitectura de microservicios desarrollada con **Spring Boot 3**, **Java 21**, **PostgreSQL** y **RabbitMQ**.

## 🏗 Arquitectura del Sistema

El sistema está dividido en los siguientes módulos, todos enrutados a través de un **API Gateway**:

| Componente | Carpeta | Puerto Local | Base de Datos (Docker) | Descripción |
| :--- | :--- | :--- | :--- | :--- |
| **API Gateway** | `services/api-gateway` | `8080` | N/A | Punto de entrada único para los clientes. Enruta y valida tokens JWT. |
| **Auth Service** | `services/auth-service` | `8081` | `Auth_DB` (`:5433`) | Emite tokens JWT y gestiona el registro y login de usuarios (`/auth/**`). |
| **Catalog Service** | `services/catalog-service` | `8082` | `Catalog_DB` (`:5434`) | Gestiona el CRUD de productos, el stock y escucha eventos de órdenes (`/catalog/**`). |
| **Order Service** | `services/order-service` | `8083` | `Order_DB` (`:5435`) | Procesamiento de órdenes. Emite eventos asíncronos para actualizar el catálogo (`/orders/**`). |
| **Frontend** | `mi-frontend` | `4200` | N/A | Aplicación SPA construida en Angular para la interacción del usuario final. |

---

## 🛠 Tecnologías Utilizadas

*   **Lenguaje:** Java 21 / TypeScript
*   **Frameworks Backend:** Spring Boot 3.2.x, Spring Cloud (Gateway, OpenFeign)
*   **Framework Frontend:** Angular 17+
*   **Persistencia:** PostgreSQL, Spring Data JPA, Hibernate
*   **Mensajería Asíncrona:** RabbitMQ (Publicación/Suscripción y colas)
*   **Seguridad:** Spring Security con JWT (JSON Web Tokens)
*   **Infraestructura:** Docker & Docker Compose

---

## 🚀 Cómo Ejecutar el Proyecto Localmente

### 1. Iniciar Infraestructura (Bases de datos y RabbitMQ)
Asegúrate de tener Docker corriendo en tu sistema.
Desde la raíz del proyecto (`reto-microservicios/reto-microservicios`):
```bash
docker-compose up -d
```
*Esto levantará 3 contenedores de PostgreSQL y 1 contenedor de RabbitMQ.*

### 2. Ejecutar los Microservicios Backend
Abre una terminal por cada uno de los microservicios y ejecuta el comando de inicio en su respectivo directorio:

**API Gateway:**
```bash
cd services/api-gateway
mvn spring-boot:run
```

**Auth Service:**
```bash
cd services/auth-service
mvn spring-boot:run
```

**Catalog Service:**
```bash
cd services/catalog-service
mvn spring-boot:run
```

**Order Service:**
```bash
cd services/order-service
mvn spring-boot:run
```

### 3. Ejecutar el Frontend
Desde el directorio del frontend, instala las dependencias y corre el servidor de desarrollo:
```bash
cd mi-frontend
npm install
npm run start
```
El frontend estará disponible en `http://localhost:4200`.

---

## 🔒 Autenticación y Endpoints Principales

Para interactuar con el sistema a través de herramientas como Postman, recuerda siempre pasar por el **API Gateway en el puerto 8080**:

### Obtener Token
1. **Crear usuario:** `POST http://localhost:8080/auth/register` (Enviar JSON con username y password).
2. **Login:** `POST http://localhost:8080/auth/login` (Obtienes el Token JWT).

### Usar Endpoints Protegidos
Agrega el token recibido en la cabecera (Header) de tus peticiones HTTP:
*   `Authorization: Bearer <TU_TOKEN>`

#### Catálogo y Órdenes
*   **Crear Producto:** `POST http://localhost:8080/catalog/products` (Requiere rol ADMIN).
*   **Listar Productos:** `GET http://localhost:8080/catalog/products` (No requiere token).
*   **Crear Orden:** `POST http://localhost:8080/orders` (Requiere Token).
*   **Cancelar Orden:** `DELETE http://localhost:8080/orders/{id}` (Requiere Token. Dispara evento a RabbitMQ para reponer stock).
