package fr.ward.weconomy.cache;

import java.util.UUID;

public class WPlayerCache {

    private final UUID uuid;
    private float money;

    public WPlayerCache(UUID uuid, float money) {
        this.uuid = uuid;
        this.money = money;
    }

    /*
    GETTER
     */

    public UUID getUuid() {
        return uuid;
    }

    public float getMoney() {
        return (float) (Math.round(money * 100.0) / 100.0);
    }

    /*
    ADDING
     */

    public void addMoney(float money) {
        this.money = getMoney() + money;
    }

    /*
    SETTER
     */

    public void setMoney(float money) {
        this.money = money;
    }
}

