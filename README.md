
# FreightFox – Dispatch Load Balancer

A backend application built using Java and Spring Boot to automatically assign delivery orders to suitable vehicles based on delivery priority, vehicle capacity, and proximity.

## Live Application

- **Base URL:** https://freightfox-dispatch-load-balancer-3.onrender.com
- **Swagger UI:** https://freightfox-dispatch-load-balancer-3.onrender.com/swagger-ui/index.html
- **GitHub:** https://github.com/Manolittle/freightfox-dispatch-load-balancer

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven
- REST APIs
- Swagger / OpenAPI
- Docker
- Render (Deployment)

## Features

- Create, retrieve, and manage delivery orders.
- Register and manage delivery vehicles.
- Automatically assign orders to available vehicles.
- Support manual vehicle assignment.
- Prioritize orders based on HIGH, MEDIUM, and LOW priority.
- Validate vehicle capacity before assignment.
- Calculate distance using the Haversine formula.
- Update order and vehicle statuses after assignment.
- Complete deliveries and release vehicles for new assignments.
- Handle validation errors and business exceptions.

## Dispatch Logic

1. Retrieve pending delivery orders.
2. Sort orders by priority: HIGH, MEDIUM, then LOW.
3. Find available vehicles with sufficient capacity.
4. Calculate the distance between the vehicle and pickup location using the Haversine formula.
5. Select the nearest suitable vehicle.
6. Assign the order and update the order status to ASSIGNED.
7. Update the vehicle status to BUSY.

When delivery is completed, the order status changes to DELIVERED and the vehicle becomes AVAILABLE.

## API Endpoints

### Orders

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/orders` | Create an order |
| GET | `/api/orders` | Get all orders |
| GET | `/api/orders/{id}` | Get order by ID |
| PUT | `/api/orders/{id}/complete` | Complete delivery |

### Vehicles

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/vehicles` | Register a vehicle |
| GET | `/api/vehicles` | Get all vehicles |
| GET | `/api/vehicles/{id}` | Get vehicle by ID |
| PUT | `/api/vehicles/{id}/status` | Update vehicle status |

### Dispatch

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/dispatch/assign/{orderId}` | Manually assign an order |
| POST | `/api/dispatch/auto-assign` | Automatically assign pending orders |

## Database

PostgreSQL is used for persistent storage.

Main entities:
- DeliveryOrder
- Vehicle
- DispatchAssignment

## Run Locally

### Prerequisites
- Java 21
- PostgreSQL
- Maven

### Steps

1. Clone the repository:

   ```bash
   git clone https://github.com/Manolittle/freightfox-dispatch-load-balancer.git
   ```

2. Navigate to the project:

   ```bash
   cd freightfox-dispatch-load-balancer
   ```

3. Configure your PostgreSQL database and environment variables:

   ```text
   DB_URL=jdbc:postgresql://localhost:5432/dispatch_db
   DB_USERNAME=postgres
   DB_PASSWORD=your_password
   ```

4. Run the application:

   ```bash
   ./mvnw spring-boot:run
   ```

5. Open Swagger UI:

   ```text
   http://localhost:8080/swagger-ui/index.html
   ```

## Testing

Run the test suite using:

```bash
./mvnw clean test
```

## Deployment

The application is deployed on Render with PostgreSQL as the database.

---

**Developed as part of the FreightFox Backend Assignment.**
