package fr.ward.weconomy.database.type;

import fr.ward.weconomy.WEconomy;

import java.sql.Connection;

public class MySQL extends Database {

    public MySQL(WEconomy wEconomy) {
        super(wEconomy);
    }

    @Override
    public Connection getSQLConnection() {
        return null;
    }

    @Override
    public void load() {

    }
}
