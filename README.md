# Sistema de Pagos Unisalles

Un sistema de gestión de pagos y estudiantes desarrollado con una arquitectura Full-Stack utilizando **Spring Boot (Java)** para el backend y **Angular (TypeScript)** para el frontend.

Este sistema permite administrar estudiantes, registrar pagos con comprobantes (archivos adjuntos) y visualizar o actualizar el estado de los pagos dependiendo de su tipo (Efectivo, Cheque o Transferencia).

## 🚀 Tecnologías y Herramientas

### Backend (`sistema-pagos-backend-unisalle`)
- **Java 23**
- **Spring Boot 3.4.4**
- **Spring Data JPA** (Hibernate)
- **PostgreSQL** para persistencia de datos (base de datos en producción o local)
- **H2 Database** (configuración opcional en memoria)
- **Springdoc OpenAPI (Swagger)** para documentación de la API
- **Lombok** para reducir código repetitivo

### Frontend (`sistema-pagos-frontend-unisalle`)
- **Angular 19.x**
- **TypeScript**
- **Angular Material & CDK** (Componentes de interfaz visual)
- **SweetAlert2** (Para notificaciones e interacciones UI modales)
- **RxJS** para manejo de observables en servicios HTTP

---

## 🛠 Instalación y Configuración

### 1. Prerrequisitos
- **Java 23** o superior instalado.
- **Node.js** (versión compatible con Angular 19, sugerido v18.x o superior) y **npm**.
- **PostgreSQL** instalado y ejecutándose localmente.
- (Opcional) **Angular CLI** instalado globalmente (`npm install -g @angular/cli`).

### 2. Configuración del Backend
1. Navega a la carpeta del backend:
   ```bash
   cd sistema-pagos-backend-unisalle
   ```
2. Configura la base de datos en `src/main/resources/application.properties`. Por defecto, está configurado así:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/pagos_universidad
   spring.datasource.username=postgres
   spring.datasource.password=TuContraseña
   ```
   *Asegúrate de crear previamente la base de datos `pagos_universidad` en tu servidor PostgreSQL.*

3. Ejecuta la aplicación utilizando Maven Wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```
   La API estará corriendo en `http://localhost:8080`.

   > **Documentación de la API:** Una vez el backend esté corriendo, puedes ver la documentación Swagger en `http://localhost:8080/swagger-ui-html` o la url configurada para OpenAPI.

### 3. Configuración del Frontend
1. Navega a la carpeta del frontend:
   ```bash
   cd sistema-pagos-frontend-unisalle
   ```
2. Instala las dependencias:
   ```bash
   npm install
   ```
3. Ejecuta el servidor de desarrollo:
   ```bash
   ng serve
   ```
   La aplicación web estará disponible en `http://localhost:4200/`.

---

## 🏗 Arquitectura y Módulos

### Estructura Backend
El backend está organizado basándose en principios de diseño de capas:
- `entities`: Modelos de Dominio (`Estudiante`, `Pago`).
- `repositories`: Interfaces de Spring Data para acceso a datos.
- `services`: Lógica de negocio (Ej. `PagoService`).
- `web`: Controladores REST (Ej. `PagoController`).
- `enums`: Constantes y Tipos de estado (`PagoStatus`, `TypePago`).

### Estructura Frontend
- **Autenticación (Mock):** Sistema de inicio de sesión gestionado por `AuthService` utilizando credenciales mockeadas en el lado del cliente (roles `admin` y `user1`).
- **Pages / Views:** 
  - `home` y `dashboard`: Vistas generales post-login.
  - `estudiantes`, `estudiante-details`, `load-estudiantes`: Gestión de información y detalles de estudiantes.
  - `pagos`, `new-pago`, `load-pagos`: Visualización, creación y carga de archivos de pago.
- **Servicios API:** Integraciones con Axios / HttpClient para comunicarse con el backend (`EstudiantesService`).

---

## 🔌 Endpoints Principales del Backend

**Gestión de Estudiantes:**
- `GET /estudiantes` : Obtiene todos los estudiantes.
- `GET /estudiantes/{codigo}` : Obtiene un estudiante por su código.
- `POST /agregarEstudiante` : Crea un nuevo registro de estudiante.

**Gestión de Pagos:**
- `GET /pagos` : Lista de todos los pagos registrados.
- `POST /pagos` : *(Multipart)* Sube un comprobante de pago con información adjunta (archivo, cantidad, tipo, fecha, código del estudiante).
- `GET /estudiantes/{codigo}/pagos` : Historial de pagos de un estudiante en particular.
- `PUT /pagos/{pagoId}/updatePayment` : Actualiza el estado de un pago.
- `GET /pagoFile{pagoId}` : Descargar recibo en formato PDF.

---
## 📝 Notas Adicionales
- Al ejecutar el sistema por primera vez, `spring.jpa.hibernate.ddl-auto=create-drop` creará las tablas y las eliminará al detener la aplicación. Cambia esta propiedad a `update` para persistir los datos permanentemente en fases de desarrollo posterior o producción.
