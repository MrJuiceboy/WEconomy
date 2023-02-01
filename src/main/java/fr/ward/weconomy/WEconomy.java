package fr.ward.weconomy;

import fr.ward.weconomy.command.WEconomyCommand;
import fr.ward.weconomy.database.DatabaseType;
import fr.ward.weconomy.database.type.Database;
import fr.ward.weconomy.database.type.MySQL;
import fr.ward.weconomy.database.type.SQLite;
import fr.ward.weconomy.listeners.PlayerJoinLeaveListener;
import fr.ward.weconomy.manager.CacheManager;
import fr.ward.weconomy.manager.EconomyManager;
import fr.ward.weconomy.utils.MineLogger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;

public class WEconomy extends JavaPlugin {

    private static WEconomy INSTANCE;

    private EconomyManager economyManager;
    private CacheManager cacheManager;
    private Database database;

    public WEconomy() {
        INSTANCE = this;
    }

    /**
     * Get the actual WEconomy instance
     *
     * @return {@link WEconomy} the actual instance
     */
    public static WEconomy getInstance() {
        return INSTANCE;
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        getConfig().options().copyDefaults(true);
        saveConfig();

        this.cacheManager = new CacheManager();

        if(!setupEconomy()){
            MineLogger.error("Economy could not be registered... Vault is missing!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            initDatabaseType(getDatabaseType());
        } catch (SQLException e) {
            MineLogger.error("" + e);
        }

        final PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinLeaveListener(), this);

        Objects.requireNonNull(this.getCommand("economy")).setExecutor(new WEconomyCommand());

        super.onEnable();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        getServer().getServicesManager().register(Economy.class, economyManager, this, ServicePriority.High);
        MineLogger.info("Economy has ben registered!");
        return true;
    }

    public EconomyManager getEconomy() {
        return economyManager;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public Database getDatabase() {
        return database;
    }

    private void initDatabaseType(DatabaseType databaseType) throws SQLException {
        switch (databaseType) {
            case SQLITE -> {
                this.database = new SQLite(this);
                this.database.load();
            }

            case MYSQL -> {
                this.database = new MySQL(this);
                this.database.load();
            }
        }
    }

    public DatabaseType getDatabaseType() {
        final FileConfiguration fileConfiguration = WEconomy.getInstance().getConfig();
        final String choice = fileConfiguration.getString("databaseType");

        if("MYSQL".equals(choice)) {
            return DatabaseType.MYSQL;
        }
        return DatabaseType.SQLITE;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
