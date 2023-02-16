package fr.ward.weconomy;

import fr.ward.weconomy.command.WEconomyCommand;
import fr.ward.weconomy.config.ConfigType;
import fr.ward.weconomy.listeners.DepositListener;
import fr.ward.weconomy.listeners.PlayerJoinLeaveListener;
import fr.ward.weconomy.manager.*;
import fr.ward.weconomy.placeholder.SomeExpansion;
import fr.ward.weconomy.utils.Metrics;
import fr.ward.weconomy.utils.MineLogger;
import fr.ward.weconomy.utils.MineUpdater;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class WEconomy extends JavaPlugin {

    private static WEconomy INSTANCE;

    private EconomyManager economyManager;
    private CacheManager cacheManager;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private DiscordManager discordManager;

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
        this.economyManager = new EconomyManager();
        economyManager.load();
        super.onLoad();
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        checker();

        getConfig().options().copyDefaults(true);
        saveConfig();

        this.cacheManager = new CacheManager();
        this.configManager = new ConfigManager(this);
        this.databaseManager = new DatabaseManager();
        this.discordManager = new DiscordManager();

        configManager.load();
        databaseManager.load();
        discordManager.load();

        setupPlaceHolder();

        MessageManager.build(ConfigType.MESSAGE.getGeneratedYML().getConfig());
        MessageListManager.build(ConfigType.MESSAGE.getGeneratedYML().getConfig());

        final PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinLeaveListener(), this);
        pluginManager.registerEvents(new DepositListener(), this);

        Objects.requireNonNull(this.getCommand("economy")).setExecutor(new WEconomyCommand());

        loadBStats();

        super.onEnable();
    }

    public void reload() {
        this.reloadConfig();
        getConfigManager().reloadConfig();
        MessageManager.build(ConfigType.MESSAGE.getGeneratedYML().getConfig());
        MessageListManager.build(ConfigType.MESSAGE.getGeneratedYML().getConfig());
        for(Player plz : Bukkit.getOnlinePlayers()) {
            getCacheManager().updatePlayerData(plz.getUniqueId());
            getCacheManager().addPlayerCache(plz);
        }
    }

    private void setupPlaceHolder() {
        if (getServer().getPluginManager().getPlugin("PlaceHolderAPI") != null) {
            new SomeExpansion().register();
        }
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public DiscordManager getDiscordManager() {
        return discordManager;
    }

    private void loadBStats() {
        new Metrics(this, 17624);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private void checker() {
        final String version = this.getDescription().getVersion();
        getConfig().set("version", version);
        if(getConfig().getBoolean("check-update")) return;
        new MineUpdater(this, 107785).getVersion(ver -> {
            if (version.equals(ver)) {
                MineLogger.info("There is not a new update available.");
            } else {
                MineLogger.warning("There is a new update available.");
                MineLogger.warning("https://www.spigotmc.org/resources/✅-weconomy-the-economy-easy-solution-1-16-5.107785/");
            }
        });
    }
}
