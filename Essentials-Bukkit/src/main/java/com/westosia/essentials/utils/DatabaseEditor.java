package com.westosia.essentials.utils;

import com.westosia.databaseapi.database.DatabaseConnector;
import com.westosia.essentials.homes.Home;
import com.westosia.essentials.homes.HomeManager;
import com.westosia.westosiaapi.utils.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DatabaseEditor {

    private static final String DATABASE = "essentials";
    private static final String HOMES_TABLE = "homes";
    private static final String COLUMN_UUID = "uuid";
    private static final String COLUMN_HOME_NAME = "home_name";
    private static final String COLUMN_HOME_STRING = "home_string";
    private static final String NICKNAME_TABLE = "nicknames";
    private static final String COLUMN_NICKNAME = "nickname";


    public static void createTable() {
        //"CREATE DATABASE 'essentials'"
        try (Connection con = DatabaseConnector.getConnection(DATABASE)) {
            PreparedStatement ps = con.prepareStatement("CREATE TABLE " + HOMES_TABLE +
                    " (" + COLUMN_UUID + " varchar(255), " +
                    COLUMN_HOME_NAME + " varchar(255), " +
                    COLUMN_HOME_STRING + " text(255));");
            ps.execute();
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createNickTable() {
        try (Connection con = DatabaseConnector.getConnection(DATABASE)) {
            PreparedStatement ps = con.prepareStatement("CREATE TABLE " + NICKNAME_TABLE +
                    " (" + COLUMN_UUID + " varchar(36), " +
                    COLUMN_NICKNAME + " varchar(255));");
            ps.execute();
            ps.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkIfTableExists() {
        boolean exists = false;
        //TODO: check and create database & table code?
        try (Connection con = DatabaseConnector.getConnection(DATABASE)) {
            if (con == null) Logger.warning("con is null");
            PreparedStatement ps = con.prepareStatement("SELECT * FROM information_schema.tables WHERE table_schema = '" + DATABASE + "' AND table_name = '" + HOMES_TABLE + "' LIMIT 1;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                exists = true;
            }
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }

    public static boolean checkIfNickTableExists() {
        boolean exists = false;
        //TODO: check and create database & table code?
        try (Connection con = DatabaseConnector.getConnection(DATABASE)) {
            if (con == null) Logger.warning("con is null");
            PreparedStatement ps = con.prepareStatement("SELECT * FROM information_schema.tables WHERE table_schema = '" + DATABASE + "' AND table_name = '" + NICKNAME_TABLE + "' LIMIT 1;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                exists = true;
            }
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }


    public static Map<String, Home> getHomesInDB(UUID uuid) {
        Map<String, Home> homesInDB = new HashMap<>();
        try (Connection con = DatabaseConnector.getConnection(DATABASE)) {
            PreparedStatement ps = con.prepareStatement("SELECT " + COLUMN_HOME_NAME + ", " + COLUMN_HOME_STRING +
                    " FROM " + HOMES_TABLE +
                    " WHERE " + COLUMN_UUID + " = '" + uuid.toString() + "';");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String homeName = rs.getString(COLUMN_HOME_NAME);
                String homeString = rs.getString(COLUMN_HOME_STRING);
                homesInDB.put(homeName, HomeManager.fromString(homeString));
            }
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return homesInDB;
    }

    public static void saveAllHomes(UUID uuid) {
        // Player has left; send Redis message for all servers to unload and save to database
        Map<String, Home> currentHomes = new HashMap<>(HomeManager.getHomes(uuid));
        Map<String, Home> dbHomes = DatabaseEditor.getHomesInDB(uuid);
        // Get all homes from database that aren't in the current list. These need to be deleted
        dbHomes.forEach((dbHomeName, dbHome) -> {
            // Database home does not exist in current homes. Delete from database
            if (!currentHomes.containsKey(dbHomeName)) {
                DatabaseEditor.deleteHome(dbHome);
            }
        });

        // Determine if home needs to be updated or inserted into database
        for (Map.Entry<String, Home> homeEntry : currentHomes.entrySet()) {
            // Current home does not yet exist in database, insert it now
            boolean newEntry = false;
            if (dbHomes.isEmpty() || !dbHomes.containsKey(homeEntry.getKey())) {
                newEntry = true;
            }
            DatabaseEditor.saveHome(homeEntry.getValue(), newEntry);
            // Tell Redis to delete all current homes from cache
            //RedisConnector.getInstance().getConnection().publish(Main.getInstance().DEL_HOME_REDIS_CHANNEL, homeEntry.getValue().toString());
        }
    }

    public static void saveHome(Home home, boolean newEntry) {
        UUID uuid = home.getOwner().getUniqueId();
        String sql = "UPDATE " + HOMES_TABLE +
                " SET " + COLUMN_HOME_STRING + " = '" + home.toString() +
                "' WHERE " + COLUMN_UUID + " = '" + uuid.toString() +
                "' AND " + COLUMN_HOME_NAME + " = '" + home.getName() + "';";
        if (newEntry) {
            sql = "INSERT INTO " + HOMES_TABLE + " VALUES ('" + uuid.toString() + "', '" +
                    home.getName() + "', '" + home.toString() + "');";
        }
        try (Connection con = DatabaseConnector.getConnection(DATABASE)) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.execute();
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteHome(Home home) {
        UUID uuid = home.getOwner().getUniqueId();
        try (Connection con = DatabaseConnector.getConnection(DATABASE)) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM " + HOMES_TABLE +
                    " WHERE " + COLUMN_UUID + " = '" + uuid.toString() +
                    "' AND " + COLUMN_HOME_NAME + " = '" + home.getName() + "';");
            ps.execute();
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveNick(String nick, UUID uuid) {
        try (Connection con = DatabaseConnector.getConnection(DATABASE)) {
            PreparedStatement ps = con.prepareStatement("INSERT INTO nicknames (uuid, nickname) VALUES ('" + uuid.toString() + "', '" + nick + "') ON DUPLICATE KEY UPDATE nickname = '" + nick + "';"
            );
            ps.execute();
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static String getNick(UUID uuid) {
        String nickname = "";
        try (Connection con = DatabaseConnector.getConnection(DATABASE)) {
            PreparedStatement ps = con.prepareStatement("SELECT nickname FROM nicknames WHERE uuid = '" + uuid.toString() + "' LIMIT 1;"
            );
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                nickname = rs.getString("nickname");
            }
            ps.close();
            rs.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nickname;
    }

    public static void removeNick(UUID uuid) {
        try (Connection con = DatabaseConnector.getConnection(DATABASE)) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM nicknames WHERE uuid = '" + uuid.toString() + "';"
            );
            ps.execute();
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
