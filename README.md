Cloudsuites
==========

### Revolutionizing Condominium Management with a Cloud-First Approach  (This is just a playground project for self Learning and POCs :) )

**Cloud Suites** is an innovative, cloud-based property management system tailored specifically for the dynamic needs of condominium communities. It enhances operational efficiency, streamlines communication, and elevates the resident experience by providing a centralized platform for managing everything from amenity bookings to parking.

As condominium living evolves, Cloud Suites ensures all stakeholders—from residents and property managers to staff and owners—can enjoy real-time, secure access to essential features and data. This modern, cloud-first platform delivers superior convenience and user experience.

---

## Key Benefits

### Built for Efficiency and Security

- **Secure Identity Management**: Role-based access control ensures every user (tenant, staff, or owner) has appropriate permissions, protecting sensitive data and enhancing operational security.
  
- **Resident and Owner Portals**: Personalized dashboards allow tenants and owners to view financials, pay condo fees, request maintenance, and receive building updates—offering complete transparency and ease of use.

- **Automated Communication**: Real-time notifications via email, SMS, or in-app messages keep everyone informed, from lease reminders to amenity booking confirmations.

### Mobile-First Experience for Residents and Staff

Cloud Suites’ dedicated mobile app offers both residents and staff full control from their smartphones. Residents can: Book amenities, Access the marketplace, Submit service requests, and many more...

Staff can manage tasks, communicate with residents, and receive instant alerts for urgent issues, all on the go...

---

## Cost Savings Through Efficiency

CloudSuites helps property managers minimize operational costs by automating key processes and reducing manual oversight. 

Here's how:

- **Reduced Staffing Needs**: Automation of notifications, amenity bookings, and lease management means fewer administrative tasks, reducing the need for a large staff.
  
- **Cost-Effective**: Cloud-based technology eliminates expensive on-premises infrastructure, making Cloud Suites a competitively priced solution with extensive features.
  
- **Efficient Resource Management**: Automated workflows and real-time data access minimize time spent on administrative tasks, letting teams focus on enhancing resident satisfaction.

---

## Streamlined Operations

- **Centralized Management**: Manage all functions—amenities, parking, and notifications—through a single, easy-to-use platform with minimal manual intervention.
  
- **Scalable and Flexible**: Suitable for managing both single buildings and large portfolios, Cloud Suites scales to meet your needs without incurring extra costs for unnecessary features.

- **Enhanced Communication**: Multi-channel, automated notifications ensure seamless communication, improving efficiency and reducing the need for additional communication staff.

---

## Features Overview

### Identity Management

- **User Management**: Manage user details, roles, and permissions.
- **Tenant/Staff/Owner Management**: Handle details, leases, contracts, and communication efficiently.
- **Role-Based Access Control**: Secure and appropriate access for all user types.
- **User Authentication**: Robust security with authentication and authorization mechanisms.

### Property Management

- **Building/Unit/Lease Management**: Efficiently handle building assets, unit details, leases, and maintenance.
- **Management Companies**: Centralized management for company details and contacts.

### Amenity Management

- **Amenity Booking**: Robust booking engine with availability validation and self-service for tenants.
- **Booking Calendar**: User-friendly system for managing availability and optimizing resource use.

### Parking Management

- **Parking Spaces**: Track availability, assignments, and renewals.
- **Visitor Parking**: Manage visitor parking requests and restrictions.
- **Parking Analytics**: Insights into usage, availability, and trends for data-driven decisions.

### Notification Service

- **Automated Notifications**: Event-based messages for lease expirations, maintenance requests, and booking confirmations.
- **Customizable Templates**: Personalize messages for tenants and staff.
- **Multi-Channel Delivery**: Notifications via email, SMS, or in-app, ensuring timely communication.

---

## Additional Technical Features

### Amenity Management System

A dedicated platform for managing amenities like pools, gyms, and event spaces, providing seamless booking and usage tracking. Key features include:

- **[Amenity Management Service](https://github.com/cheeksnpeeps/cloudsuites/blob/main/modules/amenity-module/Amenity%20Management.md)**: Offers a central hub for managing property amenities, allowing staff to configure
  and control access, availability, and usage rules for shared resources like pools, gyms, or event spaces.

- **[Amenity Booking Management](https://github.com/cheeksnpeeps/cloudsuites/blob/main/modules/amenity-module/Amenity%20Booking%20Management.md)**: A robust booking engine that ensures smooth scheduling of amenities by validating
  availability, enforcing usage constraints, and supporting both tenant and staff needs.

- **[Amenity Booking Calendar Management](https://github.com/cheeksnpeeps/cloudsuites/blob/main/modules/amenity-module/Amenity%20Booking%20Calendar%20Management.md)**: A user-friendly calendar system that displays both booked and available
  slots, allowing tenants to self-manage bookings and staff to oversee and optimize amenity use.
  
### Partition Management Migration

Optimize data management by partitioning the `Amenity Bookings` table for improved system performance. Key features include:
1. **Time-Based Partitioning**: Organize large booking datasets by time intervals, boosting query performance.
2. **Automated Partition Creation**: Seamless scalability with automatic partition creation.
3. **Historical Data Archiving**: Archive or drop outdated booking data to optimize storage.
4. **Retention Policies**: Retain only necessary data for active operations, reducing storage strain.

<img width="658" alt="image" src="https://github.com/user-attachments/assets/3431e29d-53be-4da5-8741-7a4d8b2bafdb">

For a step-by-step guide on implementing and managing partitioning for the Amenity Bookings table, including
instructions on changing partition intervals, managing old partitions, and more advanced partitioning techniques, please
refer to the [Partition Management Migration Guide](https://github.com/cheeksnpeeps/cloudsuites/blob/main/modules/common-module/src/main/resources/db/migration/Partition%20Management%20Migration.md).

---

### CONCEPT - LATE Partition Management Migration

This diagram illustrates a multi-phase data migration or partitioning process for the `Amenity Bookings` table, the assumption in this scenario is that the `Amenity Bookings` table was never partitioned and now hold a large amount of data (billions of rows).

Summary of the Process:
- **Phase I:** Initial partitioning of the database to store historical data.
- **Phase II:** Decommissioning the old database, making it inactive.
- **Phase III:** Auto-partitioning as new data is added each fiscal quarter.
- **Phase IV:** Archiving old data into Parquet files and removing it from the main system.

For more details, see the CONCEPT: [CONCEPT - LATE Partition Management Migration](https://github.com/cheeksnpeeps/cloudsuites/blob/main/modules/common-module/src/main/resources/db/migration/CONCEPT%20-%20LATE%20Partition%20Management%20Migration.md)

<img width="994" alt="image" src="https://github.com/user-attachments/assets/5b780740-c458-43da-a903-016f7f95b506">



## Getting Started with Cloud Suites

Cloud Suites is built using **Java**, **Spring Boot**, **JavaScript**, and **React**, with **Maven** and **npm** managing dependencies. Follow the instructions below to get started:

### Server-Side Setup

1. Navigate to the project directory.
2. Run `mvn clean install` to install dependencies.
3. Run `mvn spring-boot:run` to start the server.

### Client-Side Setup

1. Navigate to the `src/main/resources/static/app` directory.
2. Run `npm install` to install dependencies.
3. Run `npm start` to launch the React application.

---

## Contributing and License

Contributions are welcome via issues or pull requests.  
This project is licensed under the **MIT License**.
