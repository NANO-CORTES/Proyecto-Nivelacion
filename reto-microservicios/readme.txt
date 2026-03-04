Este proyecto consiste en un ecosistema de microservicios desarrollado con Java Spring Boot, que utiliza una arquitectura de API Gateway y seguridad basada en JSON Web Tokens (JWT). Para que el sistema funcione correctamente, sigue estos pasos:

🛠️ Requisitos Previos
Antes de empezar, asegúrate de tener instalado y abierto:

Docker Desktop (Fundamental para las bases de datos).

Java 17 o superior.

Postman (Para realizar las pruebas).

Un IDE como VS Code o IntelliJ.

Paso 1: Levantar la Infraestructura (Docker)
El proyecto utiliza bases de datos PostgreSQL independientes para cada servicio.

Abre una terminal en la carpeta raíz del proyecto.

Ejecuta el comando:

Bash
docker-compose up -d
Espera a que todos los contenedores aparezcan como "Running" en Docker Desktop.

Paso 2: Ejecutar los Microservicios
Para que el sistema funcione, los microservicios deben iniciarse en el siguiente orden (preferiblemente):

Auth-Service: Gestiona el acceso y seguridad (Puerto 8081).

Catalog-Service: Gestiona los productos.

Orders-Service: Gestiona las órdenes.

Gateway-Service: Es la puerta de entrada principal (Puerto 8080).

Paso 3: Flujo de Pruebas en Postman
Para validar la seguridad, sigue este flujo estricto. Nota: Todas las peticiones deben ir al puerto 8080 (Gateway).

A. Registro de Usuario
Método: POST

URL: http://localhost:8080/auth/register

Body (JSON):

JSON
{
  "username": "usuario_prueba",
  "password": "mi_clave_segura",
  "role": "USER"
}
B. Inicio de Sesión (Obtener Token)
Método: POST

URL: http://localhost:8080/auth/login

Body (JSON):

JSON
{
  "username": "usuario_prueba",
  "password": "mi_clave_segura"
}
Resultado: El sistema te devolverá un campo "token". Cópialo.

C. Acceso Protegido (Prueba de Fuego)
Método: GET

URL: http://localhost:8080/catalog/ping o http://localhost:8080/orders/ping

Configuración en Postman:

Ve a la pestaña Authorization.

Selecciona Type: Bearer Token.

Pega el token copiado en el cuadro de texto.

Resultado: Deberías ver el mensaje de "Conexión exitosa". Si no usas el token, el sistema te dará un error 401 Unauthorized, demostrando que la seguridad funciona.