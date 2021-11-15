package io.github.divios.core_lib.database;

import io.github.divios.core_lib.Core_lib;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DataManagerAbstract {

    protected final DatabaseConnector databaseConnector;
    protected final Plugin plugin;

    private static final Map<String, LinkedList<Runnable>> queues = new HashMap<>();

    public DataManagerAbstract(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
        this.plugin = Core_lib.PLUGIN;
    }

    /**
     * @return the prefix to be used by all table names
     */
    public String getTablePrefix() {
        return this.plugin.getDescription().getName().toLowerCase() + '_';
    }

    /**
     * Deprecated because it is often times not accurate to its use case.
     */
    @Deprecated
    protected int lastInsertedId(Connection connection) {
        return lastInsertedId(connection, null);
    }

    protected int lastInsertedId(Connection connection, String table) {
        String select = "SELECT * FROM " + this.getTablePrefix() + table + " ORDER BY id DESC LIMIT 1";
        String query;
        if (this.databaseConnector instanceof SQLiteConnector) {
            query = table == null ? "SELECT last_insert_rowid()" : select;
        } else {
            query = table == null ? "SELECT LAST_INSERT_ID()" : select;
        }

        int id = -1;
        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            result.next();
            id = result.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * Queue a task to be run synchronously.
     *
     * @param runnable task to run on the next server tick
     */
    public void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(this.plugin, runnable);
    }

    /**
     * Queue a task to be run asynchronously with all the
     * advantages of CompletableFuture api <br>
     *
     * @param runnable task to run
     */
    public CompletableFuture<Void> async(Runnable runnable) {
        return supplyAsync(() -> {
            runnable.run();
            return null;
        });
    }

    /**
     * Supplies a task to be run asynchronously with all the
     * advantages of CompletableFuture api <br>
     *
     * @param supplier supplier to run
     */

    public <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier);
    }

    private void doQueue(String queueKey, Consumer<Boolean> callback) {
        if (queues.get(queueKey) == null) return;
        Runnable runnable = queues.get(queueKey).getFirst();
        async(() -> {
            runnable.run();
            sync(() -> {
                queues.get(queueKey).remove(runnable);
                callback.accept(true);
            });
        });
    }
}