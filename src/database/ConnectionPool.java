package database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/** Lazily initialized PostgreSQL connection pool. */
public final class ConnectionPool {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionPool.class);
    private static volatile HikariDataSource dataSource;

    private ConnectionPool() { }

    public static Connection getConnection() throws SQLException {
        try {
            return dataSource().getConnection();
        } catch (IllegalStateException exception) {
            throw new SQLException("PostgreSQL connection configuration is unavailable.", exception);
        }
    }

    public static void shutdown() {
        HikariDataSource pool = dataSource;
        if (pool != null) {
            pool.close();
            dataSource = null;
        }
    }

    private static HikariDataSource dataSource() {
        HikariDataSource current = dataSource;
        if (current == null) {
            synchronized (ConnectionPool.class) {
                current = dataSource;
                if (current == null) {
                    current = createDataSource(DatabaseConfig.fromEnvironment());
                    dataSource = current;
                }
            }
        }
        return current;
    }

    private static HikariDataSource createDataSource(DatabaseConfig database) {
        HikariConfig config = new HikariConfig();
        config.setPoolName("HospitalPostgreSQLPool");
        config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(0);
        config.setConnectionTimeout(10_000);
        config.setValidationTimeout(5_000);
        config.setInitializationFailTimeout(-1);
        Properties properties = new Properties();
        properties.setProperty("serverName", database.host());
        properties.setProperty("portNumber", Integer.toString(database.port()));
        properties.setProperty("databaseName", database.database());
        properties.setProperty("user", database.username());
        properties.setProperty("password", database.password());
        properties.setProperty("sslMode", database.sslMode());
        properties.setProperty("tcpKeepAlive", "true");
        config.setDataSourceProperties(properties);
        try {
            return new HikariDataSource(config);
        } catch (RuntimeException exception) {
            LOGGER.error("Unable to initialize the PostgreSQL connection pool.", exception);
            throw new IllegalStateException("PostgreSQL connection pool initialization failed.", exception);
        }
    }
}
