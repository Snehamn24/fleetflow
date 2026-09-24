#  FleetFlow

**FleetFlow** is a reliability-focused transportation booking backend built using **Java, Spring Boot, PostgreSQL, and Spring Data JPA**.

The project models a simplified ride-booking workflow including customers, drivers, vehicles, trip booking, fare calculation, payment processing, and trip lifecycle management.

Instead of being only a CRUD application, FleetFlow also focuses on common backend reliability problems such as:

- Duplicate booking requests
- Duplicate payment processing
- Concurrent vehicle allocation
- Trip state validation
- Driver and vehicle availability management

A lightweight HTML/CSS/JavaScript dashboard is included for demonstrating the booking workflow.

---

##  Features

### Customer Management
- Create customers
- Retrieve all customers
- Retrieve customer by ID
- Update customer information

### Driver Management
- Register drivers
- Track driver availability
- Prevent duplicate driving-license registration

### Vehicle Management
- Register vehicles
- Associate vehicles with drivers
- Support multiple vehicle types:
  - CAB
  - AUTO
  - BIKE
- Track vehicle status:
  - AVAILABLE
  - ASSIGNED
  - MAINTENANCE

### Trip Booking
- Create transportation bookings
- Automatically allocate an available vehicle and driver
- Filter vehicles based on requested vehicle type
- Store pickup and destination information
- Track trip creation time

### Concurrency-Safe Vehicle Allocation
FleetFlow uses **pessimistic database locking** while selecting an available vehicle.

This helps prevent multiple simultaneous booking transactions from assigning the same vehicle.

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
Optional<Vehicle> findFirstByTypeAndStatusOrderByIdAsc(
        VehicleType type,
        VehicleStatus status
);
```

---

##  Idempotent Trip Booking

Clients send an `Idempotency-Key` while creating a trip.

Example:

```http
Idempotency-Key: TRIP-TEST-001
```

FleetFlow generates a **SHA-256 fingerprint** from the booking request.

If the same request is sent again using the same idempotency key:

```text
First request  → Trip ID 2
Second request → Trip ID 2
```

No duplicate trip is created.

If the same key is reused with different booking data, FleetFlow rejects the request with:

```text
409 Conflict
```

This protects the system against duplicate booking requests caused by retries, network issues, or repeated button clicks.

---

##  Fare Calculation

Fare is calculated based on the requested vehicle type and travel distance.

Current pricing rules:

| Vehicle | Base Fare | Rate per km |
|---|---:|---:|
| CAB | ₹50 | ₹15/km |
| AUTO | ₹30 | ₹10/km |
| BIKE | ₹20 | ₹7/km |

Example:

```text
Vehicle: CAB
Distance: 8 km

Fare = ₹50 + (8 × ₹15)
     = ₹170
```

Pricing logic is maintained separately inside `FareService`.

---

##  Payment Processing

FleetFlow supports payment processing for booked trips.

Each payment stores:

- Trip
- Fare amount
- Payment status
- Unique transaction reference
- Creation timestamp

Example transaction reference:

```text
PAY-c2fe505f-c8ec-4bf6-9476-b904980d70cb
```

A trip can have only one payment record.

Repeated payment requests for the same trip return the existing payment instead of creating another payment entry.

Payment states include:

```text
PENDING
SUCCESS
FAILED
```

---

##  Trip Lifecycle

Trips follow a controlled lifecycle:

```text
CONFIRMED
    ↓
IN_PROGRESS
    ↓
COMPLETED
```

A trip may also be:

```text
CANCELLED
```

Supported operations:

```text
CONFIRMED → IN_PROGRESS
IN_PROGRESS → COMPLETED
CONFIRMED / IN_PROGRESS → CANCELLED
```

Invalid state transitions are rejected with an appropriate HTTP error.

When a trip is completed or cancelled:

```text
Vehicle → AVAILABLE
Driver  → AVAILABLE
```

allowing them to be assigned to another booking.

---

##  Demo Dashboard

FleetFlow includes a lightweight frontend built using:

- HTML
- CSS
- JavaScript
- Fetch API

The dashboard communicates directly with the Spring Boot REST APIs.

It allows a user to enter:

- Customer ID
- Pickup
- Destination
- Vehicle type
- Distance
- Idempotency key

and create a trip through the backend.

Frontend files are located in:

```text
src/main/resources/static/
```

Spring Boot serves the dashboard at:

```text
http://localhost:8080
```

---

##  Architecture

FleetFlow follows a layered Spring Boot architecture:

```text
            Demo UI
       HTML / CSS / JS
              │
              ▼
        REST Controllers
              │
              ▼
          Services
     Business Logic Layer
              │
              ▼
         Repositories
       Spring Data JPA
              │
              ▼
          PostgreSQL
