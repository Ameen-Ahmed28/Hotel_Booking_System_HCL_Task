Hotel Booking System

A modern hotel booking web application that allows users to explore hotels across multiple cities, view detailed information, and simulate booking with an intuitive UI.

🚀 Features
🌍 Multi-City Hotel Search
Select from Bangalore, Chennai, Mumbai
View hotels specific to each city
📅 Date-Based Booking
Select Check-in and Check-out dates
Booking enabled only after selecting valid dates
🏨 Hotel Listings
Displays 6 hotels per city
Each hotel includes:
Image preview
Name & location
Price per night
Amenities (WiFi, Gym, Pool, etc.)
🔍 Hotel Details Page
Full hotel description
Amenities displayed as tags
Image carousel (multiple images)
"Book Now" button (enabled after date selection)
🎨 Modern UI
Airbnb-inspired design
Responsive layout
Clean card-based interface
Smooth hover effects
🧱 Tech Stack
Frontend
⚛️ React (Vite)
🎨 CSS / Tailwind (optional)
🔄 React Router
Backend (Optional / Future Scope)
☕ Spring Boot
🛢️ MySQL
🔗 REST APIs
📂 Project Structure
hotel-booking-app/
│
├── src/
│   ├── components/
│   │   ├── Navbar.jsx
│   │   ├── HotelCard.jsx
│   │   ├── DatePicker.jsx
│   │
│   ├── pages/
│   │   ├── Home.jsx
│   │   ├── HotelList.jsx
│   │   ├── HotelDetails.jsx
│   │
│   ├── data/
│   │   └── hotels.json
│   │
│   ├── App.jsx
│   └── main.jsx
│
└── README.md
📊 Data Structure

Each hotel contains:

{
  id: number,
  name: string,
  city: string,
  location: string,
  price: number,
  description: string,
  amenities: string[],
  images: string[]
}
🔄 Application Flow
User selects:
City
Check-in & Check-out dates
Redirect to Hotel Listings
Filtered by selected city
Click on a hotel
Navigate to Hotel Details page
View:
Images (carousel)
Amenities
Description
Book Now
Enabled only after selecting dates
🧠 Key Concepts Used
React Hooks (useState, useEffect)
Component-based architecture
Routing using React Router
Conditional rendering
State management
JSON-based mock data
⚡ Installation & Setup
Clone the repository
git clone https://github.com/your-username/hotel-booking-app.git
Navigate to project folder
cd hotel-booking-app
Install dependencies
npm install
Run the app
npm run dev
