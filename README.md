Amenity Management System
=========================

The **Amenity Management System** is a comprehensive platform designed to streamline the management, scheduling, and
utilization of property amenities. It provides both tenants and staff with an efficient way to handle bookings, optimize
resource allocation, and ensure seamless amenity use.

### Key Features:

- **[Amenity Management Service](https://github.com/cheeksnpeeps/cloudsuites/blob/main/modules/amenity-module/Amenity%20Management.md)**: Offers a central hub for managing property amenities, allowing staff to configure
  and control access, availability, and usage rules for shared resources like pools, gyms, or event spaces.

- **[Amenity Booking Management](https://github.com/cheeksnpeeps/cloudsuites/blob/main/modules/amenity-module/Amenity%20Booking%20Management.md)**: A robust booking engine that ensures smooth scheduling of amenities by validating
  availability, enforcing usage constraints, and supporting both tenant and staff needs.

- **[Amenity Booking Calendar Management](https://github.com/cheeksnpeeps/cloudsuites/blob/main/modules/amenity-module/Amenity%20Booking%20Calendar%20Management.md)**: A user-friendly calendar system that displays both booked and available
  slots, allowing tenants to self-manage bookings and staff to oversee and optimize amenity use.

This system fosters convenience for tenants and operational efficiency for property managers, leading to better overall
property management.

---

Partition Management Migration
=============================

## Overview

Our Property Management System deals with a large volume of bookings daily, making efficient data management critical. To optimize query performance and maintain system efficiency, we partition the `Amenity Bookings` table based on time intervals. This approach divides the data into smaller, more manageable segments, which improves performance and simplifies maintenance.
For a detailed guide on how to implement and manage partitioning for the `Amenity Bookings` table, including how to handle changes in partitioning intervals and manage partitions effectively, please refer to the ([Partition Management Migration](https://github.com/cheeksnpeeps/cloudsuites/blob/74a49c302733bf8d68847d2af89f20701ca453bf/modules/common-module/src/main/resources/db/migration/Partition%20Management%20Migration.md)).

---
# Cloudsuites

Cloudsuites is a web application built with Java, Spring Boot, JavaScript, and React. It uses Maven and npm for
dependency management.

## Project Structure

The project is structured into two main parts: the server-side code written in Java and the client-side code written in
JavaScript (React).

### Server-Side

The server-side code is located in the `src/main/java` directory. It's a Spring Boot application that serves the static
files of the React application and provides API endpoints that the React application can interact with.

### Client-Side

The client-side code is located in the `src/main/resources/static/app` directory. It's a React application that is built
with npm and served by the Spring Boot application.

## Getting Started

To get started with the project, you need to install the dependencies and start the server.

### Server-Side

1. Navigate to the project directory.
2. Run `mvn clean install` to install the dependencies.
3. Run `mvn spring-boot:run` to start the server.

### Client-Side

1. Navigate to the `src/main/resources/static/app` directory.
2. Run `npm install` to install the dependencies.
3. Run `npm start` to start the React application.

Please note that the React application is built and served by the Spring Boot application, so you don't need to start it
separately in production.

---

## Contributing

Contributions are welcome. Please open an issue or submit a pull request.

## License

This project is licensed under the MIT License.
