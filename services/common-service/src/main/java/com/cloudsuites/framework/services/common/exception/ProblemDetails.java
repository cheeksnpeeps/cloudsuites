package com.cloudsuites.framework.services.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemDetails {

    @JsonProperty("title")
    private String title;

    @JsonProperty("status")
    private int status;

    @JsonProperty("detail")
    private String detail;

    @JsonProperty("instance")
    private String instance;

    @JsonProperty("timestamp")
    private ZonedDateTime timestamp;

    // Getters and setters

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ProblemDetails problemDetails;

        private Builder() {
            this.problemDetails = new ProblemDetails();
        }

        public Builder withTitle(String title) {
            this.problemDetails.title = title;
            return this;
        }

        public Builder withStatus(int status) {
            this.problemDetails.status = status;
            return this;
        }

        public Builder withDetail(String detail) {
            this.problemDetails.detail = detail;
            return this;
        }

        public Builder withInstance(String instance) {
            this.problemDetails.instance = instance;
            return this;
        }

        public Builder withTimestamp(ZonedDateTime timestamp) {
            this.problemDetails.timestamp = timestamp;
            return this;
        }

        public ProblemDetails build() {
            return this.problemDetails;
        }
    }
}
