package fr.ward.weconomy.manager;

import fr.ward.weconomy.WEconomy;
import fr.ward.weconomy.cache.WPlayerCache;

import java.util.ArrayList;
import java.util.UUID;

public class CacheManager {

    private final ArrayList<WPlayerCache> wPlayerCaches = new ArrayList<>();

    public ArrayList<WPlayerCache> getPlayerCaches() {
        return wPlayerCaches;
    }

    public WPlayerCache getPlayerCache (UUID uuid){
        for(WPlayerCache playerCache : wPlayerCaches){
            if(playerCache.getUuid().equals(uuid)){
                return playerCache;
            }
        }
        return null;
    }
}