```

Main backend layers:

```text
controller/
service/
repository/
entity/
dto/
```

---

##  Technology Stack

### Backend
- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- Jakarta Validation

### Database
- PostgreSQL

### Frontend
- HTML
- CSS
- JavaScript
- Fetch API

### Build & Development
- Maven
- Maven Wrapper
- IntelliJ IDEA
- Git
- GitHub

### Testing / API Verification
- cURL
- REST API testing

---

##  Project Structure

```text
fleetflow/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/fleetflow/
│   │   │       │
│   │   │       ├── controller/
│   │   │       │   ├── CustomerController.java
│   │   │       │   ├── DriverController.java
│   │   │       │   ├── HealthController.java
│   │   │       │   ├── PaymentController.java
│   │   │       │   ├── TripController.java
│   │   │       │   └── VehicleController.java
│   │   │       │
│   │   │       ├── dto/
│   │   │       │   └── request/
│   │   │       │
│   │   │       ├── entity/
│   │   │       │
│   │   │       ├── repository/
│   │   │       │
│   │   │       ├── service/
│   │   │       │   ├── CustomerService.java
│   │   │       │   ├── DriverService.java
│   │   │       │   ├── FareService.java
│   │   │       │   ├── PaymentService.java
│   │   │       │   ├── TripService.java
│   │   │       │   └── VehicleService.java
│   │   │       │
│   │   │       └── FleetflowApplication.java
│   │   │
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── index.html
│   │       │   ├── style.css
│   │       │   └── app.js
│   │       │
│   │       └── application.properties
│   │
│   └── test/
│
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

---

##  Running FleetFlow Locally

### Prerequisites

Install:

- Java 21
- PostgreSQL
- Git

Maven does not need to be installed globally because the project includes the Maven Wrapper.

---

### 1. Clone the repository

```bash
git clone https://github.com/Snehamn24/fleetflow.git
```

```bash
cd fleetflow
```

---

### 2. Create PostgreSQL Database

Create:

```text
fleetflow_db
```

The application expects PostgreSQL to be running on:

```text
localhost:5432
```

---

### 3. Configure Database Password

FleetFlow does not hardcode the PostgreSQL password.

Set the environment variable:

```text
DB_PASSWORD
```

For IntelliJ:

```text
Run
→ Edit Configurations
→ Environment Variables
→ DB_PASSWORD=<your-postgresql-password>
```

---

### 4. Run the Application

Windows:

```cmd
mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

The application starts at:

```text
http://localhost:8080
```

---

##  Important REST APIs

### Health Check

```http
GET /api/health
```

### Customers

```http
POST /api/customers
GET  /api/customers
GET  /api/customers/{id}
PUT  /api/customers/{id}
```

### Drivers

```http
POST /api/drivers
GET  /api/drivers
GET  /api/drivers/{id}
```

### Vehicles

```http
POST /api/vehicles
GET  /api/vehicles
GET  /api/vehicles/{id}
```

### Trips

```http
POST  /api/trips
GET   /api/trips
GET   /api/trips/{id}
GET   /api/trips/customer/{customerId}

PATCH /api/trips/{id}/start
PATCH /api/trips/{id}/complete
PATCH /api/trips/{id}/cancel
```

### Payments

```http
POST /api/payments/trips/{tripId}
GET  /api/payments/{id}
GET  /api/payments/trips/{tripId}
```

---

##  Example Trip Request

```http
POST /api/trips
```

Headers:

```text
Content-Type: application/json
Idempotency-Key: TRIP-TEST-001
```

Body:

```json
{
  "customerId": 1,
  "pickup": "BMSCE",
  "destination": "Majestic",
  "vehicleType": "CAB",
  "distanceKm": 8.0
}
```

Example result:

```json
{
  "id": 2,
  "pickup": "BMSCE",
  "destination": "Majestic",
  "distanceKm": 8.0,
  "fareAmount": 170.00,
  "status": "CONFIRMED"
}
```

---

##  Verified Workflow

The backend has been manually verified for the following workflow:

```text
Create Trip
     ↓
Automatic Vehicle + Driver Assignment
     ↓
Fare Calculation
     ↓
Repeat Same Booking Request
     ↓
Same Trip Returned
     ↓
Process Payment
     ↓
Repeat Payment Request
     ↓
Same Payment Returned
     ↓
Start Trip
     ↓
Complete Trip
     ↓
Driver + Vehicle Released
```

Verified examples include:

```text
8 km CAB trip → ₹170 fare
```

and repeated booking requests returning the same trip when the same idempotency key is supplied.

---

##  Backend Concepts Demonstrated

FleetFlow demonstrates:

- REST API development
- Layered application architecture
- Dependency injection
- JPA entity relationships
- Transaction management
- Database constraints
- Pessimistic locking
- Idempotent API design
- SHA-256 request fingerprinting
- Business rule validation
- Resource availability management
- State-transition validation
- Duplicate payment prevention
- PostgreSQL integration
- Error handling using HTTP status codes

---

##  Future Improvements

Potential improvements include:

- JWT-based authentication and authorization
- Resilience4j retry and circuit breaker for external payment gateways
- Automated unit and integration tests
- Testcontainers for PostgreSQL integration testing
- Swagger / OpenAPI documentation
- Docker containerization
- CI/CD with GitHub Actions
- External maps API for real distance calculation
- Production payment gateway integration

---

##  Author

**Sneha M N**

GitHub: [Snehamn24](https://github.com/Snehamn24)
