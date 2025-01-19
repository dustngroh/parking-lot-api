# Parking Lot API

## Overview

Welcome to the **Parking Lot API**! This project is the backend for the Parking Lot Management System, built using **Spring Boot**. It provides RESTful API endpoints for managing parking lots, reservations, and user accounts. This project handles the business logic, data persistence, and integration with the frontend application ([parking-lot-ui](https://github.com/dustngroh/parking-lot-ui)).

The system allows users to register, log in, and manage parking lot reservations, while administrators can oversee parking lot operations.

---

## Features

### User Features:

- User registration and login
- Create, view, and delete parking lot reservations
- Check available parking spaces in real time

### Admin Features:

- Add, update, and delete parking lots
- View and manage reservations across all parking lots

### Additional Features:

- Secure authentication using **JWT** (JSON Web Tokens)
- API designed to handle concurrency and scalability
- Integration-ready for the frontend application built with **Next.js**

---

## Technology Stack

### Backend:

- **Framework**: Spring Boot
- **Database**: PostgreSQL
- **Authentication**: JWT-based authentication
- **Build Tool**: Maven

### Frontend (for reference):

- **Framework**: [parking-lot-ui](https://github.com/dustngroh/parking-lot-ui) (built with Next.js)
- **Styling**: Tailwind CSS

### Development Tools:

- **Version Control**: Git and GitHub
- **Testing**: JUnit 5 with Mockito and Spring Boot testing utilities
- **Containerization**: Docker (planned for deployment)

---

## API Endpoints

Here are some key API endpoints provided by the backend:

- **Parking Lots**:
    - `GET /api/parkinglots` - Retrieve all parking lots
    - `GET /api/parkinglots/{name}` - Retrieve details of a specific parking lot
    - `POST /api/parkinglots` - Add a new parking lot (Admin only)
    - `DELETE /api/parkinglots/{id}` - Delete a parking lot (Admin only)

- **Reservations**:
    - `GET /api/reservations/user/{username}` - View reservations for a specific user
    - `POST /api/reservations` - Create a new reservation
    - `DELETE /api/reservations/{id}` - Cancel a reservation

- **Users**:
    - `POST /api/users/register` - Register a new user
    - `PUT /api/users/{id}` - Update user details
    - `PATCH /api/users/{id}/password` - Change user password

---

### 🚧 Work in Progress

This project is actively under development, with new features and improvements being added regularly. It serves as the backbone of the Parking Lot Management System, ensuring robust and scalable functionality.

---

For the frontend implementation, visit the [parking-lot-ui](https://github.com/dustngroh/parking-lot-ui) repository.
