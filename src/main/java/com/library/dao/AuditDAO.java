package com.library.dao;

import com.library.model.AuditLog;

import java.util.List;

public interface AuditDAO {
    int addAuditLog(AuditLog log);
    List<AuditLog> findRecentLogs(int limit);
}
