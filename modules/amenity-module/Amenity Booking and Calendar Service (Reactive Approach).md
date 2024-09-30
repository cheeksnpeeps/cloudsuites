# Amenity Booking and Calendar Service (Reactive Approach) - Technical Documentation

### Introduction
This document provides a comprehensive overview of the **Amenity Booking and Calendar Service**. The service is built using a **Reactive Programming** paradigm, designed to handle **high concurrency**, **scalability**, and **efficiency**. This approach leverages **Spring WebFlux** and **Project Reactor**, offering non-blocking I/O operations, backpressure support, and a highly responsive user experience.

### Key Concepts

1. **Reactive Programming**: Reactive systems are event-driven, asynchronous, and non-blocking by nature, making them ideal for handling a large number of simultaneous requests with minimal resource consumption.
2. **Mono/Flux**: The use of `Mono` (0-1 results) and `Flux` (0-N results) allows for better resource utilization, ensuring that our system handles user interactions, database calls, and other I/O-bound operations without blocking.
3. **Schedulers**: Leveraging `Schedulers.boundedElastic()` ensures non-blocking access to external services (like databases) while maintaining a clean separation of computational and I/O tasks.
4. **R2DBC (Reactive Relational Database Connectivity)**: The system uses R2DBC for non-blocking communication with relational databases, providing the benefits of reactive programming even at the data layer.

### Business Problem Addressed

Managing amenities, such as gym bookings, meeting rooms, or other shared resources, in multi-tenant environments can be cumbersome. High demand, multiple overlapping requests, and different user roles require an efficient and dynamic system that ensures fairness, availability, and performance.

### Design Principles

1. **Efficiency**: With a high volume of users interacting with the system concurrently, the use of non-blocking, reactive code ensures a fast and scalable solution. Users experience minimal delays, even during peak usage.
2. **Consistency and Accuracy**: The system ensures that bookings and cancellations reflect real-time availability of amenities. This is achieved using reactive streams and database-level concurrency control.
3. **User Role-based Access**: Different user roles, such as `TENANT`, `OWNER`, and `BUILDING_SECURITY`, are handled securely and efficiently with Spring Security's reactive support.

### Reactive Design Overview

The Amenity Booking service is composed of several layers that interconnect seamlessly using reactive streams:
1. **Controller Layer**: Exposes RESTful endpoints for booking amenities, cancelling bookings, checking availability, etc. This layer interacts with the service layer in a non-blocking fashion, using `Mono` and `Flux` for responses.
2. **Service Layer**: This layer handles the business logic, interacting with both the controller and the DAO layer. It manages booking constraints, availability checks, and data persistence using reactive patterns.
3. **DAO Layer**: This layer uses R2DBC and `R2dbcEntityTemplate` for reactive, non-blocking interaction with the database. Complex queries are executed efficiently, handling criteria such as overlapping bookings and occupancy checks in real-time.

### Benefits of the Reactive Approach

1. **Scalability**: Non-blocking operations allow the system to handle a massive number of concurrent users, ensuring that resources like CPU and memory are utilized efficiently.
2. **Responsiveness**: The system remains highly responsive under load, providing real-time updates and feedback to users.
3. **Efficiency**: With non-blocking database calls, the system can continue processing other requests while waiting for I/O operations to complete, avoiding performance bottlenecks.
4. **Error Resilience**: Reactive error handling ensures the system gracefully manages failure scenarios without blocking or crashing.

### **Dual Data Access Layers**: 

To successfully enable both **JPA** and **Reactive** approaches to coexist in the project, we had to address a few key challenges, as JPA was not originally designed with **Reactive** paradigms in mind. This involved separating concerns related to each database access strategy while ensuring a seamless experience in terms of transaction management and configuration.

   - We implemented two distinct configurations: one for **JPA** and another for **R2DBC (Reactive)**. This approach allows us to choose between synchronous JPA for traditional blocking database access and non-blocking reactive access with R2DBC for scenarios where reactive behavior was necessary.
   With both JPA and R2DBC coexisting, we decided to have different services mapped to the appropriate data access technology:
   
   - **JPA for Property Management Services**: Since property management services tend to have relatively fewer concurrency concerns, we utilized JPA's synchronous nature for these operations.
   - **Reactive for High-Concurrency Services**: For services that required high scalability and non-blocking behavior, such as user interaction services, we opted for R2DBC and reactive flows. This allowed the services to handle a large number of concurrent requests efficiently.

### **Challenges and Solutions**:
   - **Transaction Management**: Managing transactions across synchronous (JPA) and asynchronous (Reactive) services can lead to complications. By keeping transactions isolated (JPA transactions with `JpaTransactionManager` and reactive flows managed by R2DBC), we ensured that each service could work independently without mixing blocking and non-blocking calls.
   - **Consistent Configuration Management**: Using `Environment` and external configuration files ensured that the different data access strategies could pull their respective properties from the same source, making maintenance easier.
   
#### **JPA Configuration (Synchronous)**:
JPA, being built on top of JDBC, relies on blocking I/O. This means that when JPA performs database operations, it waits for the database to respond before continuing execution. 
Each query execution or transaction commit blocks the current thread until the operation completes.   
Our `JpaConfig` class configures JPA, which is typically synchronous and blocking in nature.

#### **R2DBC Configuration (Reactive)**:
   The `R2dbcConfig` class configures the reactive database access using **R2DBC**. Since R2DBC is non-blocking and doesn’t rely on JDBC, it allows for truly reactive database operations, which are crucial in high-concurrency environments.
  Reactive programming aims to free up threads by avoiding such blocking operations, allowing the system to handle more concurrent requests with fewer threads. 
**Key Design Considerations**:
   - The use of `ConnectionFactoryOptions` enables a flexible and programmatic way of building database connection factories tailored to R2DBC.
   - Properties related to connection pools (`initial-size`, `max-size`, etc.) ensure optimal resource management in a reactive context.
   - Reactive transactions, when needed, can be managed using `R2dbcTransactionManager` or similar constructs depending on use cases.

### **Key Advantages**:
   - **Scalability and Performance**: By leveraging R2DBC, we enabled better scalability for the parts of the application that required high throughput and concurrency.
   - **Simplicity in Use**: The decision to keep JPA for traditional, blocking workflows where performance was not a concern allowed us to retain the simplicity and familiarity of the JPA ecosystem.

### Conclusion:
In summary, this approach allowed us to adopt a hybrid data access strategy—using **JPA** where a blocking, traditional ORM approach was more suitable, and **R2DBC** for highly scalable, non-blocking reactive operations. By carefully managing configurations, transaction management, and service separation, we enabled both technologies to coexist smoothly in the same application.

The Amenity Booking and Calendar Service is a prime example of how **Reactive Programming** can enhance the performance and scalability of modern applications. By using non-blocking I/O, the system is able to handle high-demand operations efficiently, making it well-suited for environments with a large number of concurrent users.
