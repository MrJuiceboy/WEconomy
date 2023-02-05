package fr.ward.weconomy.manager;

import fr.ward.weconomy.WEconomy;
import fr.ward.weconomy.cache.WPlayerCache;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public enum MessageManager {

    GET_MONEY(WEconomy.getInstance().getConfig().getString("MessageGetMoney")),
    NEGATIVE_AMOUNT(WEconomy.getInstance().getConfig().getString("MessageNegativeAmount")),
    PLAYER_NOT_FOUND(WEconomy.getInstance().getConfig().getString("MessagePlayerNotFound")),
    INSUFFICIENT_FUNDS(WEconomy.getInstance().getConfig().getString("MessageInsufficientFunds")),
    PAY_YOURSELF(WEconomy.getInstance().getConfig().getString("MessagePayYourself")),
    SEND_PAY(WEconomy.getInstance().getConfig().getString("MessagePaySend")),
    RECEIVER_PAY(WEconomy.getInstance().getConfig().getString("MessagePayReceive")),
    SEND_GIVE(WEconomy.getInstance().getConfig().getString("MessageGiveSend")),
    RECEIVER_GIVE(WEconomy.getInstance().getConfig().getString("MessageGiveReceive")),
    SEND_REMOVE(WEconomy.getInstance().getConfig().getString("MessageRemoveSend")),
    RECEIVER_REMOVE(WEconomy.getInstance().getConfig().getString("MessageRemoveReceive")),
    BAL_TOP(WEconomy.getInstance().getConfig().getString("MessageBalTop")),
    BAL_TOP_NOT_FUNDS(WEconomy.getInstance().getConfig().getString("MessageBalTopNotFound")),
    NO_PERMISSION(WEconomy.getInstance().getConfig().getString("MessageNoPermission")),
    RESET_ALL(WEconomy.getInstance().getConfig().getString("MessageResetAll")),
    BAD_SYNTAX(WEconomy.getInstance().getConfig().getString("MessageBadSyntax")),
    ;

    private final String message;

    MessageManager(String message) {
        this.message = message;
    }

    public String build() {
        return ChatColor.translateAlternateColorCodes('&', this.message);
    }

    public String build(Player player) {
        final String message = WEconomy.getInstance().getPrefix() + " " + ChatColor.translateAlternateColorCodes('&', this.message);
        if(hasPlaceHolderAPI()) {
            return PlaceholderAPI.setPlaceholders(player, replace(player, message));
        }
        return replace(player, message);
    }

    public String build(Player player, Player receiver, double amount) {
        final String message = WEconomy.getInstance().getPrefix() + " " + ChatColor.translateAlternateColorCodes('&', this.message);
        if(hasPlaceHolderAPI()) {
            return PlaceholderAPI.setPlaceholders(player, replace(player.getName(), receiver.getName(), amount, message));
        }
        return replace(player.getName(), receiver.getName(), amount, message);
    }

    public String build(String sender, String receiver, double amount) {
        final String message = WEconomy.getInstance().getPrefix() + " " + ChatColor.translateAlternateColorCodes('&', this.message);
        return replace(sender, receiver, amount, message);
    }

    public String build(int place, String playerName, double amount) {
        final String message = ChatColor.translateAlternateColorCodes('&', this.message);
        return replace(place, playerName, amount, message);
    }

    private String replace(Player player, String message) {
        final WPlayerCache wPlayerCache = WEconomy.getInstance().getCacheManager().getPlayerCache(player.getUniqueId());
        switch (this) {
            case GET_MONEY : return message.replace("%weconomy_current%", String.valueOf(wPlayerCache.getMoney()));
            default: return message;
        }
    }

    private String replace(String sender, String receiver, double amount, String message) {
        switch (this) {
            case SEND_PAY, SEND_GIVE, SEND_REMOVE: {
                final String doubleReplace = message.replace("%receiver%", receiver);
                return doubleReplace.replace("%amount%", String.valueOf(amount));
            }

            case RECEIVER_PAY, RECEIVER_GIVE, RECEIVER_REMOVE: {
                final String doubleReplace = message.replace("%player%", sender);
                return doubleReplace.replace("%amount%", String.valueOf(amount));
            }
            default: return message;
        }
    }

    private String replace(int place, String playerName, double amount, String message) {
        switch (this) {
            case BAL_TOP : {
                final String doubleReplace = message.replace("%rank%", String.valueOf(place));
                final String tripleReplace = doubleReplace.replace("%player%", playerName);
                return tripleReplace.replace("%amount%", String.valueOf(amount));
            }

            case BAL_TOP_NOT_FUNDS: return message.replace("%rank%", String.valueOf(place));
            default: return message;
        }
    }

    private boolean hasPlaceHolderAPI() {
        return WEconomy.getInstance().getServer().getPluginManager().getPlugin("PlaceHolderAPI") != null;
    }
}
