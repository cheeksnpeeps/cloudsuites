package com.cloudsuites.framework.services.common.exception;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

public class ProblemDetailsBuilder {

    private String title;
    private HttpStatus status;
    private String detail;
    private String instance;
    private ZonedDateTime timestamp;

    public ProblemDetailsBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public ProblemDetailsBuilder withStatus(HttpStatus status) {
        this.status = status;
        return this;
    }

    public ProblemDetailsBuilder withDetail(String detail) {
        this.detail = detail;
        return this;
    }

    public ProblemDetailsBuilder withInstance(String instance) {
        this.instance = instance;
        return this;
    }

    public ProblemDetailsBuilder withTimestamp(Object timestamp) {
        if (timestamp instanceof ZonedDateTime) {
            this.timestamp = (ZonedDateTime) timestamp;
        }
        return this;
    }

    public ProblemDetails build() {
        return new ProblemDetails(title, status, detail, instance, timestamp);
    }
}

