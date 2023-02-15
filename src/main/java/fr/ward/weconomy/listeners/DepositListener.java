package fr.ward.weconomy.listeners;

import fr.ward.weconomy.WEconomy;
import fr.ward.weconomy.manager.EconomyManager;
import fr.ward.weconomy.manager.MessageManager;
import fr.ward.weconomy.utils.MineUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class DepositListener implements Listener {

    @EventHandler
    private void onClick(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItem();

        if(hasBagOrGift(player.getItemInHand()) && itemStack != null) {
            if ((event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                click(player, itemStack);
            }
        }
    }

    @EventHandler
    private void OnBlockPlace(BlockPlaceEvent event) {
        final ItemStack itemStack = event.getItemInHand();
        if(hasBagOrGift(itemStack)) event.setCancelled(true);
    }

    private void click(Player player, ItemStack itemStack) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return;
        final PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        final Double amount = dataContainer.get(new NamespacedKey(WEconomy.getInstance(), "WEconomy"), PersistentDataType.DOUBLE);
        if(amount == null) return;
        final EconomyManager economyManager = WEconomy.getInstance().getEconomyManager();
        economyManager.getEconomy().depositPlayer(player.getPlayer(), amount);
        MineUtils.sendMessage(player, MessageManager.RECEIVE_ITEM.toString()
                .replace("%amount%", String.valueOf(amount)), false);
        itemStack.setAmount(itemStack.getAmount() - 1);
    }

    private boolean hasBagOrGift(ItemStack itemStack) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return false;
        final PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        return dataContainer.has(new NamespacedKey(WEconomy.getInstance(), "WEconomy"), PersistentDataType.DOUBLE);
    }
}
