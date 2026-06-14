package com.library.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an audit log entry for tracking system events.
 */
public class AuditLog {
    private int auditId;
    private String eventType;
    private String description;
    private LocalDateTime eventTime;

    public AuditLog() {
    }

    public AuditLog(int auditId, String eventType, String description, LocalDateTime eventTime) {
        this.auditId = auditId;
        this.eventType = eventType;
        this.description = description;
        this.eventTime = eventTime;
    }

    public AuditLog(String eventType, String description, LocalDateTime eventTime) {
        this(0, eventType, description, eventTime);
    }

    public int getAuditId() {
        return auditId;
    }

    public void setAuditId(int auditId) {
        this.auditId = auditId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuditLog)) return false;
        AuditLog auditLog = (AuditLog) o;
        return auditId == auditLog.auditId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(auditId);
    }

    @Override
    public String toString() {
        return String.format("AuditLog{id=%d, type='%s', description='%s', time=%s}", auditId, eventType, description, eventTime);
    }
}
