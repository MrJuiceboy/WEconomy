package fr.ward.weconomy.manager;

import org.bukkit.configuration.file.FileConfiguration;

public enum MessageManager {

    PREFIX("messages.prefix"),
    GET_MONEY("messages.getMoney"),
    NEGATIVE_AMOUNT("messages.negativeAmount"),
    PLAYER_NOT_FOUND("messages.playerNotFound"),
    INSUFFICIENT_FUNDS("messages.insufficientFunds"),
    PAY_YOURSELF("messages.payYourself"),
    SEND_PAY("messages.paySend"),
    RECEIVER_PAY("messages.payReceive"),
    SEND_GIVE("messages.giveSend"),
    RECEIVER_GIVE("messages.giveReceive"),
    SEND_REMOVE("messages.removeSend"),
    RECEIVER_REMOVE("messages.removeReceive"),
    BAL_TOP_ONLINE("messages.balTopOnline"),
    BAL_TOP_OFFLINE("messages.balTopOffline"),
    BAL_TOP_NOT_FUNDS("messages.balTopNotFound"),
    RESET_ALL("messages.resetAll"),
    INVENTORY_FULL("messages.inventoryFull"),
    RECEIVE_ITEM("messages.receiveByItem"),
    MINIMAL_AMOUNT_DEPOT("messages.minimalAmountDeposit"),
    MAXIMUM_AMOUNT_DEPOT("messages.maximalAmountDeposit"),
    BAG_DEPOSIT("messages.bagDeposit"),
    GIFT_DEPOSIT("messages.giftDeposit"),
    BAG_DISABLED("messages.bagDisabled"),
    GIFT_DISABLED("messages.giftDisabled"),
    NO_PERMISSION("messages.noPermission"),
    RELOAD("messages.reload"),
    UNKNOWN_COMMAND("messages.unknownCommand"),
    ;

    private final String configPath;
    private String value = "Not Loaded! Please contact administrator!";

    MessageManager(String configPath) {
        this.configPath = configPath;
    }

    public static void build(FileConfiguration config) {
        for(MessageManager messageManager : MessageManager.values()){
            messageManager.value = config.getString(messageManager.configPath);
        }
    }

    @Override
    public String toString() { return this.value; }
}
