# 📚 Microservicio de Libros

## 📋 Descripción
Este microservicio gestiona el catálogo completo de libros de la biblioteca. Se encarga de mantener la unicidad del ISBN (identificador único internacional de libros) y controla el inventario de ejemplares disponibles, un componente fundamental para el sistema de préstamos distribuido.

## 🏗️ Arquitectura
- **Spring Boot 3.x** con arquitectura en capas
- **PostgreSQL** como base de datos
- **Spring Cloud Netflix Eureka** para service discovery
- **Spring Data JPA** para persistencia
- **Swagger/OpenAPI** para documentación
- **JUnit 5 + Mockito** para testing

## 🚀 Cómo ejecutar

### 📦 Prerrequisitos obligatorios:
1. ✅ **Java 17+** instalado
2. 🐘 **PostgreSQL 14+** corriendo
3. 🔗 **Eureka Server** corriendo en `http://localhost:8761`
4. 📦 **Maven 3.8+** instalado

### 📝 Pasos:
1. Crear base de datos en PostgreSQL:
```sql
CREATE DATABASE ms_libros_db;
```

2. Crear archivo `src/main/resources/env.properties`:
```properties
POSTGRES_USERNAME=postgres
POSTGRES_PASSWORD=tu_contraseñaDePostgresSQL
```

3. Ejecutar el microservicio:
```bash
mvn spring-boot:run
```

## 📊 Datos de prueba
```json
{"titulo": "The Clean Coder: A Code of Conduct for Professional Programmers", "autor": "Robert C. Martin", "isbn": "978-0137081073", "ejemplaresDisponibles": 3}
{"titulo": "Refactoring: Improving the Design of Existing Code", "autor": "Martin Fowler", "isbn": "978-0134757599", "ejemplaresDisponibles": 1}
{"titulo": "Design Patterns: Elements of Reusable Object-Oriented Software", "autor": "Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides", "isbn": "978-0201633610", "ejemplaresDisponibles": 1}
```

## 📸 Captura del análisis SonarLint
![Análisis SonarLint](docs/images/SonarQubeAnalysisBooks.png)

## 🛠️ Características
- ✅ CRUD completo de libros
- ✅ Validación de ISBN único
- ✅ Control de stock de ejemplares
- ✅ Manejo centralizado de excepciones
- ✅ Tests unitarios e integración
- ✅ Documentación Swagger/OpenAPI

## 🧪 Testing
```bash
mvn test  # Ejecutar todos los tests
```

## 🔗 Endpoints Principales
| Método | Ruta | Descripción |
|-------|------|-------------|
| POST  | `/api/libros` | Crear nuevo libro |
| GET  | `/api/libros` | Listar todos los libros |
| GET  | `/api/libros/{id}` | Buscar libro por ID |
| PUT  | `/api/libros/{id}` | Actualizar libro existente |
| DELETE  | `/api/libros/{id}` | Eliminar libro del sistema |

## 🌐 Documentación con Swagger / OpenAPI

Para ver la documentación interactiva de la API, accede a la siguiente URL cuando la aplicación esté en ejecución:

**Swagger UI:** http://localhost:8081/swagger-ui/index.html

**📌 Nota:** Este microservicio corre en el puerto **8081** por defecto.
