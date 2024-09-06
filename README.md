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

# Partition Management Migration

## Overview

Our Property Management System deals with a large volume of bookings daily, making efficient data management critical. To optimize query performance and maintain system efficiency, we partition the `Amenity Bookings` table based on time intervals. This approach divides the data into smaller, more manageable segments, which improves performance and simplifies maintenance.
For a detailed guide on how to implement and manage partitioning for the `Amenity Bookings` table, including how to handle changes in partitioning intervals and manage partitions effectively, please refer to the ([Partition Management Migration](https://github.com/cheeksnpeeps/cloudsuites/blob/74a49c302733bf8d68847d2af89f20701ca453bf/modules/common-module/src/main/resources/db/migration/Partition%20Management%20Migration.md)).

## Requirements Breakdown

1. **Table Creation:**
   - Ensure the partitioning table `${partitioning.prefix}` and `partition_log` are created.

2. **Data Safety:**
   - Implement a check to prevent dropping partitions with existing data.

3. **Interval Change Handling:**
   - Drop and recreate future partitions if the partitioning interval changes, ensuring no data loss.

4. **Partition Creation:**
   - Automatically create partitions for the current and next year based on the defined frequency.

5. **Concurrency Control:**
   - Use `pg_advisory_lock` and `pg_advisory_unlock` to manage concurrent partition modifications.

6. **Performance Consideration:**
   - Execute partition management operations during off-hours or maintenance windows.

7. **Version Control:**
   - Track migration and partition changes across development, staging, and production environments, with proper auditing mechanisms.

## Setup

1. **Run the Migration Script:**

   Execute the `V1__create_or_manage_partitions.sql` script to apply partition management logic to your database.

   ```sql
   \i V1__create_or_manage_partitions.sql

## Contributing

Contributions are welcome. Please open an issue or submit a pull request.

## License

This project is licensed under the MIT License.
