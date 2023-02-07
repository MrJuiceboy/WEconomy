package fr.ward.weconomy.manager;

import fr.ward.weconomy.config.ConfigType;
import fr.ward.weconomy.discord.DiscordMessage;
import fr.ward.weconomy.utils.DiscordWebhook;
import fr.ward.weconomy.utils.MineLogger;
import org.bukkit.configuration.file.FileConfiguration;

public class DiscordManager {

    public void load() {
        if(hasEnabled()) {
            MineLogger.info("The Discord webhook is activated!");
        }
    }

    public void sendMessage(DiscordMessage discordMessage, String sender, String receiver, double amount) {
        final FileConfiguration config = ConfigType.DISCORD.getGeneratedYML().getConfig();
        final String url = config.getString("Discord.webHook-URL");

        if(!hasEnabled()) return;

        if(url == null || url.isEmpty()) {
            MineLogger.error("The webHook-URL in discord.yml is empty or invalid");
        }

        final DiscordWebhook webhook = new DiscordWebhook(url);
        discordMessage.buildEmbed(webhook, sender, receiver, amount);
    }

    private boolean hasEnabled(){
        return ConfigType.DISCORD.getGeneratedYML().getConfig().getBoolean("Discord.enabled");
    }
}
