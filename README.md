# cognevance_onlineCourseManagement

Online Course Management System
A full-stack web app for managing online courses and student enrollments. Admins create and manage courses; students browse, enroll and track progress.
Tech stack
Backend: Java 17, Spring Boot 3, Spring Security (JWT, BCrypt), Spring Data JPA, Bean Validation
Frontend: React 18 (Vite), React Router, Axios, responsive CSS
Database: MySQL (in-memory H2 by default for quick local runs)
Features
Register / login with JWT, roles STUDENT and ADMIN, protected routes
Course CRUD (admin), enroll / unenroll (student)
Search by title or instructor, filter by category and level
Progress tracking with percentage bars
Admin dashboard: courses, enrollments, students
Form validation on frontend and backend, global exception handling
Responsive layout, light and dark theme
Project structure
backend/    Spring Boot app (controller, model, repository, security, config, dto, exception)
frontend/   React app (pages, auth context, api client)
database/   schema.sql (reference schema)
Run locally
Backend (needs Java 17 and Maven)
cd backend
mvn spring-boot:run
Runs on http://localhost:8080 with in-memory H2. For MySQL set DB_URL, DB_USER, DB_PASSWORD (see application.properties).
Frontend (needs Node 18+)
cd frontend
cp .env.example .env
npm install
npm run dev
Open http://localhost:5173.
Demo admin: admin@course.com / Admin@123 (seeded on first run). Students sign up from the UI.
REST API documentation
Base URL: /api. Protected endpoints need Authorization: Bearer <token>.
Method
Endpoint
Role
Description
POST
/auth/register
Public
Register student {name, email, password}
POST
/auth/login
Public
Login {email, password} returns token, name, email, role
GET
/courses?search=&category=&level=
Any user
List and filter courses
GET
/courses/{id}
Any user
Course details
POST
/courses
Admin
Create course
PUT
/courses/{id}
Admin
Update course
DELETE
/courses/{id}
Admin
Delete course and its enrollments
POST
/enrollments/{courseId}
Student
Enroll
DELETE
/enrollments/{courseId}
Student
Unenroll
GET
/enrollments/my
Student
My enrollments with progress
PUT
/enrollments/{courseId}/progress
Student
Update {completedLessons}
GET
/enrollments
Admin
All enrollments
GET
/users
Admin
All users
Error format: { "message": "...", "errors": { "field": "reason" } }
Database schema
users (id, name, email UNIQUE, password, role) courses (id, title, description, category, level, instructor, total_lessons) enrollments (id, user_id FK, course_id FK, completed_lessons, enrolled_at, UNIQUE(user_id, course_id))
Full SQL in database/schema.sql.
Deployment
Database: create a MySQL instance (Railway, Aiven or PlanetScale-compatible) and copy its JDBC URL.
Backend (Render, Docker or Java web service): root backend, build mvn clean package -DskipTests, start java -jar target/coursemgmt-1.0.0.jar. Environment variables: DB_URL, DB_USER, DB_PASSWORD, JWT_SECRET (32+ chars), CORS_ORIGINS (your frontend URL).
Frontend (Vercel or Netlify): root frontend, build npm run build, output dist. Environment variable: VITE_API_URL (your backend URL). vercel.json handles SPA routing.
Screenshots
Add screenshots here (login, courses, my learning, admin dashboard).
Links
Live frontend: add link
Live backend: add link
GitHub: add link