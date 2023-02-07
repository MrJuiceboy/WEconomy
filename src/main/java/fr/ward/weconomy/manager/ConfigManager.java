package fr.ward.weconomy.manager;

import fr.ward.weconomy.WEconomy;
import fr.ward.weconomy.config.ConfigType;
import fr.ward.weconomy.utils.GeneratedYML;
import fr.ward.weconomy.utils.MineLogger;

import java.util.ArrayList;

public class ConfigManager {

    private final ArrayList<GeneratedYML> configs = new ArrayList<>();

    private final WEconomy wEconomy;

    public ConfigManager(WEconomy wEconomy) {
        this.wEconomy = wEconomy;
    }

    public void load() {
        wEconomy.saveDefaultConfig();
        wEconomy.getConfig().options().copyDefaults(true);

        for(ConfigType configType : ConfigType.values()) {
            check(configType);
        }
    }

    public void reloadConfig() {
        for (GeneratedYML generatedYMLs : this.configs) {
            generatedYMLs.loadConfiguration();
        }
    }

    private void check(ConfigType configType) {
        if(!configType.getGeneratedYML().exists()) {
            MineLogger.warning(configType.getGeneratedYML().getFile().getName() + " not found, creating " + configType.getGeneratedYML().getFile().getName() + "...");
            this.wEconomy.saveResource(configType.getGeneratedYML().getFile().getName(), false);
        }
        configType.getGeneratedYML().loadConfiguration();
        this.configs.add(configType.getGeneratedYML());
    }
}
