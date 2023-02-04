package fr.ward.weconomy.api;

import fr.ward.weconomy.WEconomy;
import fr.ward.weconomy.cache.WPlayerCache;
import fr.ward.weconomy.discord.DiscordMessage;
import fr.ward.weconomy.manager.EconomyManager;
import fr.ward.weconomy.manager.MessageManager;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class WEconomyAPI {

    protected WEconomyAPI() {}

    public WPlayerCache getPlayerCache(UUID uuid) {
        return WEconomy.getInstance().getCacheManager().getPlayerCache(uuid);
    }

    public void updatePlayerData(UUID uuid) {
        WEconomy.getInstance().getCacheManager().updatePlayerData(uuid);
    }

    public void pay(Player paying, Player receiving, double amount) {
        final EconomyManager economyManager = WEconomy.getInstance().getEconomy();

        if(amount > economyManager.getBalance(paying.getUniqueId().toString())) {
            paying.sendMessage(MessageManager.INSUFFICIENT_FUNDS.build(paying));
            return;
        }

        if(receiving == paying) {
            paying.sendMessage(MessageManager.PAY_YOURSELF.build(paying));
            return;
        }

        if(receiving == null){
            paying.sendMessage(MessageManager.PLAYER_NOT_FOUND.build(paying));
        } else {
            WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.TRANSACTION, paying.getName(), receiving.getName(), amount);
            economyManager.withdrawPlayer(paying.getUniqueId().toString(), amount);
            economyManager.depositPlayer(receiving.getUniqueId().toString(), amount);
            paying.sendMessage(MessageManager.SEND_PAY.build(paying, receiving, amount));
            receiving.sendMessage(MessageManager.RECEIVER_PAY.build(paying, receiving, amount));
        }
    }

    public void addingMoney(UUID uuid, double amount) {
        final EconomyManager economyManager = WEconomy.getInstance().getEconomy();
        WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.GIVE, "WEconomyAPI", uuid.toString(), amount);
        economyManager.depositPlayer(uuid.toString(), amount);
    }

    public void removeMoney(UUID uuid, double amount) {
        final EconomyManager economyManager = WEconomy.getInstance().getEconomy();
        WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.REMOVE, "WEconomyAPI", uuid.toString(), amount);
        economyManager.withdrawPlayer(uuid.toString(), amount);
    }
}
