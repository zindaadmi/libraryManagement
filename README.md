# 📚 Library Management System

A comprehensive Spring Boot REST API for managing library operations including books, borrowers, borrowing workflow, and analytics.

## 🚀 Features

### Core Functionality
- **Book Management**: Add, update, delete, and search books with pagination
- **Borrower Management**: Register and manage library members
- **Borrowing Workflow**: Complete borrow/return cycle with validation
- **Analytics**: Track popular books, borrower activity, and overdue records
- **Automated Scheduling**: Daily overdue record flagging

### Technical Features
- **RESTful API Design** with proper HTTP status codes
- **Global Exception Handling** with custom error responses
- **Data Validation** using Bean Validation annotations
- **Pagination & Sorting** for large datasets
- **Caching** for frequently accessed data
- **Swagger Documentation** for API testing
- **Scheduled Tasks** for automated operations
- **Comprehensive Logging** with SLF4J

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.1.0
- **Database**: H2 (In-Memory)
- **ORM**: Spring Data JPA with Hibernate
- **Validation**: Bean Validation (JSR-303)
- **Documentation**: SpringDoc OpenAPI 3
- **Build Tool**: Gradle
- **Java Version**: 17

## 📋 Prerequisites

- Java 17 or higher
- Gradle 7.0 or higher
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd libraryManagement
```

### 2. Run the Application
```bash
./gradlew bootRun
```

### 3. Access the Application
- **API Base URL**: `http://localhost:8081/api`
- **Swagger UI**: `http://localhost:8081/swagger-ui.html`
- **H2 Console**: `http://localhost:8081/h2-console`
- **API Docs**: `http://localhost:8081/api-docs`

## 📚 API Endpoints

### Books
- `GET /api/books` - List all books with pagination and filters
- `POST /api/books` - Add a new book
- `GET /api/books/{id}` - Get book by ID
- `PUT /api/books/{id}` - Update book
- `DELETE /api/books/{id}` - Delete book
- `GET /api/books/available` - Get available books
- `GET /api/books/category/{category}` - Get books by category

### Borrowers
- `GET /api/borrowers` - List all borrowers
- `POST /api/borrowers` - Register new borrower
- `GET /api/borrowers/{id}` - Get borrower by ID
- `PUT /api/borrowers/{id}` - Update borrower
- `DELETE /api/borrowers/{id}` - Delete borrower
- `GET /api/borrowers/{id}/records` - Get borrower's borrow history
- `GET /api/borrowers/overdue` - Get borrowers with overdue books

### Borrowing
- `POST /api/borrow` - Borrow a book
- `POST /api/return` - Return a book
- `GET /api/records/active` - Get active borrow records

### Analytics
- `GET /api/analytics/top-borrowed-books` - Get most borrowed books
- `GET /api/analytics/borrower-activity` - Get borrower activity summary
- `GET /api/books/similar/{id}` - Get similar books
- `GET /api/books/availability-summary` - Get availability summary

## 🗄️ Database Schema

### Entities
- **Book**: id, title, author, category, totalCopies, availableCopies, isAvailable
- **Borrower**: id, name, email, membershipType, maxBorrowLimit
- **BorrowRecord**: id, bookId, borrowerId, borrowDate, dueDate, returnDate, fineAmount
- **FinePolicy**: id, category, finePerDay

### Relationships
- One-to-Many: Borrower → BorrowRecord
- One-to-Many: Book → BorrowRecord
- Many-to-One: BorrowRecord → Book
- Many-to-One: BorrowRecord → Borrower

## 🔧 Configuration

### Application Properties
```properties
# Database Configuration
spring.datasource.url=jdbc:h2:mem:librarydb
spring.h2.console.enabled=true

# JPA Configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# Server Configuration
server.port=8081

# Cache Configuration
spring.cache.type=simple

# Swagger Configuration
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

## 🏗️ Architecture

### Layered Architecture
```
Controller Layer (REST Controllers)
    ↓
Service Layer (Business Logic)
    ↓
Repository Layer (Data Access)
    ↓
Database Layer (H2)
```

### Key Components
- **Controllers**: Handle HTTP requests and responses
- **Services**: Implement business logic and validation
- **Repositories**: Data access using Spring Data JPA
- **DTOs**: Data transfer objects for API communication
- **Entities**: JPA entities for database mapping
- **Exception Handler**: Global exception handling
- **Scheduler**: Automated tasks

## 🔍 Key Features Explained

### 1. Global Exception Handling
```java
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {
    // Handles all exceptions globally
}
```

### 2. Pagination & Sorting
```java
@GetMapping
public ResponseEntity<Page<BookDTO>> getAllBooks(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(defaultValue = "title") String sortBy
) {
    // Implementation with Pageable
}
```

### 3. Caching
```java
@Cacheable("books")
public List<BookDTO> getAvailableBooks() {
    // Cached method
}
```

### 4. Scheduled Tasks
```java
@Scheduled(cron = "0 0 0 * * ?")
public void flagOverdueRecords() {
    // Runs daily at midnight
}
```

## 🧪 Testing

### Manual Testing
1. Start the application
2. Access Swagger UI at `http://localhost:8081/swagger-ui.html`
3. Test all endpoints using the interactive interface

### Sample Data
The application automatically initializes with sample data:
- 10 books across different categories
- 5 borrowers with different membership types
- Sample borrow records

## 📊 Sample API Calls

### Add a Book
```bash
curl -X POST http://localhost:8081/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Spring Boot in Action",
    "author": "Craig Walls",
    "category": "Tech",
    "totalCopies": 5
  }'
```

### Borrow a Book
```bash
curl -X POST http://localhost:8081/api/borrow \
  -H "Content-Type: application/json" \
  -d '{
    "bookId": "book-uuid",
    "borrowerId": "borrower-uuid"
  }'
```

## 🚀 Deployment

### Local Development
```bash
./gradlew bootRun
```

### Production Build
```bash
./gradlew build
java -jar build/libs/libraryManagement-0.0.1-SNAPSHOT.jar
```

## 📝 License

This project is licensed under the MIT License.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📞 Support

For questions or support, please open an issue in the repository.

---

**Built with ❤️ using Spring Boot**
