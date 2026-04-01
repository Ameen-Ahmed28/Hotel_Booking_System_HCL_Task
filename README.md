# Hotel Booking System

A full-stack hotel booking application with React frontend and Spring Boot backend.

## 🚀 Features

### 🌍 Multi-City Hotel Search
- Select from Bangalore, Chennai, or Mumbai
- View hotels specific to each selected city

### 📅 Date-Based Booking
- Choose Check-in and Check-out dates
- Booking is enabled only after selecting valid dates

### 🏨 Hotel Listings
- Displays 6 hotels per city
- Each hotel includes:
  - Image preview
  - Name & location
  - Price per night
  - Amenities (WiFi, Gym, Pool, etc.)

### 🔍 Hotel Details Page
- Full hotel description
- Amenities displayed as tags
- Image carousel (multiple images)
- "Book Now" button (enabled after date selection)

### 🎨 Modern UI
- Airbnb-inspired design
- Responsive layout
- Clean card-based interface
- Smooth hover effects

---

## 🧱 Tech Stack

### Frontend
- ⚛️ React (Vite)
- 🎨 Tailwind CSS
- 🔄 React Router
- 🌐 Axios

### Backend
- ☕ Spring Boot 3.x
- 🔐 Spring Security with JWT
- 🛢️ MySQL Database
- 📧 Email Service
- 🔗 REST APIs

---

## 📋 Prerequisites

Before running this application, make sure you have the following installed:

- **Java 17+** - [Download Java](https://adoptium.net/)
- **Node.js 18+** - [Download Node.js](https://nodejs.org/)
- **MySQL 8.0+** - [Download MySQL](https://dev.mysql.com/downloads/)
- **Maven 3.8+** - [Download Maven](https://maven.apache.org/download.cgi)
- **Git** - [Download Git](https://git-scm.com/)

---

## ⚙️ Environment Setup

### Backend Environment Variables

Create a `.env` file in the `backend/` directory (or set environment variables):

```properties
# Database Configuration
DB_URL=jdbc:mysql://localhost:3306/hotel_booking
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

# JWT Configuration
JWT_SECRET=your_jwt_secret_key_here

# Email Configuration (Optional)
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_email_app_password
```

### Frontend Environment Variables

Create a `.env` file in the `frontend/` directory:

```properties
VITE_API_BASE_URL=http://localhost:8080/api
```

---

## 🚀 Installation & Running

### 1. Clone the Repository

```bash
git clone https://github.com/Ameen-Ahmed28/Hotel_Booking_System_HCL_Task.git
cd Hotel_Booking_System_HCL_Task
```

### 2. Database Setup

```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE hotel_booking;

# Exit MySQL
exit;
```

### 3. Backend Setup

```bash
# Navigate to backend directory
cd backend

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### 4. Frontend Setup

Open a new terminal:

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Run development server
npm run dev
```

The frontend will start on `http://localhost:5173`

---

## 🐳 Docker Setup (Alternative)

You can also run the entire application using Docker Compose:

```bash
# Build and run all services
docker-compose up --build

# Run in detached mode
docker-compose up -d
```

This will start:
- Frontend on `http://localhost:80`
- Backend on `http://localhost:8080`
- MySQL on port `3306`

---

## 📁 Project Structure

```
Hotel_Booking_System_HCL_Task/
├── backend/                    # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/hotel/system/
│   │   │   │       ├── config/         # Configuration classes
│   │   │   │       ├── controller/     # REST Controllers
│   │   │   │       ├── dto/            # Data Transfer Objects
│   │   │   │       ├── entity/         # JPA Entities
│   │   │   │       ├── exception/      # Exception handlers
│   │   │   │       ├── repository/     # Data repositories
│   │   │   │       ├── security/       # Security components
│   │   │   │       └── service/        # Business logic
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                   # React frontend
│   ├── src/
│   │   ├── api/                # API configuration
│   │   ├── components/         # Reusable components
│   │   ├── context/            # React Context
│   │   ├── pages/              # Page components
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── Dockerfile
│   ├── package.json
│   └── vite.config.js
├── docker-compose.yml
└── README.md
```

---

## 🔗 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `POST /api/auth/verify-email` - Verify email address

### Hotels
- `GET /api/hotels` - Get all hotels
- `GET /api/hotels/{id}` - Get hotel by ID
- `GET /api/hotels/city/{city}` - Get hotels by city

### Bookings
- `POST /api/bookings` - Create booking
- `GET /api/bookings/user/{userId}` - Get user bookings
- `PUT /api/bookings/{id}/cancel` - Cancel booking

### Admin
- `GET /api/admin/bookings` - Get all bookings (Admin only)
- `GET /api/admin/users` - Get all users (Admin only)

---

## 📝 License

This project is created for educational purposes as part of an HCL Task.

---

## 👤 Author

**Ameen Ahmed**
- GitHub: [@Ameen-Ahmed28](https://github.com/Ameen-Ahmed28)