package fr.ward.weconomy.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class MineLogger {
    private static void sendMessage(String message) {
        Bukkit.getServer().getConsoleSender().sendMessage(message);
    }

    public static void info(String message) {
        sendMessage(ChatColor.BLUE + "[WEconomy] " + message);
    }

    public static void warning(String message) {
        sendMessage(ChatColor.YELLOW + "[WEconomy] " + message);
    }

    public static void error(String message) {
        sendMessage(ChatColor.RED + "[WEconomy] " + message);
    }
}
