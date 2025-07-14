# Book Management System - Complete Documentation

## Table of Contents
1. [Project Overview](#project-overview)
2. [Technology Stack](#technology-stack)
3. [Project Structure](#project-structure)
4. [Dependencies Explained](#dependencies-explained)
5. [Configuration Files](#configuration-files)
6. [Entity Layer](#entity-layer)
7. [Repository Layer](#repository-layer)
8. [Service Layer](#service-layer)
9. [Controller Layer](#controller-layer)
10. [Exception Handling](#exception-handling)
11. [Data Transfer Objects (DTOs)](#data-transfer-objects-dtos)
12. [Data Initialization](#data-initialization)
13. [API Endpoints](#api-endpoints)
14. [Database Design](#database-design)
15. [Setup & Installation](#setup--installation)
16. [Testing Guide](#testing-guide)

---

## Project Overview

The Book Management System is a RESTful API built with Spring Boot that provides comprehensive functionality for managing books, authors, publishers, and users. It's designed following industry best practices with proper layered architecture, data validation, and error handling.

### Key Features:
- **Complete CRUD Operations** for all entities
- **Advanced Search & Filtering** capabilities
- **Role-based User Management** (USER, ADMIN, LIBRARIAN)
- **Data Validation** with meaningful error messages
- **Relationship Management** between entities
- **MySQL Database Integration**
- **RESTful API Design** with proper HTTP status codes

---

## Technology Stack

### Core Technologies:
- **Java 17**: Modern LTS version with enhanced features
- **Spring Boot 3.2.0**: Latest stable version with auto-configuration
- **Spring Data JPA**: Object-Relational Mapping and database operations
- **MySQL 8.0**: Relational database for data persistence
- **Maven**: Dependency management and build tool

### Why These Technologies?

**Spring Boot**: 
- **Rapid Development**: Auto-configuration reduces boilerplate code
- **Production Ready**: Built-in metrics, health checks, and externalized configuration
- **Ecosystem**: Rich ecosystem with extensive community support

**Spring Data JPA**:
- **Abstraction**: Reduces database access code by 70%
- **Query Methods**: Automatic query generation from method names
- **Relationships**: Easy handling of entity relationships

**MySQL**:
- **Reliability**: ACID compliance ensures data integrity
- **Performance**: Optimized for read-heavy operations
- **Scalability**: Proven track record in enterprise applications

---

## Project Structure

```
src/main/java/com/bookmanagement/
├── BookManagementSystemApplication.java    # Main application class
├── config/
│   └── DataInitializer.java               # Sample data setup
├── controller/                            # REST endpoints
│   ├── AuthorController.java
│   ├── BookController.java
│   ├── PublisherController.java
│   └── UserController.java
├── dto/                                   # Data Transfer Objects
│   └── BookDto.java
├── entity/                                # JPA entities
│   ├── Author.java
│   ├── Book.java
│   ├── BookStatus.java
│   ├── Publisher.java
│   ├── User.java
│   └── UserRole.java
├── exception/                             # Error handling
│   └── GlobalExceptionHandler.java
├── repository/                            # Data access layer
│   ├── AuthorRepository.java
│   ├── BookRepository.java
│   ├── PublisherRepository.java
│   └── UserRepository.java
└── service/                               # Business logic
    ├── AuthorService.java
    ├── BookService.java
    ├── PublisherService.java
    └── UserService.java
```

### Layered Architecture Benefits:
- **Separation of Concerns**: Each layer has a specific responsibility
- **Maintainability**: Changes in one layer don't affect others
- **Testability**: Easy to unit test individual layers
- **Scalability**: Can be easily extended or modified

---

## Dependencies Explained

### `pom.xml` Analysis:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```
**Purpose**: Provides JPA/Hibernate support for database operations
**What it does**: Enables automatic repository creation, entity mapping, and transaction management

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```
**Purpose**: Web layer support for REST API development
**What it does**: Includes embedded Tomcat, Spring MVC, JSON serialization

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```
**Purpose**: Bean validation support
**What it does**: Enables `@Valid`, `@NotNull`, `@Email` annotations for input validation

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```
**Purpose**: MySQL database driver
**What it does**: Enables connection and communication with MySQL database

---

## Configuration Files

### `application.yml` Breakdown:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/book_management?createDatabaseIfNotExist=true
    username: root
    password: password
```
**Purpose**: Database connection configuration
**Key Features**:
- `createDatabaseIfNotExist=true`: Automatically creates database if it doesn't exist
- `useSSL=false`: Disables SSL for local development
- `allowPublicKeyRetrieval=true`: Allows connection to MySQL 8.0+

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```
**Purpose**: JPA/Hibernate configuration
**What it does**:
- `create-drop`: Creates tables on startup, drops on shutdown (development mode)
- `show-sql: true`: Logs SQL queries for debugging

---

## Entity Layer

### Purpose:
Entities represent database tables as Java objects using JPA annotations.

### Key Annotations Explained:

#### `@Entity`
```java
@Entity
@Table(name = "books")
public class Book {
```
**Purpose**: Marks class as JPA entity
**What it does**: Tells Hibernate to create a database table for this class

#### `@Id` and `@GeneratedValue`
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```
**Purpose**: Primary key configuration
**What it does**: 
- `@Id`: Marks field as primary key
- `IDENTITY`: Uses database auto-increment for ID generation

#### Validation Annotations
```java
@NotBlank(message = "Book title is required")
@Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
private String title;
```
**Purpose**: Input validation
**What it does**:
- `@NotBlank`: Ensures field is not null or empty
- `@Size`: Validates string length
- Custom error messages for better user experience

#### Relationship Annotations
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "author_id", nullable = false)
@JsonBackReference
private Author author;
```
**Purpose**: Entity relationships
**What it does**:
- `@ManyToOne`: Many books can have one author
- `FetchType.LAZY`: Loads author only when accessed (performance optimization)
- `@JsonBackReference`: Prevents infinite recursion in JSON serialization

#### Lifecycle Hooks
```java
@PrePersist
protected void onCreate() {
    createdAt = LocalDateTime.now();
}
```
**Purpose**: Automatic timestamp management
**What it does**: Automatically sets creation time before saving to database

### Entity Relationships:

```
Author (1) ──→ (Many) Book ←── (Many) (1) Publisher
User (Independent entity)
```

**Why These Relationships?**
- **Author-Book**: One author can write multiple books
- **Publisher-Book**: One publisher can publish multiple books
- **Book**: Central entity linking authors and publishers

---

## Repository Layer

### Purpose:
Data access layer that provides database operations without writing SQL.

### Key Features:

#### Automatic Query Generation
```java
List<Author> findByNameContainingIgnoreCase(String name);
```
**What it does**: Spring Data JPA automatically generates SQL:
```sql
SELECT * FROM authors WHERE LOWER(name) LIKE LOWER('%searchTerm%')
```

#### Custom Queries
```java
@Query("SELECT a FROM Author a JOIN a.books b GROUP BY a.id ORDER BY COUNT(b) DESC")
List<Author> findAuthorsOrderByBookCount();
```
**Purpose**: Complex queries that can't be generated automatically
**What it does**: JPQL query to find authors ordered by number of books

#### Why Repository Pattern?
- **Abstraction**: Hides database implementation details
- **Testability**: Easy to mock for unit testing
- **Consistency**: Uniform interface for data access
- **Performance**: Built-in caching and optimization

---

## Service Layer

### Purpose:
Contains business logic and coordinates between controllers and repositories.

### Key Responsibilities:

#### Business Logic
```java
public Author saveAuthor(Author author) {
    return authorRepository.save(author);
}
```
**What it does**: Handles data validation, business rules, and coordination

#### Error Handling
```java
public Author updateAuthor(Long id, Author authorDetails) {
    Author author = authorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));
    // Update logic
}
```
**Purpose**: Centralized error handling with meaningful messages

#### Data Transformation
```java
private BookDto convertToDto(Book book) {
    // Convert entity to DTO
}
```
**Why**: Separates internal data structure from API response format

### Service Layer Benefits:
- **Reusability**: Business logic can be reused across controllers
- **Transaction Management**: `@Transactional` support
- **Security**: Centralized authorization logic
- **Testing**: Easy to unit test business logic

---

## Controller Layer

### Purpose:
REST endpoints that handle HTTP requests and responses.

### Key Annotations:

#### `@RestController`
```java
@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {
```
**What it does**:
- `@RestController`: Combines `@Controller` and `@ResponseBody`
- `@RequestMapping`: Base URL for all endpoints in this controller
- `@CrossOrigin`: Enables CORS for frontend integration

#### HTTP Method Mappings
```java
@GetMapping("/{id}")
public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
```
**Purpose**: Maps HTTP GET requests to method
**What it does**: `@PathVariable` extracts ID from URL path

#### Request/Response Handling
```java
@PostMapping
public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookDto bookDto) {
```
**What it does**:
- `@Valid`: Triggers validation on request body
- `@RequestBody`: Converts JSON to Java object
- `ResponseEntity`: Provides control over HTTP status codes

### HTTP Status Codes Used:
- **200 OK**: Successful GET, PUT operations
- **201 CREATED**: Successful POST operations
- **204 NO CONTENT**: Successful DELETE operations
- **400 BAD REQUEST**: Validation errors
- **404 NOT FOUND**: Resource not found

---

## Exception Handling

### Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
```

### Purpose:
Centralized error handling across all controllers.

### Key Features:

#### Validation Error Handling
```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
```
**What it does**: Catches validation errors and returns structured error response

#### Runtime Exception Handling
```java
@ExceptionHandler(RuntimeException.class)
public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
```
**Purpose**: Handles business logic errors (like "Author not found")

### Error Response Format:
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "errors": {
    "title": "Book title is required",
    "isbn": "ISBN must be between 10 and 17 characters"
  }
}
```

### Benefits:
- **Consistency**: Uniform error responses across API
- **User Experience**: Clear, actionable error messages
- **Debugging**: Timestamps and detailed error information

---

## Data Transfer Objects (DTOs)

### Purpose:
DTOs separate internal entity structure from API contract.

### `BookDto` Example:
```java
public class BookDto {
    private Long id;
    private String title;
    private Long authorId;
    private String authorName;  // Computed field
}
```

### Why Use DTOs?

#### 1. **API Stability**
- Internal entity changes don't break API clients
- Can evolve entities without affecting API contract

#### 2. **Security**
- Hide sensitive internal fields
- Control what data is exposed

#### 3. **Performance**
- Include only necessary data
- Flatten complex relationships

#### 4. **Flexibility**
- Combine data from multiple entities
- Add computed fields (like `authorName`)

### DTO Conversion:
```java
private BookDto convertToDto(Book book) {
    BookDto dto = new BookDto();
    dto.setId(book.getId());
    dto.setTitle(book.getTitle());
    dto.setAuthorId(book.getAuthor().getId());
    dto.setAuthorName(book.getAuthor().getName());  // Flattened relationship
    return dto;
}
```

---

## Data Initialization

### `DataInitializer` Class:

```java
@Component
public class DataInitializer implements CommandLineRunner {
```

### Purpose:
Automatically populates database with sample data on application startup.

### Key Features:

#### Sample Data Creation
```java
Author author1 = new Author("J.K. Rowling", "jk.rowling@example.com", 
        LocalDate.of(1965, 7, 31), "British author, best known for Harry Potter series");
```

#### Relationship Setup
```java
Book book1 = new Book("Harry Potter and the Philosopher's Stone", "9780747532699", 
        "The first book in the Harry Potter series", LocalDate.of(1997, 6, 26), 
        223, new BigDecimal("19.99"), author1, publisher1);
```

### Benefits:
- **Immediate Testing**: API ready to test without manual data entry
- **Demonstration**: Shows proper entity relationships
- **Development**: Consistent test data across environments

---

## API Endpoints

### RESTful Design Principles:

#### Resource-Based URLs
```
GET    /api/books           # Get all books
POST   /api/books           # Create new book
GET    /api/books/1         # Get specific book
PUT    /api/books/1         # Update specific book
DELETE /api/books/1         # Delete specific book
```

#### Query Parameters for Filtering
```
GET /api/books/search?title=Harry
GET /api/books/author/1
GET /api/books/status/AVAILABLE
```

### Complete Endpoint List:

#### Books API
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/books` | Get all books |
| POST | `/api/books` | Create new book |
| GET | `/api/books/{id}` | Get book by ID |
| PUT | `/api/books/{id}` | Update book |
| DELETE | `/api/books/{id}` | Delete book |
| GET | `/api/books/search?title={title}` | Search books by title |
| GET | `/api/books/isbn/{isbn}` | Get book by ISBN |
| GET | `/api/books/author/{authorId}` | Get books by author |
| GET | `/api/books/publisher/{publisherId}` | Get books by publisher |
| GET | `/api/books/status/{status}` | Get books by status |

#### Authors API
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/authors` | Get all authors |
| POST | `/api/authors` | Create new author |
| GET | `/api/authors/{id}` | Get author by ID |
| PUT | `/api/authors/{id}` | Update author |
| DELETE | `/api/authors/{id}` | Delete author |
| GET | `/api/authors/search?name={name}` | Search authors by name |
| GET | `/api/authors/email/{email}` | Get author by email |

#### Publishers API
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/publishers` | Get all publishers |
| POST | `/api/publishers` | Create new publisher |
| GET | `/api/publishers/{id}` | Get publisher by ID |
| PUT | `/api/publishers/{id}` | Update publisher |
| DELETE | `/api/publishers/{id}` | Delete publisher |
| GET | `/api/publishers/search?name={name}` | Search publishers by name |
| GET | `/api/publishers/email/{email}` | Get publisher by email |

#### Users API
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | Get all users |
| POST | `/api/users` | Create new user |
| GET | `/api/users/{id}` | Get user by ID |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |
| GET | `/api/users/username/{username}` | Get user by username |
| GET | `/api/users/email/{email}` | Get user by email |
| GET | `/api/users/role/{role}` | Get users by role |

---

## Database Design

### Entity Relationship Diagram:

```
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│   AUTHORS   │         │    BOOKS    │         │ PUBLISHERS  │
├─────────────┤         ├─────────────┤         ├─────────────┤
│ id (PK)     │──┐   ┌──│ id (PK)     │──┐   ┌──│ id (PK)     │
│ name        │  │   │  │ title       │  │   │  │ name        │
│ email       │  │   │  │ isbn        │  │   │  │ email       │
│ birth_date  │  │   │  │ description │  │   │  │ address     │
│ biography   │  │   │  │ pub_date    │  │   │  │ phone       │
│ created_at  │  │   │  │ page_count  │  │   │  │ created_at  │
│ updated_at  │  │   │  │ price       │  │   │  │ updated_at  │
└─────────────┘  │   │  │ status      │  │   │  └─────────────┘
                 │   │  │ cover_url   │  │   │
                 │   │  │ author_id(FK)│──┘   │
                 │   │  │publisher_id(FK)│────┘
                 │   │  │ created_at  │
                 │   │  │ updated_at  │
                 │   │  └─────────────┘
                 │   │
                 └───┘ One-to-Many Relationships

┌─────────────┐
│    USERS    │
├─────────────┤
│ id (PK)     │
│ username    │
│ first_name  │
│ last_name   │
│ email       │
│ password    │
│ role        │
│ created_at  │
│ updated_at  │
└─────────────┘
```

### Table Specifications:

#### Books Table
```sql
CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    isbn VARCHAR(17) UNIQUE NOT NULL,
    description TEXT,
    publication_date DATE,
    page_count INT,
    price DECIMAL(10,2),
    status ENUM('AVAILABLE', 'BORROWED', 'RESERVED', 'DAMAGED', 'OUT_OF_PRINT'),
    cover_image_url VARCHAR(500),
    author_id BIGINT NOT NULL,
    publisher_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES authors(id),
    FOREIGN KEY (publisher_id) REFERENCES publishers(id)
);
```

### Indexes:
- **Primary Keys**: Automatic clustering for fast lookups
- **Foreign Keys**: Indexed for join performance
- **Unique Constraints**: ISBN, email fields for data integrity

---

## Setup & Installation

### Prerequisites:
- **Java 17+**: `java -version`
- **Maven 3.6+**: `mvn -version`
- **MySQL 8.0+**: Running on localhost:3306

### Installation Steps:

#### 1. Database Setup
```sql
-- Create MySQL user (optional)
CREATE USER 'bookmanager'@'localhost' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON book_management.* TO 'bookmanager'@'localhost';
FLUSH PRIVILEGES;
```

#### 2. Clone and Build
```bash
# Clone the project
git clone <repository-url>
cd book-management-system

# Build the project
mvn clean install
```

#### 3. Configuration
Update `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/book_management?createDatabaseIfNotExist=true
    username: your_username
    password: your_password
```

#### 4. Run Application
```bash
# Run with Maven
mvn spring-boot:run

# Or run JAR file
java -jar target/book-management-system-1.0.0.jar
```

#### 5. Verify Installation
```bash
# Health check
curl http://localhost:8080/api/books

# Should return sample books data
```

### Environment Profiles:

#### Development (default)
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop  # Recreates tables on restart
    show-sql: true          # Shows SQL queries
```

#### Production
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate     # Only validates schema
    show-sql: false         # No SQL logging
```

---

## Testing Guide

### Manual Testing with curl:

#### Create Author
```bash
curl -X POST http://localhost:8080/api/authors \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Stephen King",
    "email": "stephen.king@example.com", 
    "birthDate": "1947-09-21",
    "biography": "American author of horror, supernatural fiction"
  }'
```

#### Create Publisher
```bash
curl -X POST http://localhost:8080/api/publishers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Doubleday",
    "email": "contact@doubleday.com",
    "address": "New York, NY",
    "phoneNumber": "+1-212-555-0123"
  }'
```

#### Create Book
```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "The Shining",
    "isbn": "9780385121675",
    "description": "A psychological horror novel",
    "publicationDate": "1977-01-28",
    "pageCount": 447,
    "price": 15.99,
    "authorId": 3,
    "publisherId": 3,
    "status": "AVAILABLE"
  }'
```

#### Search Books
```bash
# Search by title
curl "http://localhost:8080/api/books/search?title=Harry"

# Get books by author
curl http://localhost:8080/api/books/author/1

# Get available books
curl http://localhost:8080/api/books/status/AVAILABLE
```

### Testing Validation:

#### Invalid Book (Missing Required Fields)
```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Book without title or ISBN"
  }'

# Returns validation errors
```

#### Duplicate ISBN
```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Another Book",
    "isbn": "9780747532699",  # Duplicate ISBN
    "authorId": 1,
    "publisherId": 1
  }'

# Returns constraint violation error
```

### Integration Testing:

#### Test Complete Workflow
```bash
# 1. Create author
AUTHOR_ID=$(curl -s -X POST http://localhost:8080/api/authors \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Author","email":"test@example.com"}' \
  | jq -r '.id')

# 2. Create publisher  
PUBLISHER_ID=$(curl -s -X POST http://localhost:8080/api/publishers \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Publisher","email":"pub@example.com"}' \
  | jq -r '.id')

# 3. Create book with author and publisher
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d "{
    \"title\":\"Test Book\",
    \"isbn\":\"9781234567890\",
    \"authorId\":$AUTHOR_ID,
    \"publisherId\":$PUBLISHER_ID
  }"
```

### Performance Testing:

#### Bulk Data Creation
```bash
# Create 100 books
for i in {1..100}; do
  curl -X POST http://localhost:8080/api/books \
    -H "Content-Type: application/json" \
    -d "{
      \"title\":\"Book $i\",
      \"isbn\":\"978123456789$((i%10))\",
      \"authorId\":1,
      \"publisherId\":1
    }"
done
```

---

## Best Practices Implemented

### 1. **Code Organization**
- **Package by Feature**: Related classes grouped together
- **Separation of Concerns**: Each layer has single responsibility
- **Naming Conventions**: Clear, descriptive names

### 2. **Database Design**
- **Normalization**: Eliminates data redundancy
- **Constraints**: Ensures data integrity
- **Indexes**: Optimizes query performance

### 3. **API Design**
- **RESTful Principles**: Consistent URL patterns
- **HTTP Status Codes**: Proper status code usage
- **Error Handling**: Meaningful error messages

### 4. **Security Considerations**
- **Input Validation**: Prevents malicious data
- **SQL Injection Prevention**: JPA/Hibernate protection
- **Data Sanitization**: Automatic through validation

### 5. **Performance Optimization**
- **Lazy Loading**: Loads related data only when needed
- **DTO Pattern**: Transfers only necessary data
- **Connection Pooling**: Efficient database connections

### 6. **Maintainability**
- **Documentation**: Comprehensive code comments
- **Exception Handling**: Centralized error management
- **Configuration**: Externalized configuration

---

## Common Issues & Solutions

### Issue 1: Database Connection Failed
**Problem**: `Communications link failure`
**Solution**: 
- Verify MySQL is running: `sudo systemctl status mysql`
- Check connection URL and credentials
- Ensure database exists or use `createDatabaseIfNotExist=true`

### Issue 2: Validation Errors
**Problem**: `MethodArgumentNotValidException`
**Solution**:
- Check required fields are provided
- Verify data types match entity definitions
- Review validation annotations

### Issue 3: Foreign Key Constraint Violation
**Problem**: `Cannot delete or update a parent row`
**Solution**:
- Delete related records first (books before authors)
- Use cascade options in entity relationships
- Check referential integrity

### Issue 4: JSON Serialization Issues
**Problem**: Infinite recursion in relationships
**Solution**:
- Use `@JsonBackReference` and `@JsonManagedReference`
- Implement DTOs to control serialization
- Use `@JsonIgnore` for circular references

---

## Future Enhancements

### 1. **Security**
- JWT authentication
- Role-based access control
- Password encryption

### 2. **Features**
- Book borrowing system
- Advanced search with filters
- Book ratings and reviews
- Email notifications

### 3. **Performance**
- Redis caching
- Database indexing optimization
- Pagination for large datasets
- Connection pooling tuning

### 4. **Monitoring**
- Application metrics
- Health checks
- Logging improvements
- Performance monitoring

### 5. **Testing**
- Unit tests with JUnit 5
- Integration tests
- Test containers for database testing
- API documentation with Swagger

---

## Conclusion

This Book Management System demonstrates a complete, production-ready Spring Boot application with:

- **Clean Architecture**: Well-organized, maintainable code structure
- **Best Practices**: Industry-standard patterns and conventions  
- **Comprehensive Features**: Full CRUD operations with advanced search
- **Proper Error Handling**: User-friendly error messages
- **Database Design**: Normalized schema with proper relationships
- **API Design**: RESTful endpoints following HTTP standards

The system is designed to be easily extensible and can serve as a foundation for more complex library management systems or similar domain applications.