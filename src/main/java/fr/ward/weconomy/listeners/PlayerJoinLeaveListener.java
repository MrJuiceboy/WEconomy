package fr.ward.weconomy.listeners;

import fr.ward.weconomy.WEconomy;
import fr.ward.weconomy.cache.WPlayerCache;
import fr.ward.weconomy.database.type.Database;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinLeaveListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        final Database database = WEconomy.getInstance().getDatabase();
        Bukkit.getScheduler().runTaskAsynchronously(WEconomy.getInstance(), () -> database.addIntoDatabase(event.getPlayer()));
        final WPlayerCache wPlayerCache = new WPlayerCache(uuid, database.getMoney(uuid));
        WEconomy.getInstance().getCacheManager().getPlayerCaches().add(wPlayerCache);
    }
}
