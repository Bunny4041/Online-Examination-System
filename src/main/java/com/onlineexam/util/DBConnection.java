package com.onlineexam.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Hands out JDBC {@link Connection}s using credentials read ONCE from
 * {@code db.properties} on the classpath (packaged from src/main/resources into
 * WEB-INF/classes). Credentials are never hard-coded in Java source.
 *
 * <p>For a first project a fresh connection per request (closed with
 * try-with-resources by the caller) is perfectly fine. Connection pooling via a
 * Tomcat JNDI {@code DataSource} is noted as a future enhancement.</p>
 */
public final class DBConnection {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties props = new Properties();
        try (InputStream in = DBConnection.class.getResourceAsStream("/db.properties")) {
            if (in == null) {
                throw new IllegalStateException(
                        "db.properties not found on the classpath. "
                        + "Create src/main/resources/db.properties (copy db.properties.example).");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load db.properties", e);
        }

        URL = props.getProperty("db.url");
        USER = props.getProperty("db.user");
        PASSWORD = props.getProperty("db.password");

        if (URL == null || USER == null) {
            throw new IllegalStateException("db.properties is missing db.url or db.user");
        }

        // Modern MySQL Connector/J (JDBC 4+) auto-registers through the service
        // loader, so Class.forName is not strictly required. We attempt it
        // defensively and ignore failure — the service loader still applies.
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {
            // no-op
        }
    }

    private DBConnection() {
        // utility class — no instances
    }

    /**
     * @return a new open connection to the configured database.
     * @throws SQLException if the connection cannot be established.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
