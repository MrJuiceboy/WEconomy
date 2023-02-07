package fr.ward.weconomy.manager;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public enum MessageListManager {

    HELP("messages.help"),
    HELP_ADMIN("messages.helpAdmin");

    private final String configPath;
    private List<String> value = List.of("Not Loaded! Please contact administrator!");

    MessageListManager(String configPath) { this.configPath = configPath; }

    public static void build(FileConfiguration config){
        for(MessageListManager messageListManager : MessageListManager.values()){
            messageListManager.value = config.getStringList(messageListManager.configPath);
        }
    }

    public List<String> toStringList() { return this.value; }
}
