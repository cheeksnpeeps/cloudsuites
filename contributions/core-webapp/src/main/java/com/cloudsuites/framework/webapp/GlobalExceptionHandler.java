package com.cloudsuites.framework.webapp;

import com.cloudsuites.framework.services.common.exception.CustomException;
import com.cloudsuites.framework.services.common.exception.InvalidOperationException;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.common.exception.ProblemDetails;
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
import java.util.List;
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

    @ExceptionHandler(InvalidOperationException.class)
    protected ResponseEntity<ProblemDetails> handleNotFoundResponseException(InvalidOperationException ex, HttpServletRequest request) {
        logger.error("InvalidOperationException occurred: URI={}, Message={}", request.getRequestURI(), ex.getMessage(), ex);
        ProblemDetails problemDetails = ProblemDetails.builder()
                .withTitle("Invalid Operation")
                .withStatus(HttpStatus.NOT_FOUND.value())
                .withDetail(ex.getMessage())
                .withInstance(URI.create(request.getRequestURI()).getPath())
                .withTimestamp(ZonedDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetails);
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

    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
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
