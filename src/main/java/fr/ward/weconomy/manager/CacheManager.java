package fr.ward.weconomy.manager;

import fr.ward.weconomy.WEconomy;
import fr.ward.weconomy.cache.WPlayerCache;
import fr.ward.weconomy.database.type.Database;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class CacheManager {

    private final ArrayList<WPlayerCache> wPlayerCaches = new ArrayList<>();

    public ArrayList<WPlayerCache> getPlayerCaches() {
        return wPlayerCaches;
    }

    public void addPlayerCache(Player player) {
        final UUID uuid = player.getUniqueId();
        final Database database = WEconomy.getInstance().getDatabaseManager().getDatabase();
        Bukkit.getScheduler().runTaskAsynchronously(WEconomy.getInstance(), () -> database.addIntoDatabase(player));
        final WPlayerCache wPlayerCache = new WPlayerCache(uuid, database.getMoney(uuid));
        WEconomy.getInstance().getCacheManager().getPlayerCaches().add(wPlayerCache);
    }

    public WPlayerCache getPlayerCache(UUID uuid){
        for(WPlayerCache playerCache : wPlayerCaches){
            if(playerCache.getUuid().equals(uuid)){
                return playerCache;
            }
        }
        return null;
    }

    public void updatePlayerData(UUID uuid) {
        final WPlayerCache wPlayerCache = getPlayerCache(uuid);
        final Database database = WEconomy.getInstance().getDatabaseManager().getDatabase();
        Bukkit.getScheduler().runTaskAsynchronously(WEconomy.getInstance(), () -> database.setMoney(uuid, wPlayerCache.getMoney()));
    }

    public float getBalance(OfflinePlayer offlinePlayer) {
        if(offlinePlayer.isOnline()) {
            final WPlayerCache wPlayerCache = getPlayerCache(offlinePlayer.getUniqueId());
            return (float) (Math.round(wPlayerCache.getMoney() * 100.0) / 100.0);
        } else if(offlinePlayer.hasPlayedBefore()) {
            final Database database = WEconomy.getInstance().getDatabaseManager().getDatabase();
            return (float) (Math.round(database.getMoney(offlinePlayer.getUniqueId()) * 100.0) / 100.0);
        } else {
            return 0;
        }
    }

    public float getBalance(UUID uuid) {
        final Player player = Bukkit.getPlayer(uuid);

        if(player != null && player.isOnline()) {
            final WPlayerCache wPlayerCache = getPlayerCache(uuid);
            return (float) (Math.round(wPlayerCache.getMoney() * 100.0) / 100.0);
        } else if(player != null && player.hasPlayedBefore()) {
            final Database database = WEconomy.getInstance().getDatabaseManager().getDatabase();
            return (float) (Math.round(database.getMoney(uuid) * 100.0) / 100.0);
        } else {
            return 0;
        }
    }

    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, float amount) {
        if(offlinePlayer.isOnline()) {
            final WPlayerCache wPlayerCache = getPlayerCache(offlinePlayer.getUniqueId());
            final float currentAmount = WEconomy.getInstance().getDatabaseManager().getDatabase().getMoney(offlinePlayer.getUniqueId());
            if(currentAmount >= amount) {
                wPlayerCache.setMoney(wPlayerCache.getMoney() - amount);
                updatePlayerData(offlinePlayer.getUniqueId());
                return new EconomyResponse(amount, currentAmount, EconomyResponse.ResponseType.SUCCESS, "");
            } else {
                return new EconomyResponse(amount, currentAmount, EconomyResponse.ResponseType.FAILURE, "");
            }
        } else if(offlinePlayer.hasPlayedBefore()) {
            final Database database = WEconomy.getInstance().getDatabaseManager().getDatabase();
            final float currentAmount = database.getMoney(offlinePlayer.getUniqueId());
            if(currentAmount >= amount) {
                database.setMoney(offlinePlayer.getUniqueId(), currentAmount - amount);
                return new EconomyResponse(amount, currentAmount, EconomyResponse.ResponseType.SUCCESS, "");
            } else {
                return new EconomyResponse(amount, currentAmount, EconomyResponse.ResponseType.FAILURE, "");
            }
        } else {
            return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "");
        }
    }

    public EconomyResponse withdrawPlayer(UUID uuid, float amount) {
        final Player player = Bukkit.getPlayer(uuid);

        if(player != null && player.isOnline()) {
            final WPlayerCache wPlayerCache = getPlayerCache(uuid);
            final float currentAmount = WEconomy.getInstance().getDatabaseManager().getDatabase().getMoney(uuid);
            if(currentAmount >= amount) {
                wPlayerCache.setMoney(wPlayerCache.getMoney() - amount);
                updatePlayerData(uuid);
                return new EconomyResponse(amount, currentAmount, EconomyResponse.ResponseType.SUCCESS, "");
            } else {
                return new EconomyResponse(amount, currentAmount, EconomyResponse.ResponseType.FAILURE, "");
            }
        } else if(player != null && player.hasPlayedBefore()) {
            final Database database = WEconomy.getInstance().getDatabaseManager().getDatabase();
            final float currentAmount = database.getMoney(uuid);
            if(currentAmount >= amount) {
                database.setMoney(uuid, currentAmount - amount);
                return new EconomyResponse(amount, currentAmount, EconomyResponse.ResponseType.SUCCESS, "");
            } else {
                return new EconomyResponse(amount, currentAmount, EconomyResponse.ResponseType.FAILURE, "");
            }
        } else {
            return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "");
        }
    }

    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, float amount) {
        if(offlinePlayer.isOnline()) {
            final WPlayerCache wPlayerCache = getPlayerCache(offlinePlayer.getUniqueId());
            final float currentAmount = WEconomy.getInstance().getDatabaseManager().getDatabase().getMoney(offlinePlayer.getUniqueId());
            wPlayerCache.setMoney(wPlayerCache.getMoney() + amount);
            updatePlayerData(offlinePlayer.getUniqueId());
            return new EconomyResponse(amount, currentAmount, EconomyResponse.ResponseType.SUCCESS, "");
        } else if(offlinePlayer.hasPlayedBefore()) {
            final Database database = WEconomy.getInstance().getDatabaseManager().getDatabase();
            final float currentAmount = database.getMoney(offlinePlayer.getUniqueId());
            database.setMoney(offlinePlayer.getUniqueId(), currentAmount + amount);
            return new EconomyResponse(amount, currentAmount, EconomyResponse.ResponseType.SUCCESS, "");
        } else {
            return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "");
        }
    }

    public EconomyResponse depositPlayer(UUID uuid, float amount) {
        final Player player = Bukkit.getPlayer(uuid);

        if(player != null && player.isOnline()) {
            final WPlayerCache wPlayerCache = getPlayerCache(uuid);
            final float currentAmount = WEconomy.getInstance().getDatabaseManager().getDatabase().getMoney(uuid);
            wPlayerCache.setMoney(wPlayerCache.getMoney() + amount);
            updatePlayerData(uuid);
            return new EconomyResponse(amount, currentAmount, EconomyResponse.ResponseType.SUCCESS, "");
        } else if(player != null && player.hasPlayedBefore()) {
            final Database database = WEconomy.getInstance().getDatabaseManager().getDatabase();
            final float currentAmount = database.getMoney(uuid);
            database.setMoney(uuid, currentAmount + amount);
            return new EconomyResponse(amount, currentAmount, EconomyResponse.ResponseType.SUCCESS, "");
        } else {
            return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "");
        }
    }
}
