package fr.ward.weconomy.manager;

import fr.ward.weconomy.WEconomy;
import fr.ward.weconomy.economy.EconomyRegister;
import fr.ward.weconomy.utils.MineLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

public class EconomyManager {

    private EconomyRegister economyRegister;

    public void load() {
        setupEconomy();
    }

    public EconomyRegister getEconomy() {
        return economyRegister;
    }

    private void setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            MineLogger.error("EconomyRegister could not be registered... Vault is missing!");
            Bukkit.getServer().getPluginManager().disablePlugin(WEconomy.getInstance());
        }
        Bukkit.getServer().getServicesManager().register(EconomyRegister.class, this.economyRegister, WEconomy.getInstance(), ServicePriority.High);
        MineLogger.info("EconomyRegister has ben registered!");
    }
}
