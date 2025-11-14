# Kashu - Personal Finance Management Platform

## Summary
Kashu is a comprehensive REST API for personal finance management that helps users track their income, expenses, savings goals, and budgets. Built with Java and Spring Boot, it implements Domain-Driven Design (DDD) with tactical patterns and follows CQRS principles for clean architecture.

## Key Features

- **User Management & Authentication**: Secure JWT-based authentication with user registration and profile management
- **Transaction Tracking**: Record and categorize income and expenses with custom categories
- **Multiple Accounts**: Manage multiple financial accounts (bank accounts, cash, credit cards)
- **Savings Goals**: Set and track progress towards financial goals
- **Budget Management**: Create and monitor budgets with different time periods (monthly, yearly)
- **Reporting**: Generate financial reports and analytics

## Technology Stack

- **Java 25**
- **Spring Boot 3.5.6**
- **Spring Data JPA**
- **PostgreSQL** - Database
- **Spring Security** - JWT Authentication
- **Lombok** - Reduce boilerplate code
- **SpringDoc OpenAPI** - API Documentation
- **BCrypt** - Password hashing

## Architecture

The project follows **Domain-Driven Design (DDD)** with the following bounded contexts:

- **IAM (Identity and Access Management)**: User authentication and authorization
- **Transactions**: Financial transactions, accounts, and categories
- **Savings**: Goals and budgets management

Each bounded context implements CQRS pattern with:
- Command services for write operations
- Query services for read operations
- Repository pattern for data access
- Assembler pattern for DTO transformations

## Getting Started

### Prerequisites

- Java 25 or higher
- PostgreSQL database
- Maven (or use included Maven wrapper)

### Database Setup

1. Install PostgreSQL
2. Create a database named `tucash_db`
3. Update credentials in `src/main/resources/application.properties` if needed:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5434/tucash_db
   spring.datasource.username=postgres
   spring.datasource.password=1234
   ```

### Running the Application

```bash
# Using Maven wrapper (recommended)
./mvnw spring-boot:run

# Or on Windows
mvnw.cmd spring-boot:run
```

The API will be available at `http://localhost:8080`

### API Documentation

Once the application is running, access the interactive API documentation at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.6/maven-plugin)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.5.6/reference/htmlsingle/index.html#data.sql.jpa-and-spring-data)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/3.5.6/reference/htmlsingle/index.html#using.devtools)
* [Validation](https://docs.spring.io/spring-boot/docs/3.5.6/reference/htmlsingle/index.html#io.validation)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.5.6/reference/htmlsingle/index.html#web)
* [Spring Security](https://docs.spring.io/spring-security/reference/index.html)

## Guides

The following guides illustrate how to use some features concretely:

* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
