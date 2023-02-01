package fr.ward.weconomy.database.type;

import fr.ward.weconomy.WEconomy;
import fr.ward.weconomy.utils.MineLogger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite extends Database {

    private final String dbName = WEconomy.getInstance().getConfig().getString("databaseName");
    private final String tableName = WEconomy.getInstance().getConfig().getString("databaseTable");

    public SQLite(WEconomy wEconomy){
        super(wEconomy);
    }

    public String SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
            "`UUID` varchar(36) NOT NULL UNIQUE," +
            "`money` float(24) NOT NULL" +
            ");";

    @Override
    public Connection getSQLConnection() {
        File dataFolder = new File(WEconomy.getInstance().getDataFolder(), dbName + ".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                MineLogger.error("File write error: " + dbName + ".db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            MineLogger.error("SQLite exception on initialize " + ex);
        } catch (ClassNotFoundException ex) {
            MineLogger.error("You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    @Override
    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(SQLiteCreateTokensTable);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
}
