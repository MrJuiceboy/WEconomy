package fr.ward.weconomy.placeholder;

import fr.ward.weconomy.WEconomy;
import fr.ward.weconomy.database.type.Database;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SomeExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "weconomy";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Ward";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.0.1";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        final Database database = WEconomy.getInstance().getDatabaseManager().getDatabase();

        return switch (params) {
            case "current" -> String.valueOf(WEconomy.getInstance().getCacheManager().getBalance(player));
            case "top_1" -> database.getTop(1);
            case "top_2" -> database.getTop(2);
            case "top_3" -> database.getTop(3);
            case "top_4" -> database.getTop(4);
            case "top_5" -> database.getTop(5);
            case "top_6" -> database.getTop(6);
            case "top_7" -> database.getTop(7);
            case "top_8" -> database.getTop(8);
            case "top_9" -> database.getTop(9);
            case "top_10" -> database.getTop(10);
            default -> ChatColor.RED + "Error : PlaceHolderAPI";
        };
    }
}
