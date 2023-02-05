package fr.ward.weconomy.api;

import fr.ward.weconomy.WEconomy;
import fr.ward.weconomy.cache.WPlayerCache;
import fr.ward.weconomy.discord.DiscordMessage;
import fr.ward.weconomy.manager.EconomyManager;
import fr.ward.weconomy.manager.MessageManager;
import fr.ward.weconomy.utils.MineLogger;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.UUID;

public final class WEconomyAPI {

    private static WEconomy wEconomy;

    @Deprecated
    public static void test(String str) {
        MineLogger.info(str);
    }

    @Nullable
    @Deprecated
    public static WPlayerCache getPlayerCache(UUID uuid) {
        return wEconomy.getCacheManager().getPlayerCache(uuid);
    }

    @Deprecated
    public static void updatePlayerData(UUID uuid) {
        wEconomy.getCacheManager().updatePlayerData(uuid);
    }

    @Deprecated
    public static void pay(Player paying, Player receiving, double amount) {
        final EconomyManager economyManager = wEconomy.getEconomy();

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
            wEconomy.getDiscordManager().sendMessage(DiscordMessage.TRANSACTION, paying.getName(), receiving.getName(), amount);
            economyManager.withdrawPlayer(paying.getUniqueId().toString(), amount);
            economyManager.depositPlayer(receiving.getUniqueId().toString(), amount);
            paying.sendMessage(MessageManager.SEND_PAY.build(paying, receiving, amount));
            receiving.sendMessage(MessageManager.RECEIVER_PAY.build(paying, receiving, amount));
        }
    }

    @Deprecated
    public static void addingMoney(UUID uuid, double amount) {
        final EconomyManager economyManager = wEconomy.getEconomy();
        wEconomy.getDiscordManager().sendMessage(DiscordMessage.GIVE, "WEconomyAPI", uuid.toString(), amount);
        economyManager.depositPlayer(uuid.toString(), amount);
    }

    @Deprecated
    public static void removeMoney(UUID uuid, double amount) {
        final EconomyManager economyManager = wEconomy.getEconomy();
        wEconomy.getDiscordManager().sendMessage(DiscordMessage.REMOVE, "WEconomyAPI", uuid.toString(), amount);
        economyManager.withdrawPlayer(uuid.toString(), amount);
    }
}
