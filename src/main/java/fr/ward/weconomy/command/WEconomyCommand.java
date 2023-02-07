package fr.ward.weconomy.command;

import fr.ward.weconomy.WEconomy;
import fr.ward.weconomy.cache.WPlayerCache;
import fr.ward.weconomy.database.type.Database;
import fr.ward.weconomy.discord.DiscordMessage;
import fr.ward.weconomy.manager.CacheManager;
import fr.ward.weconomy.manager.EconomyManager;
import fr.ward.weconomy.manager.MessageManager;
import fr.ward.weconomy.utils.MineLogger;
import fr.ward.weconomy.utils.MineUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WEconomyCommand implements CommandExecutor {

    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        if (commandSender instanceof final Player player) {

            if(args.length == 0 || args[0].equals("balance")) {
                MineUtils.sendMessage(player, MessageManager.GET_MONEY.toString()
                        .replace("%current%", String.valueOf(WEconomy.getInstance().getCacheManager().getBalance(player.getUniqueId()))));
                return true;
            }

            if(args.length == 1) {
                if(args[0].equals("credit") || args[0].equals("dev") || args[0].equals("plugin")) {
                    player.sendMessage(MineUtils.getPrefix() + ChatColor.GRAY + " Created by " + ChatColor.AQUA +  "Ward" + ChatColor.GRAY + " with " + ChatColor.DARK_RED + "❤" + ChatColor.DARK_GRAY + " (Plugin Free)");
                    player.sendMessage(ChatColor.BLUE + "[Discord] " + ChatColor.GRAY + "https://discord.gg/cJF48s3SBJ" + ChatColor.GRAY + " (" + ChatColor.YELLOW + "Click" + ChatColor.GRAY + ")");
                    return true;
                }

                if(args[0].equals("rank") || args[0].equals("top") || args[0].equals("baltop") || args[0].equals("balancetop")) {
                    top(player);
                    return true;
                }
            }

            if(args.length == 2) {
                if(args[1].equals("all")) {
                    reset(player);
                    return true;
                }

                if(args[0].equals("reset")) {
                    reset(player, args[1]);
                    return true;
                }
            }

            if(args.length == 3) {
                if(args[0].equals("pay")) {
                    payingPlayer(player, args[1], Double.parseDouble(args[2]));
                    return true;
                }

                if(args[0].equals("give")) {
                    givePlayer(player, args[1], Double.parseDouble(args[2]));
                    return true;
                }

                if(args[0].equals("remove")) {
                    removePlayer(player, args[1], Double.parseDouble(args[2]));
                    return true;
                }
            }

            MineUtils.sendMessage(player, MessageManager.UNKNOWN_COMMAND.toString());
            return true;
        } else if (commandSender instanceof ConsoleCommandSender) {
            if(args.length == 2) {
                if(args[1].equals("all")) {
                    reset();
                    return true;
                }

                if(args[0].equals("reset")) {
                    reset(args[1]);
                    return true;
                }
            }

            if(args.length == 3) {
                if(args[0].equals("give")) {
                    givePlayer(args[1], Double.parseDouble(args[2]));
                    return true;
                }

                if(args[0].equals("remove")) {
                    removePlayer(args[1], Double.parseDouble(args[2]));
                    return true;
                }
            }
        }
        return false;
    }

    private void top(Player player) {
        if(checkPermission(player, "weconomy.top")) {
            MineUtils.sendMessage(player, MessageManager.NO_PERMISSION.toString()
                    .replace("%permission%", "weconomy.top"));
            return;
        }

        player.sendMessage(ChatColor.GRAY + "========[ Top ]========");
        for (int i = 1; i < 6; i++) {
            player.sendMessage(WEconomy.getInstance().getDatabaseManager().getDatabase().getTop(i));
        }
    }

    private void payingPlayer(Player player, String target, Double amount) {
        final EconomyManager economyManager = WEconomy.getInstance().getEconomyManager();

        if(checkPermission(player, "weconomy.pay")) {
            MineUtils.sendMessage(player, MessageManager.NO_PERMISSION.toString()
                    .replace("%permission%", "weconomy.pay"));
            return;
        }

        if(amount < 0.0D){
            MineUtils.sendMessage(player, MessageManager.NEGATIVE_AMOUNT.toString());
            return;
        }

        if(amount > economyManager.getEconomy().getBalance(player.getUniqueId().toString())) {
            MineUtils.sendMessage(player, MessageManager.INSUFFICIENT_FUNDS.toString());
            return;
        }

        final Player receiver = Bukkit.getPlayer(target);

        if(receiver == player) {
            MineUtils.sendMessage(player, MessageManager.PAY_YOURSELF.toString());
            return;
        }

        if(receiver == null){
            MineUtils.sendMessage(player, MessageManager.PLAYER_NOT_FOUND.toString());
        } else {
            WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.TRANSACTION, player.getName(), receiver.getName(), amount);
            economyManager.getEconomy().withdrawPlayer(player.getUniqueId().toString(), amount);
            economyManager.getEconomy().depositPlayer(receiver.getUniqueId().toString(), amount);
            MineUtils.sendMessage(player, MessageManager.SEND_PAY.toString()
                    .replace("%receiver%", receiver.getName())
                    .replace("%amount%", String.valueOf(amount)));
            MineUtils.sendMessage(receiver, MessageManager.RECEIVER_PAY.toString()
                    .replace("%sender%", player.getName())
                    .replace("%amount%", String.valueOf(amount)));
        }
    }

    private void givePlayer(Player player, String target, Double amount) {
        final EconomyManager economyManager = WEconomy.getInstance().getEconomyManager();

        if(checkPermission(player, "weconomy.give")) {
            MineUtils.sendMessage(player, MessageManager.NO_PERMISSION.toString()
                    .replace("%permission%", "weconomy.give"));
            return;
        }

        if(amount < 0.0D){
            MineUtils.sendMessage(player, MessageManager.NEGATIVE_AMOUNT.toString());
            return;
        }

        final Player receiver = Bukkit.getPlayer(target);

        if(receiver == null){
            MineUtils.sendMessage(player, MessageManager.PLAYER_NOT_FOUND.toString());
        } else {
            WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.GIVE, player.getName(), receiver.getName(), amount);
            economyManager.getEconomy().depositPlayer(receiver.getUniqueId().toString(), amount);
            MineUtils.sendMessage(player, MessageManager.SEND_GIVE.toString()
                    .replace("%receiver%", receiver.getName())
                    .replace("%amount%", String.valueOf(amount)));
            MineUtils.sendMessage(receiver, MessageManager.RECEIVER_GIVE.toString()
                    .replace("%sender%", player.getName())
                    .replace("%amount%", String.valueOf(amount)));
        }
    }

    private void givePlayer(String target, Double amount) {
        final EconomyManager economyManager = WEconomy.getInstance().getEconomyManager();

        if(amount < 0.0D){
            MineLogger.warning(MessageManager.NEGATIVE_AMOUNT.toString());
            return;
        }

        final Player receiver = Bukkit.getPlayer(target);

        if(receiver == null){
            MineLogger.warning(MessageManager.PLAYER_NOT_FOUND.toString());
        } else {
            WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.GIVE, "[Console]", receiver.getName(), amount);
            economyManager.getEconomy().depositPlayer(receiver.getUniqueId().toString(), amount);
            MineLogger.info(MessageManager.SEND_GIVE.toString()
                    .replace("%receiver%", receiver.getName())
                    .replace("%amount%", String.valueOf(amount)));
            MineUtils.sendMessage(receiver, MessageManager.RECEIVER_GIVE.toString()
                    .replace("%sender%", "[Console]")
                    .replace("%amount%", String.valueOf(amount)));
        }
    }

    private void removePlayer(Player player, String target, Double amount) {
        final EconomyManager economyManager = WEconomy.getInstance().getEconomyManager();

        if(checkPermission(player, "weconomy.remove")) {
            MineUtils.sendMessage(player, MessageManager.NO_PERMISSION.toString()
                    .replace("%permission%", "weconomy.remove"));
            return;
        }

        if(amount < 0.0D){
            MineUtils.sendMessage(player, MessageManager.NEGATIVE_AMOUNT.toString());
            return;
        }

        final Player receiver = Bukkit.getPlayer(target);

        if(receiver == null){
            MineUtils.sendMessage(player, MessageManager.PLAYER_NOT_FOUND.toString());
        } else {
            WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.REMOVE, player.getName(), receiver.getName(), amount);
            economyManager.getEconomy().withdrawPlayer(receiver.getUniqueId().toString(), amount);
            MineUtils.sendMessage(player, MessageManager.SEND_REMOVE.toString()
                    .replace("%receiver%", receiver.getName())
                    .replace("%amount%", String.valueOf(amount)));
            MineUtils.sendMessage(receiver, MessageManager.RECEIVER_REMOVE.toString()
                    .replace("%sender%", player.getName())
                    .replace("%amount%", String.valueOf(amount)));
        }
    }

    private void removePlayer(String target, Double amount) {
        final EconomyManager economyManager = WEconomy.getInstance().getEconomyManager();

        if(amount < 0.0D){
            MineLogger.warning(MessageManager.NEGATIVE_AMOUNT.toString());
            return;
        }

        final Player receiver = Bukkit.getPlayer(target);

        if(receiver == null){
            MineLogger.warning(MessageManager.PLAYER_NOT_FOUND.toString());
        } else {
            WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.REMOVE, "[Console]", receiver.getName(), amount);
            economyManager.getEconomy().withdrawPlayer(receiver.getUniqueId().toString(), amount);
            MineLogger.info(MessageManager.SEND_REMOVE.toString()
                    .replace("%receiver%", receiver.getName())
                    .replace("%amount%", String.valueOf(amount)));
            MineUtils.sendMessage(receiver, MessageManager.RECEIVER_REMOVE.toString()
                    .replace("%sender%", "[Console]")
                    .replace("%amount%", String.valueOf(amount)));
        }
    }

    private void reset(Player player) {
        final CacheManager cacheManager = WEconomy.getInstance().getCacheManager();
        final Database database = WEconomy.getInstance().getDatabaseManager().getDatabase();

        if(checkPermission(player, "weconomy.reset")) {
            MineUtils.sendMessage(player, MessageManager.NO_PERMISSION.toString()
                    .replace("%permission%", "weconomy.reset"));
            return;
        }

        WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.RESET, player.getName(), null, 0);
        cacheManager.getPlayerCaches().clear();
        database.reset();
        for(Player plz : Bukkit.getOnlinePlayers()) {
            cacheManager.addPlayerCache(plz);
        }
        MineUtils.sendMessage(player, MessageManager.RESET_ALL.toString());
    }

    private void reset() {
        final CacheManager cacheManager = WEconomy.getInstance().getCacheManager();
        final Database database = WEconomy.getInstance().getDatabaseManager().getDatabase();

        WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.RESET, "[CONSOLE]", null, 0);
        cacheManager.getPlayerCaches().clear();
        database.reset();
        for(Player plz : Bukkit.getOnlinePlayers()) {
            cacheManager.addPlayerCache(plz);
        }
        MineLogger.info(MessageManager.RESET_ALL.toString());
    }

    private void reset(Player player, String target) {
        final EconomyManager economyManager = WEconomy.getInstance().getEconomyManager();

        if(checkPermission(player, "weconomy.reset")) {
            MineUtils.sendMessage(player, MessageManager.NO_PERMISSION.toString()
                    .replace("%permission%", "weconomy.reset"));
            return;
        }

        final Player receiver = Bukkit.getPlayer(target);

        if(receiver == null){
            MineUtils.sendMessage(player, MessageManager.PLAYER_NOT_FOUND.toString());
        } else {
            final WPlayerCache wPlayerCache = WEconomy.getInstance().getCacheManager().getPlayerCache(receiver.getUniqueId());

            WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.REMOVE, player.getName(), receiver.getName(), wPlayerCache.getMoney());
            MineUtils.sendMessage(player, MessageManager.SEND_REMOVE.toString()
                    .replace("%receiver%", receiver.getName())
                    .replace("%amount%", String.valueOf(wPlayerCache.getMoney())));
            MineUtils.sendMessage(receiver, MessageManager.RECEIVER_REMOVE.toString()
                    .replace("%sender%", player.getName())
                    .replace("%amount%", String.valueOf(wPlayerCache.getMoney())));
            economyManager.getEconomy().withdrawPlayer(receiver.getUniqueId().toString(), wPlayerCache.getMoney());
        }
    }

    private void reset(String target) {
        final EconomyManager economyManager = WEconomy.getInstance().getEconomyManager();

        final Player receiver = Bukkit.getPlayer(target);

        if(receiver == null){
            MineLogger.warning(MessageManager.PLAYER_NOT_FOUND.toString());
        } else {
            final WPlayerCache wPlayerCache = WEconomy.getInstance().getCacheManager().getPlayerCache(receiver.getUniqueId());

            WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.REMOVE, "[Console]", receiver.getName(), wPlayerCache.getMoney());
            MineLogger.info(MessageManager.SEND_REMOVE.toString()
                    .replace("%receiver%", receiver.getName())
                    .replace("%amount%", String.valueOf(wPlayerCache.getMoney())));
            MineUtils.sendMessage(receiver, MessageManager.RECEIVER_REMOVE.toString()
                    .replace("%sender%", "[Console]")
                    .replace("%amount%", String.valueOf(wPlayerCache.getMoney())));
            economyManager.getEconomy().withdrawPlayer(receiver.getUniqueId().toString(), wPlayerCache.getMoney());
        }
    }

    private boolean checkPermission(Player player, String permission) {
        if(player.isOp()) {
            return false;
        } else return !player.hasPermission(permission);
    }
}
