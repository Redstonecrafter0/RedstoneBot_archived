package net.redstonecraft.redstonebot;

import java.sql.*;

public class SQL {

    private Connection connection;
    private Statement statement;

    public SQL(String dbname) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbname);
            statement = connection.createStatement();
            statement.setQueryTimeout(30);
        } catch(SQLException e) {
            Main.getLogger().severe(e.getMessage());
        }
    }

    public void update(String sql) {
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            Main.getLogger().warning(e.getMessage());
        }
    }

    public ResultSet query(String sql) {
        try {
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            Main.getLogger().warning(e.getMessage());
        }
        return null;
    }

}
