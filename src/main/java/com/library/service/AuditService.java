package com.library.service;

import com.library.dao.AuditDAO;
import com.library.dao.impl.AuditDAOImpl;
import com.library.model.AuditLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Writes audit events to persistent storage.
 */
public class AuditService {
    private final AuditDAO auditDAO;

    public AuditService() {
        this.auditDAO = new AuditDAOImpl();
    }

    public void logEvent(String eventType, String description) {
        AuditLog log = new AuditLog(eventType, description, LocalDateTime.now());
        auditDAO.addAuditLog(log);
    }

    public List<AuditLog> getRecentLogs(int limit) {
        return auditDAO.findRecentLogs(limit);
    }
}
