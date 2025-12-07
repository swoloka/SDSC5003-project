# Pet Service Platform

A modern pet service platform connecting pet owners with service providers.

## Tech Stack

### Frontend
- Vue 3.5.22
- TypeScript
- Vite 7.1.7
- Pinia 3.0.3
- Vue Router 4.5.1

### Backend
- Spring Boot 3.2.5
- Java 21
- MySQL 8.0+
- Spring Data JPA
- JWT Authentication

## Environment Requirements

- Node.js 20.19.0+ or >=22.12.0
- Java 21+
- Maven 3.6+
- MySQL 8.0+

## Quick Start

### 1. Clone the project
```bash
git clone https://github.com/swoloka/SDSC5003-project.git
cd SDSC5003-project
```

### 2. Database Setup
```sql
CREATE DATABASE pet_service_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Backend
```bash
cd JavaService
# Configure database in application.properties
mvn spring-boot:run
```
Server runs on: http://localhost:8080

### 4. Frontend
```bash
cd VueWeb
npm install
npm run dev
```
App runs on: http://localhost:5173

## Project Structure
```
SDSC5003-project/
├── JavaService/     # Spring Boot backend
├── VueWeb/          # Vue 3 frontend
├── .gitignore
└── README.md
```

## Configuration

### Backend (JavaService/src/main/resources/application.properties)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/pet_service_platform
spring.datasource.username=yourusername
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
server.port=8080
```

## Build & Deploy

```bash
# Frontend
cd VueWeb && npm run build

# Backend
cd JavaService && mvn clean package
```
