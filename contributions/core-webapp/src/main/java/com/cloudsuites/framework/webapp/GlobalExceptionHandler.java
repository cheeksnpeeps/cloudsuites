package com.cloudsuites.framework.webapp;

import com.cloudsuites.framework.services.common.exception.CustomException;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.common.exception.ProblemDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.ZonedDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ProblemDetails> handleCustomException(CustomException ex, HttpServletRequest request) {
        ProblemDetails problemDetails = ProblemDetails.builder()
                .withTitle("Bad Request")
                .withStatus(HttpStatus.BAD_REQUEST)
                .withDetail(ex.getMessage())
                .withInstance(URI.create(request.getRequestURI()).getPath())
                .withTimestamp(ZonedDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetails);
    }
    @ExceptionHandler(NotFoundResponseException.class)
    protected ResponseEntity<Object> handleNotFoundResponseException(NotFoundResponseException ex) {
        ProblemDetails problemDetails = ProblemDetails.builder()
                .withTitle("Not Found")
                .withStatus(HttpStatus.NOT_FOUND)
                .withDetail(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetails);
    }

    // Default exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetails> handleAllExceptions(Exception ex, HttpServletRequest request) {
        ProblemDetails problemDetails = ProblemDetails.builder()
                .withTitle("Internal Server Error")
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .withDetail(ex.getMessage())
                .withInstance(URI.create(request.getRequestURI()).getPath())
                .withTimestamp(ZonedDateTime.now())
                .build();

        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetails);
    }


}
