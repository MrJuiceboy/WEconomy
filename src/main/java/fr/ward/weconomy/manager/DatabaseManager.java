package fr.ward.weconomy.manager;

import fr.ward.weconomy.WEconomy;
import fr.ward.weconomy.config.ConfigType;
import fr.ward.weconomy.database.DatabaseType;
import fr.ward.weconomy.database.type.Database;
import fr.ward.weconomy.database.type.MySQL;
import fr.ward.weconomy.database.type.SQLite;
import fr.ward.weconomy.utils.MineLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.SQLException;

public class DatabaseManager {

    private Database database;

    public void load() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(WEconomy.getInstance(), this::setupDatabase, 40L);
    }

    public Database getDatabase() {
        return database;
    }

    public DatabaseType getDatabaseType() {
        final FileConfiguration fileConfiguration = ConfigType.DATABASE.getGeneratedYML().getConfig();
        final String choice = fileConfiguration.getString("database.Type");

        if("MySQL".equalsIgnoreCase(choice)) {
            return DatabaseType.MYSQL;
        }
        return DatabaseType.SQLITE;
    }

    private void setupDatabase() {
        try {
            initDatabaseType(getDatabaseType());
        } catch (SQLException e) {
            MineLogger.error("" + e);
            Bukkit.getServer().getPluginManager().disablePlugin(WEconomy.getInstance());
        }
    }

    private void initDatabaseType(DatabaseType databaseType) throws SQLException {
        switch (databaseType) {
            case SQLITE -> {
                this.database = new SQLite();
                this.database.load();
            }

            case MYSQL -> {
                this.database = new MySQL();
                this.database.load();
            }
        }
    }
}
