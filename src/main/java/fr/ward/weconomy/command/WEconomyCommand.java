package fr.ward.weconomy.command;

import fr.ward.weconomy.WEconomy;
import fr.ward.weconomy.cache.WPlayerCache;
import fr.ward.weconomy.database.type.Database;
import fr.ward.weconomy.discord.DiscordMessage;
import fr.ward.weconomy.manager.CacheManager;
import fr.ward.weconomy.manager.EconomyManager;
import fr.ward.weconomy.manager.MessageManager;
import fr.ward.weconomy.utils.MineLogger;
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
                player.sendMessage(MessageManager.GET_MONEY.build(player));
                return true;
            }

            if(args.length == 1) {
                if(args[0].equals("credit") || args[0].equals("dev") || args[0].equals("plugin")) {
                    player.sendMessage(WEconomy.getInstance().getPrefix() + ChatColor.GRAY + " Created by " + ChatColor.AQUA +  "Ward" + ChatColor.GRAY + " with " + ChatColor.DARK_RED + "❤" + ChatColor.DARK_GRAY + " (Plugin Free)");
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

            player.sendMessage(WEconomy.getInstance().getPrefix() + MessageManager.BAD_SYNTAX.build(player));
            return false;
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
            player.sendMessage(MessageManager.NO_PERMISSION.build(player));
            return;
        }

        player.sendMessage(ChatColor.GRAY + "========[ Top ]========");
        for (int i = 1; i < 6; i++) {
            player.sendMessage(WEconomy.getInstance().getDatabase().getTop(i));
        }
    }

    private void payingPlayer(Player player, String target, Double amount) {
        final EconomyManager economyManager = WEconomy.getInstance().getEconomy();

        if(checkPermission(player, "weconomy.pay")) {
            player.sendMessage(MessageManager.NO_PERMISSION.build(player));
            return;
        }

        if(amount < 0.0D){
            player.sendMessage(MessageManager.NEGATIVE_AMOUNT.build(player));
            return;
        }

        if(amount > economyManager.getBalance(player.getUniqueId().toString())) {
            player.sendMessage(MessageManager.INSUFFICIENT_FUNDS.build(player));
            return;
        }

        final Player receiver = Bukkit.getPlayer(target);

        if(receiver == player) {
            player.sendMessage(MessageManager.PAY_YOURSELF.build(player));
            return;
        }

        if(receiver == null){
            player.sendMessage(MessageManager.PLAYER_NOT_FOUND.build(player));
        } else {
            WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.TRANSACTION, player.getName(), receiver.getName(), amount);
            economyManager.withdrawPlayer(player.getUniqueId().toString(), amount);
            economyManager.depositPlayer(receiver.getUniqueId().toString(), amount);
            player.sendMessage(MessageManager.SEND_PAY.build(player, receiver, amount));
            receiver.sendMessage(MessageManager.RECEIVER_PAY.build(player, receiver, amount));
        }
    }

    private void givePlayer(Player player, String target, Double amount) {
        final EconomyManager economyManager = WEconomy.getInstance().getEconomy();

        if(checkPermission(player, "weconomy.give")) {
            player.sendMessage(MessageManager.NO_PERMISSION.build(player));
            return;
        }

        if(amount < 0.0D){
            player.sendMessage(MessageManager.NEGATIVE_AMOUNT.build(player));
            return;
        }

        final Player receiver = Bukkit.getPlayer(target);

        if(receiver == null){
            player.sendMessage(MessageManager.PLAYER_NOT_FOUND.build(player));
        } else {
            WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.GIVE, player.getName(), receiver.getName(), amount);
            economyManager.depositPlayer(receiver.getUniqueId().toString(), amount);
            player.sendMessage(MessageManager.SEND_GIVE.build(player, receiver, amount));
            receiver.sendMessage(MessageManager.RECEIVER_GIVE.build(player, receiver, amount));
        }
    }

    private void givePlayer(String target, Double amount) {
        final EconomyManager economyManager = WEconomy.getInstance().getEconomy();

        if(amount < 0.0D){
            MineLogger.warning(MessageManager.NEGATIVE_AMOUNT.build());
            return;
        }

        final Player receiver = Bukkit.getPlayer(target);

        if(receiver == null){
            MineLogger.warning(MessageManager.PLAYER_NOT_FOUND.build());
        } else {
            WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.GIVE, "[Console]", receiver.getName(), amount);
            economyManager.depositPlayer(receiver.getUniqueId().toString(), amount);
            MineLogger.info(MessageManager.SEND_GIVE.build("[Console]", target, amount));
            receiver.sendMessage(MessageManager.RECEIVER_GIVE.build("[Console]", target, amount));
        }
    }

    private void removePlayer(Player player, String target, Double amount) {
        final EconomyManager economyManager = WEconomy.getInstance().getEconomy();

        if(checkPermission(player, "weconomy.remove")) {
            player.sendMessage(MessageManager.NO_PERMISSION.build(player));
            return;
        }

        if(amount < 0.0D){
            player.sendMessage(MessageManager.NEGATIVE_AMOUNT.build(player));
            return;
        }

        final Player receiver = Bukkit.getPlayer(target);

        if(receiver == null){
            player.sendMessage(MessageManager.PLAYER_NOT_FOUND.build(player));
        } else {
            WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.REMOVE, player.getName(), receiver.getName(), amount);
            economyManager.withdrawPlayer(receiver.getUniqueId().toString(), amount);
            player.sendMessage(MessageManager.SEND_REMOVE.build(player, receiver, amount));
            receiver.sendMessage(MessageManager.RECEIVER_REMOVE.build(player, receiver, amount));
        }
    }

    private void removePlayer(String target, Double amount) {
        final EconomyManager economyManager = WEconomy.getInstance().getEconomy();

        if(amount < 0.0D){
            MineLogger.warning(MessageManager.NEGATIVE_AMOUNT.build());
            return;
        }

        final Player receiver = Bukkit.getPlayer(target);

        if(receiver == null){
            MineLogger.warning(MessageManager.PLAYER_NOT_FOUND.build());
        } else {
            WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.REMOVE, "[Console]", receiver.getName(), amount);
            economyManager.withdrawPlayer(receiver.getUniqueId().toString(), amount);
            MineLogger.info(MessageManager.SEND_REMOVE.build("[Console]", target, amount));
            receiver.sendMessage(MessageManager.RECEIVER_REMOVE.build("[Console]", target, amount));
        }
    }

    private void reset(Player player) {
        final CacheManager cacheManager = WEconomy.getInstance().getCacheManager();
        final Database database = WEconomy.getInstance().getDatabase();

        if(checkPermission(player, "weconomy.reset")) {
            player.sendMessage(MessageManager.NO_PERMISSION.build(player));
            return;
        }

        WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.RESET, player.getName(), null, 0);
        cacheManager.getPlayerCaches().clear();
        database.reset();
        for(Player plz : Bukkit.getOnlinePlayers()) {
            cacheManager.addPlayerCache(plz);
        }
        player.sendMessage(MessageManager.RESET_ALL.build(player));
    }

    private void reset() {
        final CacheManager cacheManager = WEconomy.getInstance().getCacheManager();
        final Database database = WEconomy.getInstance().getDatabase();

        WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.RESET, "[CONSOLE]", null, 0);
        cacheManager.getPlayerCaches().clear();
        database.reset();
        for(Player plz : Bukkit.getOnlinePlayers()) {
            cacheManager.addPlayerCache(plz);
        }
        MineLogger.info(MessageManager.RESET_ALL.build());
    }

    private void reset(Player player, String target) {
        final EconomyManager economyManager = WEconomy.getInstance().getEconomy();

        if(checkPermission(player, "weconomy.reset")) {
            player.sendMessage(MessageManager.NO_PERMISSION.build(player));
            return;
        }

        final Player receiver = Bukkit.getPlayer(target);

        if(receiver == null){
            player.sendMessage(MessageManager.PLAYER_NOT_FOUND.build(player));
        } else {
            final WPlayerCache wPlayerCache = WEconomy.getInstance().getCacheManager().getPlayerCache(receiver.getUniqueId());

            WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.REMOVE, player.getName(), receiver.getName(), wPlayerCache.getMoney());
            player.sendMessage(MessageManager.SEND_REMOVE.build(player, receiver, wPlayerCache.getMoney()));
            receiver.sendMessage(MessageManager.RECEIVER_REMOVE.build(player, receiver, wPlayerCache.getMoney()));
            economyManager.withdrawPlayer(receiver.getUniqueId().toString(), wPlayerCache.getMoney());
        }
    }

    private void reset(String target) {
        final EconomyManager economyManager = WEconomy.getInstance().getEconomy();

        final Player receiver = Bukkit.getPlayer(target);

        if(receiver == null){
            MineLogger.warning(MessageManager.PLAYER_NOT_FOUND.build());
        } else {
            final WPlayerCache wPlayerCache = WEconomy.getInstance().getCacheManager().getPlayerCache(receiver.getUniqueId());

            WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.REMOVE, "[CONSOLE]", receiver.getName(), wPlayerCache.getMoney());
            MineLogger.info(MessageManager.SEND_REMOVE.build("[CONSOLE]", target, wPlayerCache.getMoney()));
            receiver.sendMessage(MessageManager.RECEIVER_REMOVE.build("[CONSOLE]", target, wPlayerCache.getMoney()));
            economyManager.withdrawPlayer(receiver.getUniqueId().toString(), wPlayerCache.getMoney());
        }
    }

    private boolean checkPermission(Player player, String permission) {
        if(player.isOp()) {
            return false;
        } else return !player.hasPermission(permission);
    }
}
