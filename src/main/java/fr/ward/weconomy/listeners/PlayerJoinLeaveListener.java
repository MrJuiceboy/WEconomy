package fr.ward.weconomy.listeners;

import fr.ward.weconomy.WEconomy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinLeaveListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        WEconomy.getInstance().getCacheManager().addPlayerCache(event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        WEconomy.getInstance().getCacheManager().updatePlayerData(event.getPlayer().getUniqueId());
    }
}
