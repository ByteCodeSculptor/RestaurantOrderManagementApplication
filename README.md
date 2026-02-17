# 🍽 Restaurant Order Management System (Backend API)

A **production-ready backend API** built with **Spring Boot 3**, designed to simulate a real-world restaurant management system.  

This API provides:  

- **Role-based authentication** with Admin and Staff users.  
- **Strict order workflow enforcement** to manage the lifecycle of orders efficiently.  
- **Structured error handling** for clear and consistent API responses.  
- **Clean layered architecture** for maintainable and scalable code.  

This project is ideal for learning **best practices in backend development** and building a robust restaurant management system backend.

## 🚀 Tech Stack
- **Java 17+**
- **Spring Boot 3**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA**
- **MySQL**
- **Maven**
- **Lombok**

## 🏗 Architecture

The project follows a **clean layered architecture** for maintainability and scalability:
```text
com.restaurants.demo
├── config/        → Security & configuration
├── controller/    → REST endpoints
├── dto/           → Request & Response models
├── entity/        → JPA entities
├── exception/     → Global exception handling
├── repository/    → Data access layer
├── security/      → JWT & security logic
├── service/       → Business logic
└── util/          → Enums & helpers
```
## 🗄 Database Tables

- users
- menu_items
- orders
- order_items

---

## ▶ How To Run

Follow the steps below to set up and run the application locally.

### 1. Clone the Repository
`git clone <your-repository-url>`
`cd <project-folder>`

### 2. Create MySQL Database
`CREATE DATABASE restaurant_db;`

### 3. Update application.properties
```text
spring.datasource.url=jdbc:mysql://localhost:3306/restaurant_db
spring.datasource.username=root
spring.datasource.password=your_password
```

### 4. Build the Project
`mvn clean install`

### 5. Run the Application
`mvn spring-boot:run`

### ✅ Application URL
`http://localhost:8080`

## 📌 Complete API Endpoints

Base URL:
http://localhost:8080

All protected routes require:
Authorization: Bearer <JWT_TOKEN>

---

## 🔐 Authentication

### Login
POST /api/auth/login

---

## 🍽 Menu Management

### Create Menu Item (ADMIN)
POST /api/menu-items

### Update Menu Item (ADMIN)
PUT /api/menu-items/{id}

### Delete Menu Item (ADMIN)
DELETE /api/menu-items/{id}

### Toggle Menu Availability (ADMIN)
PATCH /api/menu-items/{id}/availability

### Get Menu Items (ADMIN, STAFF)
GET /api/menu-items

### Get Menu Items with Pagination
GET /api/menu-items?page=0&size=10

### Get Menu Items with Availability Filter
GET /api/menu-items?available=true&page=0&size=10

---

## 🧾 Order Management (STAFF)

### Create Order
POST /api/orders

### Get All Orders
GET /api/orders

### Get Orders with Status Filter
GET /api/orders?status=PREPARING&page=0&size=10

### Get Order By ID
GET /api/orders/{id}

### Update Order Status
PATCH /api/orders/{id}/status

### Update Order
PUT /api/orders/{id}

### Delete Order
DELETE /api/orders/{id}

---

## 📊 Reporting (ADMIN)

### Daily Summary Report
GET /api/orders/reports/daily

---

## 🔐 Authorization Rules

- /api/auth/** → Public
- /api/menu-items/** → ADMIN (write), STAFF (read)
- /api/orders/** → STAFF, ADMIN
