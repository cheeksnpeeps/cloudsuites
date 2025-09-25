package com.cloudsuites.framework.services.auth.entities;

/**
 * Response for audit event operations.
 * Simple response containing the created event information.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
public class AuditEventResponse {

    /**
     * The created audit event.
     */
    private AuditEvent auditEvent;

    /**
     * Success indicator.
     */
    private boolean success;

    /**
     * Response message.
     */
    private String message;

    // Constructors
    public AuditEventResponse() {}

    public AuditEventResponse(AuditEvent auditEvent, boolean success) {
        this.auditEvent = auditEvent;
        this.success = success;
    }

    // Static factory methods
    public static AuditEventResponse success(AuditEvent auditEvent) {
        return new AuditEventResponse(auditEvent, true);
    }

    public static AuditEventResponse failure(String message) {
        AuditEventResponse response = new AuditEventResponse();
        response.success = false;
        response.message = message;
        return response;
    }

    // Getters and Setters
    public AuditEvent getAuditEvent() { return auditEvent; }
    public void setAuditEvent(AuditEvent auditEvent) { this.auditEvent = auditEvent; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
