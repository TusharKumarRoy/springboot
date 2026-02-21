# API Testing Quick Reference

## Base URL
```
Local: http://localhost:8081
Docker: http://localhost:8081
```

## 1. Register Admin

**Endpoint:** `POST /api/auth/register`

**Body:**
```json
{
  "username": "admin",
  "password": "admin123",
  "email": "admin@example.com",
  "role": "ADMIN"
}
```

---

## 2. Register Teacher

**Endpoint:** `POST /api/auth/register`

**Body:**
```json
{
  "username": "teacher1",
  "password": "teacher123",
  "email": "teacher1@example.com",
  "role": "TEACHER"
}
```

---

## 3. Register Student

**Endpoint:** `POST /api/auth/register`

**Body:**
```json
{
  "username": "student1",
  "password": "student123",
  "email": "student1@example.com",
  "role": "STUDENT"
}
```

---

## 4. Login (Any User)

**Endpoint:** `POST /api/auth/login`

**Body:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIs...",
  "type": "Bearer",
  "username": "admin",
  "email": "admin@example.com",
  "role": "ADMIN"
}
```

**Save the token for subsequent requests!**

---

## 5. Create Teacher (Admin Only)

**Endpoint:** `POST /api/admin/teachers`

**Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json
```

**Body:**
```json
{
  "name": "Mr. Smith",
  "email": "smith@school.com",
  "phone": "1234567890",
  "subject": "Mathematics",
  "username": "smith",
  "password": "smith123"
}
```

---

## 6. Create Student (Admin Only)

**Endpoint:** `POST /api/admin/students`

**Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json
```

**Body:**
```json
{
  "name": "John Doe",
  "email": "john@school.com",
  "dateOfBirth": "2005-03-15",
  "course": "Computer Science",
  "phone": "9876543210",
  "username": "john",
  "password": "john123"
}
```

---

## 7. Get All Students (Any Authenticated User)

**Endpoint:** `GET /api/students`

**Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
```

---

## 8. Get All Teachers (Any Authenticated User)

**Endpoint:** `GET /api/teachers`

**Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
```

---

## 9. Assign Student to Teacher (Admin Only)

**Endpoint:** `PUT /api/admin/students/{studentId}/assign/{teacherId}`

**Example:** `PUT /api/admin/students/1/assign/1`

**Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
```

---

## 10. Get Teacher's Students (Any Authenticated User)

**Endpoint:** `GET /api/teachers/{teacherId}/students`

**Example:** `GET /api/teachers/1/students`

**Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
```

---

## 11. Update Student (Admin Only)

**Endpoint:** `PUT /api/admin/students/{id}`

**Example:** `PUT /api/admin/students/1`

**Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json
```

**Body:**
```json
{
  "name": "John Doe Updated",
  "email": "john@school.com",
  "dateOfBirth": "2005-03-15",
  "course": "Software Engineering",
  "phone": "1111111111"
}
```

---

## 12. Delete Student (Admin Only)

**Endpoint:** `DELETE /api/admin/students/{id}`

**Example:** `DELETE /api/admin/students/1`

**Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
```

---

## 13. Unassign Student from Teacher (Admin Only)

**Endpoint:** `PUT /api/admin/students/{studentId}/unassign`

**Example:** `PUT /api/admin/students/1/unassign`

**Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
```

---

## Testing Workflow

1. **Register Admin** → Get user created confirmation
2. **Login as Admin** → Get JWT token
3. **Create Teacher** (using admin token) → Teacher created
4. **Create Student** (using admin token) → Student created
5. **Assign Student to Teacher** (using admin token)
6. **Login as Teacher** → Get teacher JWT token
7. **View Students** (using teacher token) → Should see all students
8. **View Teacher's Students** (using teacher token) → Should see assigned students
9. **Login as Student** → Get student JWT token
10. **View All Data** (using student token) → Read-only access

---

## Expected Response Codes

- `200 OK` - Successful GET/PUT
- `201 Created` - Successful POST
- `400 Bad Request` - Invalid data
- `401 Unauthorized` - Missing or invalid token
- `403 Forbidden` - Insufficient permissions (wrong role)
- `404 Not Found` - Resource not found

---

## Common Issues

### 401 Unauthorized
- Token missing from Authorization header
- Token expired (24 hour limit)
- Invalid token format (must be `Bearer <token>`)

### 403 Forbidden
- User role doesn't have permission
- Admin endpoints require ADMIN role
- Check the role in login response

### 400 Bad Request
- Missing required fields
- Invalid email format
- Duplicate username/email
- Invalid date format (use YYYY-MM-DD)
