# Student Management System

A role-based student management system built with Spring Boot, PostgreSQL, and JWT authentication.

## Features

- **Role-Based Access Control**: Admin, Teacher, and Student roles
- **JWT Authentication**: Secure token-based authentication
- **CRUD Operations**: Complete management of students and teachers
- **Teacher-Student Assignment**: One teacher can have multiple students
- **Dual Database Support**: Run locally or with Docker

## Technologies

- Spring Boot 4.0.3
- Spring Security with JWT
- PostgreSQL
- Docker & Docker Compose
- JPA/Hibernate

## Roles & Permissions

| Role | Permissions |
|------|-------------|
| **ADMIN** | Full CRUD for students, teachers, and assignments |
| **TEACHER** | Read-only access to all data |
| **STUDENT** | Read-only access to all data |

## Database Schema

- `users` - Authentication data
- `students` - Student information (linked to users and teachers)
- `teachers` - Teacher information (linked to users)

## Setup Instructions

### Prerequisites

- Java 17 or higher
- Maven
- PostgreSQL (for local development)
- Docker Desktop (for Docker deployment)

### Running Locally

1. **Create local PostgreSQL database:**
   ```sql
   CREATE DATABASE crud_springboot_database;
   ```

2. **Update credentials in `application.yaml`** (local profile section)

3. **Run the application:**
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
   ```

### Running with Docker

1. **Start all services:**
   ```bash
   docker-compose up --build
   ```

2. **Application will be available at:** `http://localhost:8081`

3. **Stop services:**
   ```bash
   docker-compose down
   ```

## API Endpoints

### Authentication (Public)

```
POST /api/auth/register - Register new user
POST /api/auth/login    - Login and get JWT token
```

### Admin Only

```
POST   /api/admin/students                      - Create student
PUT    /api/admin/students/{id}                 - Update student
DELETE /api/admin/students/{id}                 - Delete student

POST   /api/admin/teachers                      - Create teacher
PUT    /api/admin/teachers/{id}                 - Update teacher
DELETE /api/admin/teachers/{id}                 - Delete teacher

PUT    /api/admin/students/{studentId}/assign/{teacherId}  - Assign student to teacher
PUT    /api/admin/students/{studentId}/unassign            - Unassign student
```

### All Authenticated Users

```
GET /api/students                  - Get all students
GET /api/students/{id}             - Get student by ID
GET /api/students/unassigned       - Get unassigned students

GET /api/teachers                  - Get all teachers
GET /api/teachers/{id}             - Get teacher by ID
GET /api/teachers/{id}/students    - Get students assigned to teacher
```

## Usage Examples

### 1. Register Admin User

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123",
    "email": "admin@example.com",
    "role": "ADMIN"
  }'
```

### 2. Login

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "username": "admin",
  "email": "admin@example.com",
  "role": "ADMIN"
}
```

### 3. Create Teacher (Admin only)

```bash
curl -X POST http://localhost:8081/api/admin/teachers \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Mr. Smith",
    "email": "smith@example.com",
    "phone": "1234567890",
    "subject": "Mathematics",
    "username": "smith",
    "password": "smith123"
  }'
```

### 4. Create Student (Admin only)

```bash
curl -X POST http://localhost:8081/api/admin/students \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "dateOfBirth": "2000-01-15",
    "course": "Computer Science",
    "phone": "9876543210",
    "username": "john",
    "password": "john123"
  }'
```

### 5. Assign Student to Teacher (Admin only)

```bash
curl -X PUT http://localhost:8081/api/admin/students/1/assign/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 6. View All Students (Any authenticated user)

```bash
curl -X GET http://localhost:8081/api/students \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 7. View Teacher's Students (Any authenticated user)

```bash
curl -X GET http://localhost:8081/api/teachers/1/students \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Project Structure

```
src/main/java/com/tusharkumarroy/studentmanagement/
├── controller/
│   ├── AuthController.java
│   ├── AdminController.java
│   └── PublicController.java
├── dto/
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   └── RegisterRequest.java
├── entity/
│   ├── Role.java
│   ├── User.java
│   ├── Student.java
│   └── Teacher.java
├── repository/
│   ├── UserRepository.java
│   ├── StudentRepository.java
│   └── TeacherRepository.java
├── security/
│   ├── JwtUtil.java
│   ├── JwtAuthenticationFilter.java
│   ├── CustomUserDetailsService.java
│   └── SecurityConfig.java
├── service/
│   ├── AuthService.java
│   ├── StudentService.java
│   └── TeacherService.java
└── StudentManagementApplication.java
```

## Configuration

### Application Profiles

- **local**: Uses local PostgreSQL database
- **docker**: Uses Docker PostgreSQL database

### JWT Configuration

Edit `application.yaml` to customize:
- `jwt.secret`: JWT signing key (default provided)
- `jwt.expiration`: Token expiration time (default: 24 hours)

## Testing with Postman

1. Import the API endpoints into Postman
2. Register an admin user
3. Login to get JWT token
4. Add token to Authorization header: `Bearer YOUR_TOKEN`
5. Test all endpoints

## Development

### Build the project

```bash
./mvnw clean install
```

### Run tests

```bash
./mvnw test
```

## Troubleshooting

### Database Connection Issues

- **Local**: Ensure PostgreSQL is running and credentials are correct
- **Docker**: Ensure Docker Desktop is running

### Authentication Issues

- Ensure JWT token is included in Authorization header
- Token format: `Bearer <token>`
- Check token hasn't expired (24 hour default)

### Port Conflicts

If port 8081 is in use, change in `application.yaml`:
```yaml
server:
  port: 8082
```

## Future Enhancements

- Unit and integration tests
- GitHub Actions CI/CD pipeline
- Branch protection rules
- Swagger/OpenAPI documentation
- Frontend UI
- Email notifications
- Advanced search and filtering

## License

This project is for educational purposes.
