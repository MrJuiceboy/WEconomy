package fr.ward.weconomy.manager;

import fr.ward.weconomy.WEconomy;
import fr.ward.weconomy.cache.WPlayerCache;
import fr.ward.weconomy.database.type.Database;
import org.bukkit.Bukkit;
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
        final Database database = WEconomy.getInstance().getDatabase();
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
        final WPlayerCache wPlayerCache = WEconomy.getInstance().getCacheManager().getPlayerCache(uuid);
        final Database database = WEconomy.getInstance().getDatabase();
        Bukkit.getScheduler().runTaskAsynchronously(WEconomy.getInstance(), () -> database.setMoney(uuid, wPlayerCache.getMoney()));
    }
}
