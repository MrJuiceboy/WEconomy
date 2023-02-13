package fr.ward.weconomy.manager;

import fr.ward.weconomy.WEconomy;
import fr.ward.weconomy.cache.WPlayerCache;
import fr.ward.weconomy.database.type.Database;
import fr.ward.weconomy.utils.MineUtils;
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
        final WPlayerCache wPlayerCache = new WPlayerCache(uuid, WEconomy.getInstance().getDatabaseManager().getDatabase().getMoney(uuid));
        WEconomy.getInstance().getCacheManager().getPlayerCaches().add(wPlayerCache);
        return wPlayerCache;
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

    public float getBalance(String uuid) {
        final Player player = Bukkit.getPlayer(uuid);
        final UUID player_UUID = MineUtils.checkUUID(uuid);
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player_UUID);

        if(player != null && player.isOnline()) {
            final WPlayerCache wPlayerCache = getPlayerCache(player.getUniqueId());
            return (float) (Math.round(wPlayerCache.getMoney() * 100.0) / 100.0);
        } else if(offlinePlayer.hasPlayedBefore()) {
            final Database database = WEconomy.getInstance().getDatabaseManager().getDatabase();
            return (float) (Math.round(database.getMoney(player_UUID) * 100.0) / 100.0);
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
                return new EconomyResponse(amount, currentAmount, EconomyResponse.ResponseType.SUCCESS, null);
            } else {
                return new EconomyResponse(amount, currentAmount, EconomyResponse.ResponseType.FAILURE, "Loan was not permitted!");
            }
        } else if(offlinePlayer.hasPlayedBefore()) {
            final Database database = WEconomy.getInstance().getDatabaseManager().getDatabase();
            final float currentAmount = database.getMoney(offlinePlayer.getUniqueId());
            if(currentAmount >= amount) {
                database.setMoney(offlinePlayer.getUniqueId(), currentAmount - amount);
                return new EconomyResponse(amount, currentAmount, EconomyResponse.ResponseType.SUCCESS, null);
            } else {
                return new EconomyResponse(amount, currentAmount, EconomyResponse.ResponseType.FAILURE, "Loan was not permitted!");
            }
        } else {
            return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "User does not exist!");
        }
    }

    public EconomyResponse withdrawPlayer(String uuid, float amount) {
        final Player player = Bukkit.getPlayer(uuid);
        final UUID player_UUID = MineUtils.checkUUID(uuid);
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player_UUID);

        if(player != null && player.isOnline()) {
            final WPlayerCache wPlayerCache = getPlayerCache(player.getUniqueId());
            final float currentAmount = WEconomy.getInstance().getDatabaseManager().getDatabase().getMoney(player.getUniqueId());
            if(currentAmount >= amount) {
                wPlayerCache.setMoney(wPlayerCache.getMoney() - amount);
                updatePlayerData(player.getUniqueId());
                return new EconomyResponse(amount, currentAmount, EconomyResponse.ResponseType.SUCCESS, null);
            } else {
                return new EconomyResponse(amount, currentAmount, EconomyResponse.ResponseType.FAILURE, "Loan was not permitted!");
            }
        } else if(offlinePlayer.hasPlayedBefore()) {
            final Database database = WEconomy.getInstance().getDatabaseManager().getDatabase();
            final float currentAmount = database.getMoney(player_UUID);
            if (currentAmount >= amount) {
                database.setMoney(player_UUID, currentAmount - amount);
                return new EconomyResponse(amount, currentAmount, EconomyResponse.ResponseType.SUCCESS, null);
            } else {
                return new EconomyResponse(amount, currentAmount, EconomyResponse.ResponseType.FAILURE, "Loan was not permitted!");
            }
        } else {
            return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "User does not exist!");
        }
    }

    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, float amount) {
        if(offlinePlayer.isOnline()) {
            final WPlayerCache wPlayerCache = getPlayerCache(offlinePlayer.getUniqueId());
            final float currentAmount = WEconomy.getInstance().getDatabaseManager().getDatabase().getMoney(offlinePlayer.getUniqueId());
            wPlayerCache.setMoney(wPlayerCache.getMoney() + amount);
            updatePlayerData(offlinePlayer.getUniqueId());
            return new EconomyResponse(amount, currentAmount, EconomyResponse.ResponseType.SUCCESS, null);
        } else if(offlinePlayer.hasPlayedBefore()) {
            final Database database = WEconomy.getInstance().getDatabaseManager().getDatabase();
            final float currentAmount = database.getMoney(offlinePlayer.getUniqueId());
            database.setMoney(offlinePlayer.getUniqueId(), currentAmount + amount);
            return new EconomyResponse(amount, currentAmount, EconomyResponse.ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "User does not exist!");
        }
    }

    public EconomyResponse depositPlayer(String uuid, float amount) {
        final Player player = Bukkit.getPlayer(uuid);
        final UUID player_UUID = MineUtils.checkUUID(uuid);
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player_UUID);

        if(player != null && player.isOnline()) {
            final WPlayerCache wPlayerCache = getPlayerCache(player.getUniqueId());
            final float currentAmount = WEconomy.getInstance().getDatabaseManager().getDatabase().getMoney(player.getUniqueId());
            wPlayerCache.setMoney(wPlayerCache.getMoney() + amount);
            updatePlayerData(player.getUniqueId());
            return new EconomyResponse(amount, currentAmount, EconomyResponse.ResponseType.SUCCESS, null);
        } else if(offlinePlayer.hasPlayedBefore()) {
            final Database database = WEconomy.getInstance().getDatabaseManager().getDatabase();
            final float currentAmount = database.getMoney(player_UUID);
            database.setMoney(player_UUID, currentAmount + amount);
            return new EconomyResponse(amount, currentAmount, EconomyResponse.ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(amount, 0, EconomyResponse.ResponseType.FAILURE, "User does not exist!");
        }
    }
}