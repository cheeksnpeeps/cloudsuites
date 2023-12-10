package com.cloudsuites.framework.services.common.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Data
public class ProblemDetails {

    private final String title;
    private final HttpStatus status;
    private final String detail;
    private final String instance;
    private final ZonedDateTime timestamp;

    public ProblemDetails(String title, HttpStatus status, String detail, String instance, ZonedDateTime timestamp) {
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
        this.timestamp = timestamp;
    }

    public static ProblemDetailsBuilder builder() {
        return new ProblemDetailsBuilder();
    }

}

