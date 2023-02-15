package fr.ward.weconomy.discord;

import fr.ward.weconomy.utils.DiscordWebhook;

import java.awt.*;
import java.io.IOException;

public enum DiscordMessage {

    TRANSACTION("Transaction", "+", Color.GREEN),
    GIVE("Command Give", "+", Color.CYAN),
    REMOVE("Command Remove", "-", Color.YELLOW),
    RESET("Command Reset", null, Color.RED),
    BAG("Command Bag", null, Color.DARK_GRAY),
    GIFT("Command Gift", null, Color.MAGENTA),
    ;

    private final String title;
    private final String field;
    private final Color color;

    DiscordMessage(String title, String field, Color color) {
        this.title = title;
        this.field = field;
        this.color = color;
    }

    public void buildEmbed(DiscordWebhook webhook, String sender, String receiver, double amount) {
        if(receiver != null) {
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setTitle("**" + this.title + "**")
                    .addField(receiver, this.field + amount, true)
                    .setFooter("Send by " + sender, "")
                    .setColor(this.color));
        } else {
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setTitle("**" + this.title + "**")
                    .setFooter("Send by " + sender, "")
                    .setColor(this.color));
        }

        try {
            webhook.execute();
        } catch (IOException ignored) {}
    }
}
