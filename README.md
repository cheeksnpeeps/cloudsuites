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

## Contributing

Contributions are welcome. Please open an issue or submit a pull request.

## License

This project is licensed under the MIT License.