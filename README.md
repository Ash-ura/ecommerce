

## E-commerce Real-Time Spring Boot Application

A fully featured e-commerce backend with:

- Product CRUD (Create, Read, Update, Delete)
- Real-time updates via Server-Sent Events (SSE)
- Order placement with stock decrement on checkout
- Insight metrics per user (total products, orders, revenue, average order value)
- Basic Authentication (in-memory users)
- In-memory H2 database for rapid development
- Caffeine caching for product list
- Comprehensive unit & integration tests
- Dockerized with Dockerfile & Docker Compose

---

### 📥 Prerequisites

- Java 17+
- Maven 3.6+ (or Gradle)
- Docker & Docker Compose (for containerized run)

---

### 🛠️ Setup & Run Locally

1. **Clone the repo**
   ```bash
   git clone https://github.com/Ash-ura/ecommerce.git
   cd ecommerce-app
   ```

2. **Build the JAR**
   ```bash
   ./mvnw clean package
   ```

3. **Run with Maven**
   ```bash
   ./mvnw spring-boot:run
   ```  
   or run the JAR directly:
   ```bash
   java -jar target/ecommerce-0.0.1-SNAPSHOT.jar
   ```

4. **Access the app**
  - API base URL: `http://localhost:8081`
  - H2 console: `http://localhost:8081/h2-console`
    - JDBC URL: `jdbc:h2:mem:testdb`
    - User: `sa` 
    - Password: `sa`

---

### 🧰 API Endpoints

All endpoints require Basic Auth (`user`/`password` or `admin`/`admin`).

#### Products

| Method | Endpoint           | Description                   | Body (JSON)                             |
|--------|--------------------|-------------------------------|-----------------------------------------|
| GET    | `/products`        | List all products             | —                                       |
| GET    | `/products/stream` | SSE stream of product updates | —                                       |
| POST   | `/products`        | Create new product            | `{ "name","description","price","stock" }` |
| PUT    | `/products/{id}`   | Update existing product       | `{ "name","description","price","stock" }` |
| DELETE | `/products/{id}`   | Delete product                | —                                       |

#### Orders

| Method | Endpoint         | Description           | Body (JSON)                        |
|--------|------------------|-----------------------|------------------------------------|
| POST   | `/orders/cart`   | Place order (checkout)| `[{"productId":1,"quantity":2}]`   |
| GET    | `/orders/stream` | SSE stream of orders  | —                                  |

#### Insights

| Method | Endpoint     | Description                        | Response (JSON)                                              |
|--------|--------------|------------------------------------|--------------------------------------------------------------|
| GET    | `/insights`  | Get metrics for authenticated user | `{ totalProducts, totalOrders, totalRevenue, averageOrderValue }` |

---

### 🔒 Authentication

Basic Auth is enabled globally.  
In-memory users:

- **user** / **password** (ROLE_USER)
- **admin** / **admin** (ROLE_ADMIN)

In Postman choose Basic Auth, or with curl:
```
curl -u user:password http://localhost:8081/products
```

---

### 💾 Data Model

- **Product**: `id`, `name`, `description`, `price`, `stock`, `owner`
- **OrderEntity**: `id`, `customer`, `total`, `List<Long> productIds`
- **InsightMetrics**: `totalProducts`, `totalOrders`, `totalRevenue`, `averageOrderValue`

---

### ⚙️ Caching

- `GET /products` is cached via Caffeine (10 min expiry, max 100 entries).
- Cache evicted on create/update/delete.

---

### 🧪 Testing

**Unit Tests** under `src/test/java/com/assessment/ecommerce/service`  
Run:
```
./mvnw test -Dtest=*ServiceTest
```

**Integration Tests** under `src/test/java/com/assessment/ecommerce/controller`  
Run:
```
./mvnw test -Dtest=*ControllerIntegrationTest
```

---

### 🐳 Docker

**Build & Run**:
```
docker-compose up --build
```

**Dockerfile**:
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```

---

### 🚀 Next Steps

- Replace in-memory users with DB-backed `UserDetailsService`
- Add role-based authorization per endpoint
- Persist data in a production RDBMS (PostgreSQL, MySQL)
- Build a frontend consuming the SSE streams

---

### 📄 License

MIT License
