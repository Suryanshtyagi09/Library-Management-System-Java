package com.library.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Provides a connection to the configured database.
 */
public final class DBConnection {
    private static final Logger logger = LogManager.getLogger(DBConnection.class);
    private static final String PROPERTIES_FILE = "/db.properties";
    private static final String SCHEMA_RESOURCE = "/schema/library.sql";
    private static final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final Properties properties = new Properties();
    private static final boolean INITIALIZE_SCHEMA;

    static {
        try (InputStream stream = DBConnection.class.getResourceAsStream(PROPERTIES_FILE)) {
            if (stream == null) {
                throw new IllegalStateException("Database configuration file not found: " + PROPERTIES_FILE);
            }
            properties.load(stream);
            String driver = System.getProperty("db.driver", properties.getProperty("db.driver", DEFAULT_DRIVER));
            Class.forName(driver);
            INITIALIZE_SCHEMA = Boolean.parseBoolean(System.getProperty("db.initialize", properties.getProperty("db.initialize", "false")));
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Unable to initialize DB connection", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String url = System.getProperty("db.url", properties.getProperty("db.url"));
        String username = System.getProperty("db.username", properties.getProperty("db.username"));
        String password = System.getProperty("db.password", properties.getProperty("db.password"));
        logger.debug("Creating database connection for URL {}", url);
        Connection connection = DriverManager.getConnection(url, username, password);
        if (INITIALIZE_SCHEMA) {
            initializeSchemaIfRequired(connection);
        }
        return connection;
    }

    private static void initializeSchemaIfRequired(Connection connection) {
        try {
            if (!isSchemaPresent(connection)) {
                logger.info("Initializing database schema from {}", SCHEMA_RESOURCE);
                runSqlScript(connection);
            }
        } catch (IOException | SQLException e) {
            logger.error("Failed to initialize database schema", e);
            throw new RuntimeException(e);
        }
    }

    private static boolean isSchemaPresent(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        return tableExists(metaData, "BOOKS") || tableExists(metaData, "books");
    }

    private static boolean tableExists(DatabaseMetaData metaData, String tableName) throws SQLException {
        try (ResultSet tables = metaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
            return tables.next();
        }
    }

    private static void runSqlScript(Connection connection) throws IOException, SQLException {
        try (InputStream stream = DBConnection.class.getResourceAsStream(SCHEMA_RESOURCE)) {
            if (stream == null) {
                throw new IllegalStateException("Schema resource not found: " + SCHEMA_RESOURCE);
            }
            String script = readResource(stream);
            for (String statement : splitStatements(script)) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(statement);
                }
            }
        }
    }

    private static String readResource(InputStream stream) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                builder.append(line).append(' ');
            }
        }
        return builder.toString();
    }

    private static List<String> splitStatements(String script) {
        List<String> statements = new ArrayList<>();
        for (String rawStatement : script.split(";")) {
            String trimmed = rawStatement.trim();
            if (!trimmed.isEmpty()) {
                statements.add(trimmed);
            }
        }
        return statements;
    }
}
