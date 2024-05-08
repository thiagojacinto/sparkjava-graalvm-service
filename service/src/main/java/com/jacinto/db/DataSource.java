package com.jacinto.db;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSource {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;
    public static final String USER = "admin";
    public static final String PASSWORD = "1313";
    public static final String CONNECTION_URL = "jdbc:postgresql://" + getDbHostname() + ":5432/rinha";

    static {
        config.setJdbcUrl(CONNECTION_URL);
        config.setUsername(USER);
        config.setPassword(PASSWORD);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
    }

    private DataSource() {
    }

    public static String getDbHostname() {
        String dbHostname;
        try {
            dbHostname = System.getenv("DB_HOSTNAME");
        } catch (Exception e) {
            dbHostname = "localhost";
        }
        return dbHostname != null ? dbHostname : "localhost";
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
