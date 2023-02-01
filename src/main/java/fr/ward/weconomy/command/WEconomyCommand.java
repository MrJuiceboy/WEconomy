package fr.ward.weconomy.command;

import fr.ward.weconomy.WEconomy;
import fr.ward.weconomy.cache.WPlayerCache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WEconomyCommand implements CommandExecutor {

    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        if (commandSender instanceof final Player player) {
            final WPlayerCache wPlayerCache = WEconomy.getInstance().getCacheManager().getPlayerCache(player.getUniqueId());

            if(args.length == 0) {
                player.sendMessage("Money : " + wPlayerCache.getMoney());
                return true;
            }
        }
        return false;
    }
}
