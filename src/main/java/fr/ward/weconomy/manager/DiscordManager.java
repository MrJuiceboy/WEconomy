package fr.ward.weconomy.manager;

import fr.ward.weconomy.WEconomy;
import fr.ward.weconomy.discord.DiscordMessage;
import fr.ward.weconomy.utils.MineLogger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.Bukkit;

import javax.security.auth.login.LoginException;

public class DiscordManager {

    private JDA jda;

    public void load() {
        if(hasEnabled()) {
            loadBot();
        }
    }

    public void sendMessage(DiscordMessage discordMessage, String sender, String receiver, double amount) {
        if(hasEnabled()) {
            final String guildID = WEconomy.getInstance().getConfig().getString("discordServerID");
            final String canalID = WEconomy.getInstance().getConfig().getString("discordCanalID");

            if (guildID == null) {
                MineLogger.error("[Discord Bot] Discord server id not found in the config.yml");
                return;
            }

            final Guild guild = jda.getGuildById(guildID);

            if (guild == null) {
                MineLogger.error("[Discord Bot] Discord server id not found with this id " + guildID);
                return;
            }

            if (canalID == null) {
                MineLogger.error("[Discord Bot] canal id not found in the config.yml");
                return;
            }

            final TextChannel channel = guild.getTextChannelById(canalID);

            if (channel != null && channel.isSynced()) {
                final EmbedBuilder embedBuilder = discordMessage.buildEmbed(sender, receiver, (int) amount);
                channel.sendMessageEmbeds(embedBuilder.build()).queue();
                embedBuilder.clear();
            } else {
                MineLogger.error("[Discord Bot] Canal id not found in the server with this id " + canalID);
            }
        }
    }

    private void loadBot() {
        final String token = WEconomy.getInstance().getConfig().getString("discordBotToken");
        try {
            jda = JDABuilder.createLight(token)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
                    //.setActivity(Activity.playing("WEconomy V1"))
                    .disableCache(CacheFlag.ACTIVITY)
                    .setMemberCachePolicy(MemberCachePolicy.VOICE.or(MemberCachePolicy.OWNER))
                    .setChunkingFilter(ChunkingFilter.NONE)
                    .disableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_TYPING)
                    .setLargeThreshold(50)
                    .build();
            try {
                jda.awaitReady();
                SelfUser bot = jda.getSelfUser();
                MineLogger.info("[Discord Bot] Name: " + bot.getName());
                MineLogger.info("[Discord Bot] ID: " + bot.getId());
                MineLogger.info("[Discord Bot] Servers: " + jda.getGuilds().size());
            } catch (InterruptedException e) {
                handleException(e);
            }
        } catch (LoginException e) {
            handleException(e);
        }
    }

    private boolean hasEnabled(){
        return WEconomy.getInstance().getConfig().getBoolean("discordBotEnabled");
    }

    private void handleException(Exception e) {
        MineLogger.error("Exception occurred: " + e.getClass().getName());
        MineLogger.error("With message: " + e.getMessage());
        Bukkit.getServer().getPluginManager().disablePlugin(WEconomy.getInstance());
    }
}
