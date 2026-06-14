package com.library.dao.impl;

import com.library.dao.AuditDAO;
import com.library.model.AuditLog;
import com.library.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuditDAOImpl implements AuditDAO {
    private static final String INSERT_AUDIT = "INSERT INTO audit_logs (event_type, description, event_time) VALUES (?, ?, ?)";
    private static final String SELECT_RECENT = "SELECT * FROM audit_logs ORDER BY event_time DESC LIMIT ?";

    @Override
    public int addAuditLog(AuditLog log) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_AUDIT, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, log.getEventType());
            statement.setString(2, log.getDescription());
            statement.setObject(3, log.getEventTime());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving audit log", e);
        }
        return 0;
    }

    @Override
    public List<AuditLog> findRecentLogs(int limit) {
        List<AuditLog> logs = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_RECENT)) {
            statement.setInt(1, limit);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    AuditLog log = new AuditLog();
                    log.setAuditId(resultSet.getInt("audit_id"));
                    log.setEventType(resultSet.getString("event_type"));
                    log.setDescription(resultSet.getString("description"));
                    log.setEventTime(resultSet.getTimestamp("event_time").toLocalDateTime());
                    logs.add(log);
                }
            }
            return logs;
        } catch (SQLException e) {
            throw new RuntimeException("Error reading audit logs", e);
        }
    }
}
