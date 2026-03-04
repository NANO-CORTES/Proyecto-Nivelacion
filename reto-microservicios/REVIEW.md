# Guía de Revisión - Sprint 2 🚀
**Sprint 2 – Dominio y Comunicación - Eventos**

Este documento organiza la **Daily Scrum** y la **Sprint Review** para nuestro equipo de 7 personas, enfocado exclusivamente en las Épicas, Historias de Usuario (HU) y Criterios de Aceptación correspondientes a este Sprint.

---

## � Objetivo del Sprint
Implementar los dominios principales (Catálogo y Pedidos) con sus respectivas bases de datos independientes, y establecer un ecosistema de comunicación estable usando integraciones síncronas (REST) y asíncronas (Eventos vía RabbitMQ).

---

## 👥 División de la Presentación (7 Personas)

Hemos dividido el Sprint Backlog para que cada uno de los 7 integrantes exponga un punto clave de desarrollo, validando los Criterios de Aceptación frente a los stakeholders.

### 👤 Persona 1: Arquitectura Base y Épica 3 (Catalog Service)
**Responsabilidad: HU3 – Crear Catálogo Services (Parte 1: Infraestructura)**
* **Contexto:** "Como usuario quiero crear el catálogo para registrar inventario y stock."
* **Qué debe explicar:**
  * La creación del `Catalog Service` como proyecto independiente.
  * La configuración de su propia **base de datos independiente** (`catalogdb`).
  * Demostrar que el Catalog Service levanta correctamente y se conecta a la BD.

### 👤 Persona 2: Gestión de Productos (Catalog Service)
**Responsabilidad: HU3 – Crear Catálogo Services (Parte 2: Operaciones)**
* **Contexto (Criterios de Aceptación):** Crear productos, modificar productos, gestionar y mantener las cantidades disponibles.
* **Qué debe explicar y demostrar:**
  * La implementación del CRUD completo de productos.
  * Hacer una prueba en vivo (Postman/Swagger) creando un producto nuevo, listándolo y modificando sus datos y stock inicial.

### 👤 Persona 3: Épica 4 (Gestión de Pedidos)
**Responsabilidad: HU4 – Crear Pedido**
* **Contexto:** "Como usuario quiero crear pedido para registrar una compra."
* **Qué debe explicar y demostrar:**
  * La creación del `Order Service` y su persistencia en una base de datos exclusiva (`ordersdb`).
  * El modelo de dominio principal (Pedido y Estados).
  * Demostrar vía Postman la creación de una orden, mostrar que el código de respuesta HTTP es **200 OK** y que el pedido insertado queda con el estado inicial **`CREATED`**.

### 👤 Persona 4: Comunicación Síncrona (Validación)
**Responsabilidad: HU5 – Validar Stock**
* **Contexto:** "Como Orders Service quiero validar stock vía REST para evitar pedidos inválidos."
* **Qué debe explicar y demostrar:**
  * La integración síncrona: Cómo el *Orders Service* hace una petición **REST** (`check-stock`) al *Catalog Service* ANTES de aprobar el pedido.
  * Demostrar el uso del propagado **CorrelationId** a lo largo de esta llamada (trazabilidad de logs).
  * **Prueba crítica:** Intentar crear un pedido de un producto que NO tiene inventario suficiente y demostrar que el sistema rechaza la petición respondiendo con el código **`409 Conflict`**.

### 👤 Persona 5: Épica 5 (Eventos - Configuración Inicial)
**Responsabilidad: Preparación para HU6 y HU7**
* **Contexto:** Introducción a la arquitectura orientada a eventos.
* **Qué debe explicar:**
  * La configuración e inicialización de **RabbitMQ** en nuestro stack tecnológico.
  * Mostrar el panel de control de RabbitMQ (si es posible) resaltando las colas creadas (Ej. `order-events`).
  * Explicar teóricamente por qué *Orders* y *Catalog* necesitan esta comunicación asíncrona además del chequeo REST inicial.

### 👤 Persona 6: Eventos - Publicación (Order Service)
**Responsabilidad: HU6 – Publicar Evento `order.created`**
* **Contexto:** "Como Orders Service quiero publicar evento para notificar al sistema."
* **Qué debe explicar y demostrar:**
  * La lógica en *Order Service*: Justo después de guardar en `ordersdb` la orden como `CREATED`, el servicio envía un mensaje a RabbitMQ.
  * Mostrar logs o fragmentos de código donde se asegure que el evento contenga el **`eventId`** (identificador único del evento) y el **`correlationId`** (para rastreo).

### 👤 Persona 7: Eventos - Consumo Idempotente (Catalog Service)
**Responsabilidad: HU7 – Consumir Evento Idempotente**
* **Contexto:** "Como Catalog quiero consumir eventos para actualizar stock sin duplicar." (Criterios de Reposición/Descuento de la HU3 atados a la HU7).
* **Qué debe explicar y demostrar:**
  * Cómo *Catalog Service* escucha en RabbitMQ el evento de `order.created`.
  * Explicar qué es la **Idempotencia**: Al consumir el evento, descuenta de forma duradera el stock de los ítems vendidos.
  * Mostrar la **Tabla `processed_events`** en `catalogdb` y explicar cómo al guardar ahí el `eventId`, se garantiza que si RabbitMQ envía el mensaje dos veces por error, el sistema consulta esa tabla, detecta que ya se procesó **y NO descuenta el stock dos veces**.

---

## 📝 Dinámica para la Daily Scrum
Cada persona responderá en la Daily:
1. **¿Qué hice ayer?** (Ej. "Desarrollé la HU4 sobre el estado de la Orden").
2. **¿Qué haré hoy?** (Ej. "Integrar mi rama con la HU5 para que el check-stock corra junto con mi código").
3. **¿Tengo algún bloqueo?** (Ej. "Tengo problemas con el CorrelationId en las cabeceras REST, necesito revisar eso con Persona 4").
