# Guía de Pruebas de Microservicios con Postman

Esta guía detalla los pasos para probar de extremo a extremo el ecosistema de microservicios.

**NOTA IMPORTANTE:** Todas las peticiones deben realizarse al puerto **`8080`** (API Gateway). No contactes a los microservicios directamente por sus puertos individuales (8081, 8082, 8083) para garantizar que el enrutamiento y la seguridad (JWT) se apliquen correctamente.

---

## 1. Auth Service: Seguridad y Autenticación

Antes de poder interactuar con el catálogo o los pedidos, necesitas registrarte e iniciar sesión para obtener un token JWT.

### A. Registro de Usuario
Crea un nuevo usuario en la base de datos.
* **Método:** `POST`
* **URL:** `http://localhost:8080/auth/register`
* **Body (Raw -> JSON):**
  ```json
  {
    "username": "usuario_prueba",
    "password": "mi_clave_segura",
    "role": "USER"
  }
  ```
* **Resultado Esperado:** Un código de estado `200 OK` (o similar que confirme el registro).

### B. Inicio de Sesión (Obtención del Token)
Autentícate con el usuario recién creado para conseguir tu pase de acceso.
* **Método:** `POST`
* **URL:** `http://localhost:8080/auth/login`
* **Body (Raw -> JSON):**
  ```json
  {
    "username": "usuario_prueba",
    "password": "mi_clave_segura"
  }
  ```
* **Resultado Esperado:** Una respuesta que contenga un campo `"token"` con un string largo. **Copia este string**, lo necesitarás para los siguientes pasos.

---

## Configuración del Token en Postman
Para las siguientes peticiones a los servicios de Catálogo y Pedidos, *siempre* debes incluir el token:
1. Ve a la pestaña **Authorization** en tu petición de Postman.
2. Selecciona el tipo **Bearer Token**.
3. Pega el token que copiaste en el paso anterior en el campo correspondiente.

---

## 2. Catalog Service: Gestión de Productos

### A. Crear un Producto
Agrega un artículo al catálogo.
* **Método:** `POST`
* **URL:** `http://localhost:8080/catalog/products`
* **Auth:** Bearer Token (Configurado como se indicó arriba)
* **Body (Raw -> JSON):**
  ```json
  {
    "sku": "PROD-001",
    "name": "Laptop Gamer",
    "price": 1200.50,
    "stock": 10
  }
  ```
* **Resultado Esperado:** El producto creado, y si revisas los logs, un evento se envía a RabbitMQ (dependiendo de la lógica implementada).

### B. Listar Productos
Verifica que el producto se haya creado y obtén su información.
* **Método:** `GET`
* **URL:** `http://localhost:8080/catalog/products`
* **Auth:** Bearer Token
* **Resultado Esperado:** Un arreglo JSON (lista) con los productos, incluyendo el que acabas de crear. ¡Toma nota del `id` o `sku`!

### C. Prueba de Conexión (Ping)
Verifica que el servicio esté respondiendo.
* **Método:** `GET`
* **URL:** `http://localhost:8080/catalog/ping`
* **Auth:** Bearer Token
* **Resultado Esperado:** Mensaje de "Conexión exitosa" o similar. Si quitas el token, debería devolver `401 Unauthorized`.

---

## 3. Order Service: Gestión de Pedidos

### A. Crear una Orden
Realiza un pedido de un producto existente. *Asegúrate de que el producto exista en el Catálogo y tenga stock.*
* **Método:** `POST`
* **URL:** `http://localhost:8080/orders`
* **Auth:** Bearer Token
* **Body (Raw -> JSON):**
  ```json
  {
    "items": [
      {
        "sku": "PROD-001",
        "quantity": 2,
        "price": 1200.50
      }
    ]
  }
  ```
* **Resultado Esperado:** La creación de la orden con estado "PENDING" u "OPEN". Por detrás, este servicio debe comunicarse vía RabbitMQ (o llamada directa) para descontar el stock en el Catalog Service.

### B. Listar Órdenes
Verifica los pedidos que has realizado.
* **Método:** `GET`
* **URL:** `http://localhost:8080/orders`
* **Auth:** Bearer Token
* **Resultado Esperado:** Un arreglo con las órdenes registradas en la base de datos `orderdb`.

### C. Prueba de Conexión (Ping)
Verifica que el servicio de órdenes responda a través del gateway.
* **Método:** `GET`
* **URL:** `http://localhost:8080/orders/ping`
* **Auth:** Bearer Token
* **Resultado Esperado:** Mensaje de éxito si envías el token; error `401` si no lo envías.

---

## Pruebas Adicionales Sugeridas
Para confirmar que todo el ecosistema (y eventos) funciona correctamente:
1. Crea un producto con 10 unidades de stock (Catalog).
2. Crea una orden para comprar 3 unidades de ese producto (Order).
3. Vuelve a consultar la lista de productos (`GET /catalog/products`) y verifica que el stock haya bajado a 7 (prueba la integración vía RabbitMQ).
