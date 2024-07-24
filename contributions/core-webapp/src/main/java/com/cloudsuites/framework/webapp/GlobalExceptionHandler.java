package com.cloudsuites.framework.webapp;

import com.cloudsuites.framework.services.common.exception.CustomException;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.common.exception.ProblemDetails;
import com.cloudsuites.framework.services.common.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.ZonedDateTime;

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

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ProblemDetails> handleValidationException(ValidationException ex, HttpServletRequest request) {
        logger.error("ValidationException occurred: URI={}, Message={}", request.getRequestURI(), ex.getMessage(), ex);
        ProblemDetails problemDetails = ProblemDetails.builder()
                .withTitle("Validation Error")
                .withStatus(HttpStatus.BAD_REQUEST.value())
                .withDetail(ex.getMessage())
                .withInstance(URI.create(request.getRequestURI()).getPath())
                .withTimestamp(ZonedDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetails);
    }
}
