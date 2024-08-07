package com.cloudsuites.framework.webapp;

import com.cloudsuites.framework.services.common.exception.*;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ProblemDetails> handleCustomException(CustomException ex, HttpServletRequest request) {
        logger.error("CustomException occurred: URI={}, Message={}", request.getRequestURI(), ex.getMessage(), ex);

        ProblemDetails problemDetails = ProblemDetails.builder()
                .withTitle("Custom Error")
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withDetail(ex.getMessage())
                .withInstance(URI.create(request.getRequestURI()).getPath())
                .withTimestamp(ZonedDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetails);
    }

    @ExceptionHandler(NotFoundResponseException.class)
    protected ResponseEntity<ProblemDetails> handleNotFoundResponseException(NotFoundResponseException ex, HttpServletRequest request) {
        logger.error("NotFoundResponseException occurred: URI={}, Message={}", request.getRequestURI(), ex.getMessage(), ex);
        ProblemDetails problemDetails = ProblemDetails.builder()
                .withTitle("Resource Not Found")
                .withStatus(HttpStatus.NOT_FOUND.value())
                .withDetail(ex.getMessage())
                .withInstance(URI.create(request.getRequestURI()).getPath())
                .withTimestamp(ZonedDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetails);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    protected ResponseEntity<ProblemDetails> usernameAlreadyExistsException(UsernameAlreadyExistsException ex, HttpServletRequest request) {
        logger.error("UsernameAlreadyExistsException occurred: URI={}, Message={}", request.getRequestURI(), ex.getMessage(), ex);
        ProblemDetails problemDetails = ProblemDetails.builder()
                .withTitle("Username Already Exists")
                .withStatus(HttpStatus.CONFLICT.value())
                .withDetail(ex.getMessage())
                .withInstance(URI.create(request.getRequestURI()).getPath())
                .withTimestamp(ZonedDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetails);
    }

    @ExceptionHandler(InvalidOperationException.class)
    protected ResponseEntity<ProblemDetails> handleNotFoundResponseException(InvalidOperationException ex, HttpServletRequest request) {
        logger.error("InvalidOperationException occurred: URI={}, Message={}", request.getRequestURI(), ex.getMessage(), ex);
        ProblemDetails problemDetails = ProblemDetails.builder()
                .withTitle("Invalid Operation")
                .withStatus(HttpStatus.CONFLICT.value())
                .withDetail(ex.getMessage())
                .withInstance(URI.create(request.getRequestURI()).getPath())
                .withTimestamp(ZonedDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetails);
    }

    @ExceptionHandler(MalformedJwtException.class)
    protected ResponseEntity<ProblemDetails> handleMalformedJwtException(MalformedJwtException ex, HttpServletRequest request) {
        logger.error("InvalidOperationException occurred: URI={}, Message={}", request.getRequestURI(), ex.getMessage(), ex);
        ProblemDetails problemDetails = ProblemDetails.builder()
                .withTitle("Token validation error")
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withDetail(ex.getMessage())
                .withInstance(URI.create(request.getRequestURI()).getPath())
                .withTimestamp(ZonedDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetails);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    protected ResponseEntity<ProblemDetails> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException ex, HttpServletRequest request) {
        logger.error("AccessDeniedException occurred: URI={}, Message={}", request.getRequestURI(), ex.getMessage(), ex);
        ProblemDetails problemDetails = ProblemDetails.builder()
                .withTitle("Access Denied")
                .withStatus(HttpStatus.FORBIDDEN.value())
                .withDetail("You do not have permission to access this resource.")
                .withInstance(URI.create(request.getRequestURI()).getPath())
                .withTimestamp(ZonedDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problemDetails);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        // Create your ProblemDetails here
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        ProblemDetails problemDetails = ProblemDetails.builder()
                .withTitle("Validation Error")
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withDetail(errors.toString())
                .withInstance(URI.create(request.getContextPath()).getPath())
                .withTimestamp(ZonedDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetails);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetails> handleAllExceptions(Exception ex, HttpServletRequest request) {
        logger.error("Exception occurred: URI={}, Message={}", request.getRequestURI(), ex.getMessage(), ex);
        ProblemDetails problemDetails = ProblemDetails.builder()
                .withTitle("Internal Server Error")
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .withDetail(ex.getMessage())
                .withInstance(URI.create(request.getRequestURI()).getPath())
                .withTimestamp(ZonedDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetails);
    }
}
