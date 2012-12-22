package com.github.ikeirnez.commandsex;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.ikeirnez.commandsex.helpers.LogHelper;

public class Database {

    public enum DBType {
        SQLITE(0),
        MYSQL(1);

        private int code;

        private DBType(int code){
            this.code = code;
        }

        public int getDBTypeID(){
            return code;
        }
    }

    private CommandsEX p = CommandsEX.plugin;
    private transient Connection conn;
    private String prefix = "cex_";
    private String databaseName;
    private DBType dbType;
    private boolean connected = false;

    /**
     * Create a new SQLITE database connection
     * @param databaseName The name of the database, default commandsex
     * @param prefix All tables created by CommandsEX will be prefix with this, default cex_
     */
    public Database(String databaseName, String prefix){
        dbType = DBType.SQLITE;
        this.databaseName = databaseName;
        
        try {
            // this will throw an error if for some reason the JDBC class is unavailable
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + p.getDataFolder() + File.separatorChar + databaseName + ".db");
            connected = true;
        } catch (Exception e){
            e.printStackTrace();
            LogHelper.logSevere("Error while connecting to SQLITE database " + databaseName);
        }

        this.prefix = prefix;
    }

    /**
     * Create a new MYSQL database connection
     * @param databaseName The name of the database, default commandsex
     * @param username The username to the database, default root
     * @param password The password to the database, default ""
     * @param host The host/address to the database, default localhost
     * @param port The port of the database, default 3306
     * @param prefix All tables created by CommandsEX will be prefix with this, default cex_
     */
    public Database(String databaseName, String username, String password, String host, String port, String prefix){
        dbType = DBType.MYSQL;

        try {
            // this will throw an error if for some reason the Driver class is unavailable
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + databaseName, username, password);
            connected = true;
        } catch (Exception e){
            e.printStackTrace();
            LogHelper.logSevere("Error while connecting to MYSQL database " + databaseName);
        }

        this.prefix = prefix;
    }

    /**
     * Is the database connected or did it fail?
     * @return Is the database connected
     */
    public boolean isConnected(){
        return connected;
    }

    /**
     * Gets the type of database this is
     * @return The type of database this is
     */
    public DBType getType(){
        return dbType;
    }

    /**
     * Gets the database tables prefix
     * @return The database tables prefix
     */
    public String getPrefix(){
        return prefix;
    }

    /**
     * Executes a query on the database that does not return a ResultSet
     * If %prefix% is used in the query, it will be replaced with the query
     * 
     * @param query The query to execute
     * @param params Additional parameters needed
     * @return Did the query execute successfully?
     */
    public boolean query(String query, Object... params){
        if (!connected){
            LogHelper.logSevere("Could not run query because database is not connected QUERY = " + query);
            return false;
        }
        
        query = query.replaceAll("%prefix%", prefix);

        if (params.length == 0){
            try {
                Statement statement = conn.createStatement();
                statement.executeUpdate(query);
                statement.close();
            } catch (Exception e){
                e.printStackTrace();
                LogHelper.logSevere("Error while writing to database, QUERY = " + query);
            }
        } else {
            // if we have only 1 parameter that is an ArrayList, make an array of objects out of it
            // this allows us to pass in params in a List easily
            if ((params.length == 1) && ((params[0] instanceof List) || (params[0] instanceof ArrayList))) {
                params = ((List<?>) params[0]).toArray();
            }

            try {
                PreparedStatement prep = conn.prepareStatement(query);
                for (int i = 0; i < params.length; i++) {
                    Object o = params[i];

                    if (o instanceof Integer) {
                        prep.setInt(i, (Integer)o);
                    } else if (o instanceof String) {
                        prep.setString(i, (String)o);
                    } else if (o instanceof Double) {
                        prep.setDouble(i, (Double)o);
                    } else if (o instanceof Float) {
                        prep.setFloat(i, (Float)o);
                    } else if (o instanceof Long) {
                        prep.setLong(i, (Long)o);
                    } else if (o instanceof Boolean) {
                        prep.setBoolean(i, (Boolean)o);
                    } else if (o instanceof Date) {
                        prep.setTimestamp(i, new Timestamp(((Date) o).getTime()));
                    } else if (o instanceof Timestamp) {
                        prep.setTimestamp(i, (Timestamp) o);
                    } else if (o == null) {
                        prep.setNull(i, 0);
                    } else {
                        // unhandled variable type
                        LogHelper.logSevere(query);
                        LogHelper.logSevere("Unhandled variable when writing to database");
                        LogHelper.logSevere(o.toString());

                        prep.clearBatch();
                        prep.close();
                        return false;
                    }
                }

                prep.addBatch();
                conn.setAutoCommit(false);
                prep.executeBatch();
                conn.commit();
                prep.close();
                prep = null;
            } catch (Exception e) {
                e.printStackTrace();
                LogHelper.logSevere("Error while writing to the database " + databaseName + " QUERY = " + query);
                return false;
            }
        }

        return true;
    }

    /**
     * Executes a query and returns a ResultSet
     * If %prefix% is used in the query, it will be replaced with the query
     * 
     * @param query The query to execute
     * @param params Additional parameters needed
     * @return The ResultSet, null if failed
     */
    public ResultSet query_res(String query, Object... params){
        if (!connected){
            LogHelper.logSevere("Could not run query because database is not connected");
            LogHelper.logSevere(query);
            return null;
        }
        
        query = query.replaceAll("%prefix%", prefix);

        if (params.length == 0){
            try {
                Statement statement = conn.createStatement();
                ResultSet res = statement.executeQuery(query);
                return res;
            } catch (Exception e){
                e.printStackTrace();
                LogHelper.logSevere("Error while writing to the database " + databaseName + " QUERY = " + query);
            }
        } else {
            // if we have only 1 parameter that is an ArrayList, make an array of objects out of it
            // this allows us to pass in params in a List easily
            if ((params.length == 1) && ((params[0] instanceof List) || (params[0] instanceof ArrayList))) {
                params = ((List<?>) params[0]).toArray();
            }

            try {
                PreparedStatement prep = conn.prepareStatement(query);
                for (int i = 0; i < params.length; i++) {
                    Object o = params[i];

                    if (o instanceof Integer) {
                        prep.setInt(i, (Integer)o);
                    } else if (o instanceof String) {
                        prep.setString(i, (String)o);
                    } else if (o instanceof Double) {
                        prep.setDouble(i, (Double)o);
                    } else if (o instanceof Float) {
                        prep.setFloat(i, (Float)o);
                    } else if (o instanceof Long) {
                        prep.setLong(i, (Long)o);
                    } else if (o == null) {
                        prep.setNull(i, 0);
                    } else {
                        // unhandled variable type
                        LogHelper.logSevere(query);
                        LogHelper.logSevere("Unhandled variable when writing to database");
                        LogHelper.logSevere(o.toString());
                        
                        prep.close();
                        return null;
                    }
                }
                
                return prep.executeQuery();
            } catch (Exception e){
                e.printStackTrace();
                LogHelper.logSevere("Error while writing to the database " + databaseName + " QUERY = " + query);
                return null;
            }
        }
        
        return null;
    }
    
    /**
     * Closes the database connection if it is connected
     */
    public void close(){
        if (connected){
            try {
                conn.close();
            } catch (Exception e){}
        }
    }
}