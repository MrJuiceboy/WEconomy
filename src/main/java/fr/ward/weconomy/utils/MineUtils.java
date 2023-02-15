package fr.ward.weconomy.utils;

import com.cryptomorin.xseries.XMaterial;
import fr.ward.weconomy.WEconomy;
import fr.ward.weconomy.config.ConfigType;
import fr.ward.weconomy.manager.MessageListManager;
import fr.ward.weconomy.manager.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.text.DecimalFormat;
import java.util.*;

public class MineUtils {

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String getPrefix(){
        return color(MessageManager.PREFIX.toString());
    }

    public static void sendMessage(Player player, String message, boolean console){
        if(console && !WEconomy.getInstance().getConfig().getBoolean("options.console-message")) return;
        player.sendMessage(color(message).replace("%prefix%", getPrefix()));
    }

    public static UUID checkUUID(String uuid) {
        int length = uuid.length();
        if(length < 32) {
            return Bukkit.getOfflinePlayer(uuid).getUniqueId();
        }
        return UUID.fromString(uuid);
    }

    public static ItemStack bagItem(String playerName, double amount) {
        final String name = color(ConfigType.MESSAGE.getGeneratedYML().getConfig().getString("bag-item.name"));
        final ItemStack skull = new ItemStack(XMaterial.PLAYER_HEAD.parseMaterial(), 1, (byte) 3);
        final SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setDisplayName(name);
        skullMeta.setOwner("MrSnowDK");
        skullMeta.setLore(getMessageInLore(MessageListManager.BAG_LORE, playerName, amount));
        final PersistentDataContainer dataContainer = skullMeta.getPersistentDataContainer();
        dataContainer.set(new NamespacedKey(WEconomy.getInstance(), "WEconomy"), PersistentDataType.DOUBLE, amount);
        skull.setItemMeta(skullMeta);
        return skull;
    }

    public static ItemStack giftItem(String playerName, double amount) {
        final String[] nameList = {"CruXXx", "SeerPotion"};
        final Random random = new Random();
        final String name = color(ConfigType.MESSAGE.getGeneratedYML().getConfig().getString("gift-item.name"));
        final ItemStack skull = new ItemStack(XMaterial.PLAYER_HEAD.parseMaterial(), 1, (byte) 3);
        final SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setDisplayName(name);
        skullMeta.setOwner(nameList[random.nextInt(nameList.length)]);
        skullMeta.setLore(getMessageInLore(MessageListManager.GIFT_LORE, playerName, amount));
        final PersistentDataContainer dataContainer = skullMeta.getPersistentDataContainer();
        dataContainer.set(new NamespacedKey(WEconomy.getInstance(), "WEconomy"), PersistentDataType.DOUBLE, amount);
        skull.setItemMeta(skullMeta);
        return skull;
    }

    public static boolean isInventoryFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    public static DecimalFormat getDecimalFormat(){
        return new DecimalFormat("###,###,###,###,###.##");
    }

    private static List<String> getMessageInLore(MessageListManager loreListManager, String playerName, double amount) {
        final List<String> listMessage = new ArrayList<>();
        for(String message : loreListManager.toStringList()) {
            listMessage.add(color(message)
                    .replace("%player%", playerName)
                    .replace("%amount%", String.valueOf(amount)));
        }
        return listMessage;
    }
}
