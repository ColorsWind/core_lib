package io.github.divios.core_lib.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.SQLException;

public class SQLiteConnector implements DatabaseConnector {

    private final Plugin plugin;
    private final String connectionString;
    private HikariDataSource hikariConnection;

    public SQLiteConnector(Plugin plugin) {
        this.plugin = plugin;
        this.connectionString = "jdbc:sqlite:" + plugin.getDataFolder() + File.separator + plugin.getDescription().getName().toLowerCase() + ".db";

        try {
            Class.forName("org.sqlite.JDBC"); // This is required to put here for Spigot 1.10 and below to force class load
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isInitialized() {
        return true; // Always available
    }

    @Override
    public void closeConnection() {
        if (this.hikariConnection != null) {
            this.hikariConnection.close();
        }
    }

    @Override
    public void connect(ConnectionCallback callback) {
        if (this.hikariConnection == null) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(connectionString);
            config.addDataSourceProperty("autoReconnect", "true");
            config.addDataSourceProperty("leakDetectionThreshold", "true");
            config.addDataSourceProperty("verifyServerCertificate", "false");
            config.addDataSourceProperty("useSSL", "false");
            config.setConnectionTimeout(5000);
            hikariConnection = new HikariDataSource(config);
        }

        try {
            callback.accept(this.hikariConnection.getConnection());
        } catch (Exception ex) {
            this.plugin.getLogger().severe("An error occurred executing an SQLite query: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}