package fr.ward.weconomy.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.*;
import java.time.Instant;

public enum DiscordMessage {

    TRANSACTION("Transaction", "+", Color.GREEN),
    GIVE("Command Give", "+", Color.CYAN),
    REMOVE("Command Remove", "-", Color.YELLOW),
    RESET("Command Reset", null, Color.RED),
    ;

    private final String title;
    private final String field;
    private final Color color;

    DiscordMessage(String title, String field, Color color) {
        this.title = title;
        this.field = field;
        this.color = color;
    }

    public EmbedBuilder buildEmbed(String sender, String receiver, int amount) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(MarkdownUtil.bold(this.title));

        if(receiver != null) {
            embedBuilder.addField(receiver, this.field + amount, true);
        }

        embedBuilder.setFooter("Send by " + sender);
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setColor(this.color);
        return embedBuilder;
    }
}
