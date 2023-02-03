package fr.ward.weconomy.database.type;

import fr.ward.weconomy.WEconomy;
import fr.ward.weconomy.database.DatabaseType;
import fr.ward.weconomy.manager.MessageManager;
import fr.ward.weconomy.utils.MineLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public abstract class Database {

    Connection connection;
    private final String tableName;

    public Database(WEconomy wEconomy) {
        this.tableName = wEconomy.getConfig().getString("databaseTable");
    }

    public abstract Connection getSQLConnection() throws SQLException;

    public abstract void load() throws SQLException;

    public void initialize() throws SQLException {
        connection = getSQLConnection();
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE UUID = ?");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
        } catch (SQLException ex) {
            MineLogger.error("Unable to retreive connection " + ex);
        }
    }

    public synchronized void addIntoDatabase(OfflinePlayer player) {
        final DatabaseType databaseType = WEconomy.getInstance().getDatabaseType();
        switch (databaseType) {
            case SQLITE -> execute("INSERT OR IGNORE INTO " + tableName + " VALUES(?,?)", player.getUniqueId().toString(), 0);
            case MYSQL -> execute("INSERT IGNORE INTO " + tableName + " VALUES(?,?)", player.getUniqueId().toString(), 0);
        }
    }

    public synchronized Integer getMoney(UUID uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + tableName + " WHERE UUID = '" + uuid + "';");

            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("UUID").equalsIgnoreCase(uuid.toString())){
                    return rs.getInt("money");
                }
            }
        } catch (SQLException ex) {
            MineLogger.error("" + ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                MineLogger.error("" + ex);
            }
        }
        return 0;
    }

    public synchronized void setMoney(UUID uuid, double money) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO " + tableName + " (UUID,money) VALUES(?,?)");
            ps.setString(1, uuid.toString());
            ps.setFloat(2, (float) money);
            ps.executeUpdate();
        } catch (SQLException ex) {
            MineLogger.error("" + ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                MineLogger.error("" + ex);
            }
        }
    }

    public synchronized String getTop(int place) {
        Connection conn = null;
        PreparedStatement ps = null;
        String[] TopPlayer = new String[2];
        int i = 1;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + tableName + " ORDER BY money DESC");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if(i == place) {
                    TopPlayer[0] = rs.getString("UUID");
                    double money = rs.getFloat("money");
                    TopPlayer[1] = Double.toString(money);
                }
                i++;
            }

            if(TopPlayer[0] != null) {
                final Player player = Bukkit.getPlayer(UUID.fromString(TopPlayer[0]));
                if(player != null) {
                    return MessageManager.BAL_TOP.build(place, player.getName(), Double.parseDouble(TopPlayer[1]));
                }
            }
            return MessageManager.BAL_TOP_NOT_FUNDS.build(place, null, 0);
        } catch (SQLException ex) {
            MineLogger.error("" + ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                MineLogger.error("" + ex);
            }
        }
        return MessageManager.BAL_TOP_NOT_FUNDS.build(place, null, 0);
    }

    public void reset() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("DELETE FROM " + tableName);
            ps.executeUpdate();
        } catch (SQLException ex) {
            MineLogger.error("" + ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                MineLogger.error("" + ex);
            }
        }
    }

    public synchronized void execute(String sql, Object... replacements) {
        try {
            Connection c = getSQLConnection();
            try {
                PreparedStatement statement = c.prepareStatement(sql);
                try {
                    if (replacements != null)
                        for (int i = 0; i < replacements.length; i++)
                            statement.setObject(i + 1, replacements[i]);
                    statement.execute();
                    statement.close();
                } catch (Throwable throwable) {
                    if (statement != null)
                        try {
                            statement.close();
                        } catch (Throwable throwable1) {
                            throwable.addSuppressed(throwable1);
                        }
                    throw throwable;
                }
                c.close();
            } catch (Throwable throwable) {
                if (c != null)
                    try {
                        c.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                throw throwable;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            MineLogger.error("" + ex);
        }
    }
}
