package fr.ward.weconomy.cache;

import java.util.UUID;

public class WPlayerCache {

    private final UUID uuid;
    private double money;

    public WPlayerCache(UUID uuid, double money) {
        this.uuid = uuid;
        this.money = money;
    }

    /*
    GETTER
     */

    public UUID getUuid() {
        return uuid;
    }

    public double getMoney() {
        return money;
    }

    /*
    SETTER
     */

    public void setMoney(double money) {
        this.money = money;
    }
}

