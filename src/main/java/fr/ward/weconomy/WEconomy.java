package fr.ward.weconomy;

import fr.ward.weconomy.command.WEconomyCommand;
import fr.ward.weconomy.database.DatabaseType;
import fr.ward.weconomy.database.type.Database;
import fr.ward.weconomy.database.type.MySQL;
import fr.ward.weconomy.database.type.SQLite;
import fr.ward.weconomy.listeners.PlayerJoinLeaveListener;
import fr.ward.weconomy.manager.CacheManager;
import fr.ward.weconomy.manager.DiscordManager;
import fr.ward.weconomy.manager.EconomyManager;
import fr.ward.weconomy.placeholder.SomeExpansion;
import fr.ward.weconomy.utils.Metrics;
import fr.ward.weconomy.utils.MineLogger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WEconomy extends JavaPlugin {

    private static WEconomy INSTANCE;

    private final String prefix = getConfig().getString("prefixName");
    private final String money = getConfig().getString("moneyName");

    private EconomyManager economyManager;
    private CacheManager cacheManager;
    private DiscordManager discordManager;
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
        this.economyManager = new EconomyManager();
        this.discordManager = new DiscordManager();

        setupEconomy();

        setupDatabase();

        setupPlaceHolder();

        setupDiscordBot();

        final PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinLeaveListener(), this);

        Objects.requireNonNull(this.getCommand("economy")).setExecutor(new WEconomyCommand());

        loadBStats();

        super.onEnable();
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            MineLogger.error("Economy could not be registered... Vault is missing!");
            getServer().getPluginManager().disablePlugin(this);
        }
        getServer().getServicesManager().register(Economy.class, this.economyManager, this, ServicePriority.High);
        MineLogger.info("Economy has ben registered!");
    }

    private void setupDatabase() {
        try {
            initDatabaseType(getDatabaseType());
        } catch (SQLException e) {
            MineLogger.error("" + e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void setupPlaceHolder() {
        if (getServer().getPluginManager().getPlugin("PlaceHolderAPI") != null) {
            new SomeExpansion().register();
        }
    }

    private void setupDiscordBot() {
        this.discordManager.load();
    }

    public String getPrefix() {
        if(prefix != null) {
            return ChatColor.translateAlternateColorCodes('&', prefix);
        }
        return "§7[§6WEconomy§7]";
    }

    public String getMoney() {
        if(money != null) {
            return ChatColor.translateAlternateColorCodes('&', money);
        }
        return "$";
    }

    public EconomyManager getEconomy() {
        return economyManager;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public DiscordManager getDiscordManager() {
        return discordManager;
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

    private void loadBStats() {
        final Metrics metrics = new Metrics(this, 17624);

        metrics.addCustomChart(new Metrics.SingleLineChart("players", () -> Bukkit.getOnlinePlayers().size()));
        metrics.addCustomChart(new Metrics.SingleLineChart("servers", () -> 1));

        metrics.addCustomChart(new Metrics.SingleLineChart("players_and_servers_using_weconomy", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            valueMap.put("servers", 1);
            valueMap.put("players", Bukkit.getOnlinePlayers().size());
            return valueMap.size();
        }));
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
