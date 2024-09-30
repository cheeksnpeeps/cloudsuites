# Amenity Booking and Calendar Service (Reactive Approach)

### Introduction
This document outlines the design and implementation of the **Amenity Booking and Calendar Service**, which leverages **Reactive Programming** to provide high concurrency, scalability, and efficiency. Built using **Spring WebFlux** and **Project Reactor**, the service adopts a non-blocking architecture for a highly responsive user experience and better resource management.

### Key Concepts

1. **Reactive Programming**: A programming paradigm that focuses on building asynchronous, non-blocking, event-driven systems. It is well-suited for handling a large number of concurrent users with minimal resource usage.
2. **Mono/Flux**: `Mono` (representing 0-1 values) and `Flux` (representing 0-N values) are key abstractions in reactive programming, enabling efficient resource utilization and non-blocking I/O operations for system interactions, such as database calls and user interactions.
3. **Schedulers**: We utilize `Schedulers.boundedElastic()` to execute blocking operations (e.g., database access) on separate thread pools, ensuring the main thread remains non-blocking.
4. **R2DBC (Reactive Relational Database Connectivity)**: R2DBC enables non-blocking communication with relational databases, a fundamental aspect of reactive programming for persistent data storage.

### Business Problem

Managing shared amenities such as gyms, meeting rooms, or other common resources in multi-tenant environments presents unique challenges:
- Handling high-demand bookings
- Managing overlapping requests
- Implementing role-based access for different user types (e.g., `TENANT`, `OWNER`, `BUILDING_SECURITY`).

The service ensures efficient booking management by providing fairness, dynamic availability updates, and real-time feedback to users, all while maintaining performance.

### Design Principles

1. **Efficiency**: Reactive programming enables the service to handle a high volume of concurrent users without overloading system resources. Non-blocking I/O ensures minimal delays, even during peak usage.
2. **Consistency**: The system ensures bookings and cancellations reflect real-time availability through reactive streams and database concurrency controls.
3. **Role-based Access**: Secure handling of different user roles is implemented via Spring Security's reactive support, ensuring that role-based actions are efficiently managed.

### Reactive Design Overview

The system consists of several interconnected layers, all built with reactive streams for seamless interaction:

1. **Controller Layer**: Exposes RESTful endpoints for booking amenities, checking availability, and managing bookings. The controller interacts with the service layer asynchronously, returning `Mono` and `Flux` for non-blocking responses.
   
2. **Service Layer**: This layer contains the business logic. It manages the booking process, checks for availability, and persists data using reactive patterns. The service layer orchestrates the flow between the controller and data access layers.

3. **DAO Layer**: The data access layer interacts with the database via **R2DBC** and `R2dbcEntityTemplate`. It ensures that queries related to availability, overlapping bookings, and occupancy checks are executed in a reactive, non-blocking manner.

### Benefits

1. **Scalability**: Non-blocking I/O operations allow the system to scale and handle many concurrent users without becoming resource-intensive.
2. **Responsiveness**: The system remains responsive, even under heavy load, providing real-time updates and feedback to users.
3. **Efficiency**: By avoiding blocking database calls, the system can handle other requests while waiting for I/O operations to complete, preventing bottlenecks.
4. **Resilience**: Reactive error handling ensures the system gracefully recovers from errors, preventing crashes and timeouts.

### Dual Data Access Layers

One of the main challenges in the project was enabling **JPA** and **Reactive** approaches to coexist, as JPA is inherently blocking and not designed for reactive programming. To achieve this, we adopted a hybrid strategy:

- **JPA** for traditional, blocking database operations where performance and concurrency were less critical.
- **R2DBC** for high-concurrency services, which required reactive, non-blocking behavior to efficiently handle large volumes of requests.

#### Service Mapping:
- **JPA** is used in low-concurrency contexts such as **property management services**. These operations, being less affected by concurrency issues, were well-suited for JPA's blocking nature.
- **R2DBC** is employed in high-concurrency scenarios, such as **user interaction services**, where reactive programming ensures scalability and better performance.

### Challenges and Solutions

#### Transaction Management
Handling transactions across both synchronous (JPA) and asynchronous (Reactive) services presented difficulties. By isolating transactions—using `JpaTransactionManager` for JPA and native R2DBC mechanisms for reactive flows—we ensured that services function independently without mixing blocking and non-blocking calls.

#### Configuration Management
Both JPA and R2DBC pull configuration details from the same environment properties. This consistent configuration strategy simplifies maintenance, allowing both layers to seamlessly interact with their respective databases without additional complexity.

### JPA Configuration (Synchronous)
JPA, built on JDBC, performs blocking I/O operations. This means that each query or transaction blocks the executing thread until the database returns a result. Our **`JpaConfig`** class handles the configuration of the JPA layer, ensuring that traditional blocking database operations are handled correctly.

### R2DBC Configuration (Reactive)
**R2DBC** (Reactive Relational Database Connectivity) is essential for high-concurrency scenarios, allowing non-blocking database operations. The **`R2dbcConfig`** class configures the reactive database access layer, enabling efficient interaction with the database without blocking threads.

Key design considerations:
- **Connection Factory Options**: These options allow for flexible configuration of database connections tailored to R2DBC.
- **Connection Pooling**: Managing initial and maximum pool sizes ensures optimal resource management.
- **Reactive Transactions**: Managed through constructs like `R2dbcTransactionManager` for high-throughput operations.

### Key Advantages of the Hybrid Approach

1. **Scalability**: R2DBC ensures that the high-concurrency services can scale efficiently to handle many simultaneous requests.
2. **Simplicity**: For services that do not require reactive flows, JPA provides a familiar and straightforward ORM layer, keeping the complexity low.
3. **Flexibility**: The dual access strategy provides the flexibility to choose the best data access approach for each specific use case.

### Conclusion
This hybrid approach enables us to use **JPA** for traditional, blocking database operations and **R2DBC** for highly scalable, non-blocking reactive operations. By carefully managing configurations, isolating transaction management, and strategically mapping services to their respective data access technologies, we achieved a scalable, efficient, and highly responsive system. 
