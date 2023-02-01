package fr.ward.weconomy.database.type;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.ward.weconomy.WEconomy;
import fr.ward.weconomy.utils.MineLogger;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

public class MySQL extends Database {

    private HikariDataSource hikariDataSource;

    private final String tableName = WEconomy.getInstance().getConfig().getString("databaseTable");

    public MySQL(WEconomy wEconomy) {
        super(wEconomy);
    }

    public String MySQLCreateTokensTable = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
            "`UUID` varchar(36) NOT NULL UNIQUE," +
            "`money` float(24) NOT NULL" +
            ");";

    @Override
    public Connection getSQLConnection() throws SQLException {
        if(this.hikariDataSource == null){
            MineLogger.warning(ChatColor.RED + "[Database] Not connected...");
            setupHikariCP();
        }
        return this.hikariDataSource.getConnection();
    }

    private void setupHikariCP() {
        final HikariConfig hikariConfig = new HikariConfig();
        final FileConfiguration fileConfiguration = WEconomy.getInstance().getConfig();

        final String databaseHost = fileConfiguration.getString("databaseHost");
        final int databasePort = fileConfiguration.getInt("databasePort");
        final String databaseName = fileConfiguration.getString("databaseName");
        final String databaseUser = fileConfiguration.getString("databaseUser");
        final String databasePass = fileConfiguration.getString("databasePass");

        final int maximumPoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;

        hikariConfig.setPoolName(WEconomy.getInstance().getName() + "-" + databaseName);
        hikariConfig.setJdbcUrl(toURL(databaseHost, databasePort, databaseName));
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setUsername(databaseUser);
        hikariConfig.setPassword(databasePass);
        hikariConfig.setMinimumIdle(Math.min(maximumPoolSize, 10));
        hikariConfig.setMaxLifetime(TimeUnit.MINUTES.toMillis(30L));
        hikariConfig.setConnectionTimeout(TimeUnit.SECONDS.toMillis(10L));
        hikariConfig.setMaximumPoolSize(maximumPoolSize);

        MineLogger.info(ChatColor.GREEN + "[Database] Is now connected !");

        this.hikariDataSource = new HikariDataSource(hikariConfig);
    }

    private String toURL(String host, int port, String dbName) {
        final StringBuilder sb = new StringBuilder();

        sb.append("jdbc:mysql://")
                .append(host)
                .append(":")
                .append(port)
                .append("/")
                .append(dbName);
        return sb.toString();
    }

    @Override
    public void load() throws SQLException {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(MySQLCreateTokensTable);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
}
