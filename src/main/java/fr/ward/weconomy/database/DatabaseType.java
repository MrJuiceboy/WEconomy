package fr.ward.weconomy.database;

import fr.ward.weconomy.database.type.MySQL;
import fr.ward.weconomy.database.type.SQLite;

public enum DatabaseType {
    SQLITE(SQLite.class),
    MYSQL(MySQL.class);

    private final Class<?> clazz;

    DatabaseType(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> get() {
        return clazz;
    }
}
