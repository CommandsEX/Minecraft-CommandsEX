package com.github.ikeirnez.commandsex;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

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
    private DBType dbType;
    private boolean connected = false;
    
    /**
     * Create a new SQLITE database connection
     * @param databaseName The name of the database, default commandsex
     * @param prefix All tables created by CommandsEX will be prefix with this, default cex_
     */
    public Database(String databaseName, String prefix){
        dbType = DBType.SQLITE;
        
        try {
         // this will throw an error if for some reason the JDBC class is unavailable
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + p.getDataFolder() + File.separatorChar + databaseName + ".db");
            connected = true;
        } catch (Exception e){
            LogHelper.addExceptionToEventLog(e);
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
            LogHelper.addExceptionToEventLog(e);
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
}