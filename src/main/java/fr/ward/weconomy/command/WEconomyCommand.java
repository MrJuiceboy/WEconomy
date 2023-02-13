package fr.ward.weconomy.command;

import fr.ward.weconomy.WEconomy;
import fr.ward.weconomy.database.type.Database;
import fr.ward.weconomy.discord.DiscordMessage;
import fr.ward.weconomy.manager.CacheManager;
import fr.ward.weconomy.manager.EconomyManager;
import fr.ward.weconomy.manager.MessageListManager;
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
            if(args.length == 0 || args[0].equals("help")) {
                help(player);
                return true;
            }

            if(args[0].equals("balance") || args[0].equals("money")) {
                MineUtils.sendMessage(player, MessageManager.GET_MONEY.toString()
                        .replace("%current%", String.valueOf(WEconomy.getInstance().getCacheManager().getBalance(player))), false);
                return true;
            }

            if(args.length == 1) {
                if(args[0].equals("reload") || args[0].equals("rl")) {
                    return reload(player);
                }

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

            MineUtils.sendMessage(player, MessageManager.UNKNOWN_COMMAND.toString(), false);
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

    private boolean reload(Player player) {
        if(player != null && checkPermission(player, "weconomy.reload")) {
            MineUtils.sendMessage(player, MessageManager.NO_PERMISSION.toString()
                    .replace("%permission%", "weconomy.reload"), false);
            return false;
        }

        WEconomy.getInstance().reload();

        if(player != null) {
            MineUtils.sendMessage(player, MessageManager.RELOAD.toString(), false);
        } else {
            MineLogger.info(MessageManager.RELOAD.toString());
        }
        return true;
    }

    private void help(Player player) {
        if(!checkPermission(player, "weconomy.reload")) {
            for(String message : MessageListManager.HELP_ADMIN.toStringList()) {
                player.sendMessage(MineUtils.color(message));
            }
        } else {
            for(String message : MessageListManager.HELP.toStringList()) {
                player.sendMessage(MineUtils.color(message));
            }
        }
    }

    private void top(Player player) {
        if(checkPermission(player, "weconomy.top")) {
            MineUtils.sendMessage(player, MessageManager.NO_PERMISSION.toString()
                    .replace("%permission%", "weconomy.top"), false);
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
                    .replace("%permission%", "weconomy.pay"), false);
            return;
        }

        if(amount < 0.0D){
            MineUtils.sendMessage(player, MessageManager.NEGATIVE_AMOUNT.toString(), false);
            return;
        }

        if(amount > economyManager.getEconomy().getBalance(player.getUniqueId().toString())) {
            MineUtils.sendMessage(player, MessageManager.INSUFFICIENT_FUNDS.toString(), false);
            return;
        }

        final Player receiver = Bukkit.getPlayer(target);

        if(receiver == player) {
            MineUtils.sendMessage(player, MessageManager.PAY_YOURSELF.toString(), false);
            return;
        }

        if(receiver == null){
            MineUtils.sendMessage(player, MessageManager.PLAYER_NOT_FOUND.toString(), false);
        } else {
            WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.TRANSACTION, player.getName(), receiver.getName(), amount);
            economyManager.getEconomy().withdrawPlayer(player.getUniqueId().toString(), amount);
            economyManager.getEconomy().depositPlayer(receiver.getUniqueId().toString(), amount);
            MineUtils.sendMessage(player, MessageManager.SEND_PAY.toString()
                    .replace("%receiver%", receiver.getName())
                    .replace("%amount%", String.valueOf(amount)), false);
            MineUtils.sendMessage(receiver, MessageManager.RECEIVER_PAY.toString()
                    .replace("%sender%", player.getName())
                    .replace("%amount%", String.valueOf(amount)), false);
        }
    }

    private void givePlayer(Player player, String target, Double amount) {
        final EconomyManager economyManager = WEconomy.getInstance().getEconomyManager();

        if(checkPermission(player, "weconomy.give")) {
            MineUtils.sendMessage(player, MessageManager.NO_PERMISSION.toString()
                    .replace("%permission%", "weconomy.give"), false);
            return;
        }

        if(amount < 0.0D){
            MineUtils.sendMessage(player, MessageManager.NEGATIVE_AMOUNT.toString(), false);
            return;
        }

        final Player receiver = Bukkit.getPlayer(target);

        WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.GIVE, player.getName(), target, amount);
        economyManager.getEconomy().depositPlayer(target, amount);
        MineUtils.sendMessage(player, MessageManager.SEND_GIVE.toString()
                .replace("%receiver%", target)
                .replace("%amount%", String.valueOf(amount)), false);

        if(receiver != null) {
            MineUtils.sendMessage(receiver, MessageManager.RECEIVER_GIVE.toString()
                    .replace("%sender%", player.getName())
                    .replace("%amount%", String.valueOf(amount)), false);
        }

    }

    private void givePlayer(String target, Double amount) {
        final EconomyManager economyManager = WEconomy.getInstance().getEconomyManager();

        if(amount < 0.0D){
            MineLogger.warning(MessageManager.NEGATIVE_AMOUNT.toString());
            return;
        }

        final Player receiver = Bukkit.getPlayer(target);

        WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.GIVE, "[Console]", target, amount);
        economyManager.getEconomy().depositPlayer(target, amount);
        MineLogger.info(MessageManager.SEND_GIVE.toString()
                .replace("%receiver%", target)
                .replace("%amount%", String.valueOf(amount)));

        if(receiver != null) {
            MineUtils.sendMessage(receiver, MessageManager.RECEIVER_GIVE.toString()
                    .replace("%sender%", "[Console]")
                    .replace("%amount%", String.valueOf(amount)), true);
        }
    }

    private void removePlayer(Player player, String target, Double amount) {
        final EconomyManager economyManager = WEconomy.getInstance().getEconomyManager();

        if(checkPermission(player, "weconomy.remove")) {
            MineUtils.sendMessage(player, MessageManager.NO_PERMISSION.toString()
                    .replace("%permission%", "weconomy.remove"), false);
            return;
        }

        if(amount < 0.0D){
            MineUtils.sendMessage(player, MessageManager.NEGATIVE_AMOUNT.toString(), false);
            return;
        }

        final Player receiver = Bukkit.getPlayer(target);

        WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.REMOVE, player.getName(), target, amount);
        economyManager.getEconomy().withdrawPlayer(target, amount);
        MineUtils.sendMessage(player, MessageManager.SEND_REMOVE.toString()
                .replace("%receiver%", target)
                .replace("%amount%", String.valueOf(amount)), false);

        if(receiver != null) {
            MineUtils.sendMessage(receiver, MessageManager.RECEIVER_REMOVE.toString()
                    .replace("%sender%", player.getName())
                    .replace("%amount%", String.valueOf(amount)), false);
        }
    }

    private void removePlayer(String target, Double amount) {
        final EconomyManager economyManager = WEconomy.getInstance().getEconomyManager();

        if(amount < 0.0D){
            MineLogger.warning(MessageManager.NEGATIVE_AMOUNT.toString());
            return;
        }

        final Player receiver = Bukkit.getPlayer(target);

        WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.REMOVE, "[Console]", target, amount);
        economyManager.getEconomy().withdrawPlayer(target, amount);
        MineLogger.info(MessageManager.SEND_REMOVE.toString()
                .replace("%receiver%", target)
                .replace("%amount%", String.valueOf(amount)));

        if(receiver != null) {
            MineUtils.sendMessage(receiver, MessageManager.RECEIVER_REMOVE.toString()
                    .replace("%sender%", "[Console]")
                    .replace("%amount%", String.valueOf(amount)), true);
        }
    }

    private void reset(Player player) {
        final CacheManager cacheManager = WEconomy.getInstance().getCacheManager();
        final Database database = WEconomy.getInstance().getDatabaseManager().getDatabase();

        if(checkPermission(player, "weconomy.reset")) {
            MineUtils.sendMessage(player, MessageManager.NO_PERMISSION.toString()
                    .replace("%permission%", "weconomy.reset"), false);
            return;
        }

        WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.RESET, player.getName(), null, 0);
        cacheManager.getPlayerCaches().clear();
        database.reset();
        for(Player plz : Bukkit.getOnlinePlayers()) {
            cacheManager.addPlayerCache(plz);
        }
        MineUtils.sendMessage(player, MessageManager.RESET_ALL.toString(), false);
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
                    .replace("%permission%", "weconomy.reset"), false);
            return;
        }

        final Player receiver = Bukkit.getPlayer(target);

        WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.REMOVE, player.getName(), target, economyManager.getEconomy().getBalance(target));
        MineUtils.sendMessage(player, MessageManager.SEND_REMOVE.toString()
                .replace("%receiver%", target)
                .replace("%amount%", String.valueOf(economyManager.getEconomy().getBalance(target))), false);

        if(receiver != null) {
            MineUtils.sendMessage(receiver, MessageManager.RECEIVER_REMOVE.toString()
                    .replace("%sender%", player.getName())
                    .replace("%amount%", String.valueOf(economyManager.getEconomy().getBalance(target))), false);
        }
        economyManager.getEconomy().withdrawPlayer(target, economyManager.getEconomy().getBalance(target));
    }

    private void reset(String target) {
        final EconomyManager economyManager = WEconomy.getInstance().getEconomyManager();

        final Player receiver = Bukkit.getPlayer(target);

        WEconomy.getInstance().getDiscordManager().sendMessage(DiscordMessage.REMOVE, "[Console]", target, economyManager.getEconomy().getBalance(target));
        MineLogger.info(MessageManager.SEND_REMOVE.toString()
                .replace("%receiver%", target)
                .replace("%amount%", String.valueOf(economyManager.getEconomy().getBalance(target))));

        if(receiver != null) {
            MineUtils.sendMessage(receiver, MessageManager.RECEIVER_REMOVE.toString()
                    .replace("%sender%", "[Console]")
                    .replace("%amount%", String.valueOf(economyManager.getEconomy().getBalance(target))), true);
        }
        economyManager.getEconomy().withdrawPlayer(target, economyManager.getEconomy().getBalance(target));
    }

    private boolean checkPermission(Player player, String permission) {
        if(player.isOp()) {
            return false;
        } else return !player.hasPermission(permission);
    }
}
