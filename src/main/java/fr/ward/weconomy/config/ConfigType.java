package fr.ward.weconomy.config;

import fr.ward.weconomy.WEconomy;
import fr.ward.weconomy.utils.GeneratedYML;

public enum ConfigType {

    DATABASE(new GeneratedYML("database", WEconomy.getInstance())),
    MESSAGE(new GeneratedYML("message", WEconomy.getInstance())),
    DISCORD(new GeneratedYML("discord", WEconomy.getInstance())),

    ;

    private final GeneratedYML generatedYML;

    ConfigType(GeneratedYML generatedYML) {
        this.generatedYML = generatedYML;
    }

    public GeneratedYML getGeneratedYML() {
        return generatedYML;
    }
}
