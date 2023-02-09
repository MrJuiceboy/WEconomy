package fr.ward.weconomy.utils;

import fr.ward.weconomy.manager.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.UUID;

public class MineUtils {

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String getPrefix(){
        return color(MessageManager.PREFIX.toString());
    }

    public static void sendMessage(Player player, String message){
        player.sendMessage(color(message).replace("%prefix%", getPrefix()));
    }

    public static UUID checkUUID(String uuid) {
        int length = uuid.length();
        if(length < 32) {
            return Bukkit.getOfflinePlayer(uuid).getUniqueId();
        }
        return UUID.fromString(uuid);
    }

    public static DecimalFormat getDecimalFormat(){
        return new DecimalFormat("###,###,###,###,###.##");
    }
}
