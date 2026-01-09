# 📚 Sistema Distribuido de Biblioteca Digital

## 📋 Descripción General
Ecosistema de microservicios para gestión bibliotecaria implementado con Spring Boot y Spring Cloud. Sistema distribuido con servicios independientes, comunicación REST y API Gateway centralizada.

## 🚀 Guía de Inicio Rápido

### 📋 Prerrequisitos
- Java 17+, PostgreSQL 14+, Maven 3.8+

### ⚡ Pasos de Ejecución (ORDEN CRÍTICO)
1. **Levantar Eureka Server** (8761) - Service Discovery
2. **Levantar ms-usuarios** (8083) - Gestión de usuarios
3. **Levantar ms-libros** (8081) - Catálogo de libros  
4. **Levantar ms-prestamos** (8082) - Sistema de préstamos
5. **Levantar API Gateway** (8080) - Punto de entrada

**⚠️ IMPORTANTE**: El orden es obligatorio para el registro correcto en Eureka.

## 🌐 Acceso y Rutas

### 🔗 URLs Principales
| Componente | Puerto | Propósito              |
|------------|--------|------------------------|
| **API Gateway** | 8080 | Punto de entrada único |
| **Eureka Dashboard** | 8761 | Monitoreo de servicios |
| **Usuarios** | 8083 | Gestión de socios      |
| **Libros** | 8081 | Catálogo de libros     |
| **Préstamos** | 8082 | Sistema de préstamos   |

### 📍 Rutas a través del Gateway
Todas las solicitudes deben realizarse a través del Gateway:

| Microservicio | Ruta Interna | Ruta Gateway |
|---------------|--------------|--------------|
| **Libros** | `/api/libros/**` | `/biblioteca/libros/**` |
| **Usuarios** | `/api/usuarios/**` | `/biblioteca/usuarios/**` |
| **Préstamos** | `/api/prestamos/**` | `/biblioteca/prestamos/**` |

## 📬 Colección Postman
El proyecto incluye una colección completa de Postman con todos los endpoints preconfigurados:

**📁 Ubicación**: `/docs/postman/Sistema Distribuido de Biblioteca Digital.postman_collection.json`

### 🎯 Características de la colección:
- ✅ Todos los endpoints de los tres microservicios
- ✅ Ejemplos de requests con datos de prueba
- ✅ Flujos completos de préstamo y devolución

### 🚀 Cómo usar:
1. Importar la colección en Postman
2. Ejecutar los requests en orden para probar flujos completos

## 📖 Ejemplos de Uso (Postman)
```bash
# 1. Crear usuario
POST http://localhost:8080/biblioteca/usuarios

# 2. Crear libro  
POST http://localhost:8080/biblioteca/libros

# 3. Registrar préstamo
POST http://localhost:8080/biblioteca/prestamos

# 4. Devolver libro
POST http://localhost:8080/biblioteca/prestamos/{id}/devolver
```

## 📊 Bases de Datos Requeridas
```sql
CREATE DATABASE ms_usuarios_db;
CREATE DATABASE ms_libros_db;
CREATE DATABASE ms_prestamos_db;
```

## 🛠️ Configuración por Servicio
Cada microservicio requiere archivo `env.properties`:
```properties
POSTGRES_USERNAME=postgres
POSTGRES_PASSWORD=tu_contraseñaPostgresSQL
```

## 🔄 Flujos de Negocio

### 📚 Préstamo de Libro
1. Cliente solicita préstamo vía Gateway
2. Gateway enruta a ms-prestamos
3. ms-prestamos valida usuario en ms-usuarios
4. ms-prestamos verifica stock en ms-libros
5. Se actualiza stock y se registra préstamo
6. Respuesta retorna al cliente

### 📋 Validaciones Cruzadas
- ✅ Usuario debe existir y estar activo
- ✅ Libro debe existir y tener stock disponible
- ✅ Transaccionalidad distribuida
- ✅ Rollback automático en errores

## ✅ Características Principales

### 🏗️ Arquitectura
- Microservicios independientes
- Service Discovery con Eureka
- API Gateway centralizada
- Bases de datos aisladas por servicio

### 🔧 Tecnología
- Spring Boot 3.x + Spring Cloud
- PostgreSQL + Spring Data JPA
- OpenFeign para comunicación REST
- Swagger/OpenAPI para documentación
- JUnit 5 + Mockito + MockServer para las pruebas unitarias

## 📁 Estructura del Proyecto
```
/
├── eureka-server/     # Service Discovery
├── api-gateway/       # Punto de entrada único
├── usuarios/          # Ver README específico del microservicio de Usuarios
├── libros/            # Ver README específico del microservicio de Libros
├── prestamos/         # Ver README específico del microservicio de Prestamos
└── README.md          # Este archivo
```

**🚀 Proyecto orientado a arquitecturas escalables y buenas prácticas de desarrollo backend.**
