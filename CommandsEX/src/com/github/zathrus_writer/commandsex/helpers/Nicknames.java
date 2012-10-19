package com.github.zathrus_writer.commandsex.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.SQLManager;

public class Nicknames implements Listener {

	/***
	 * Nicknames - A collection of methods to handle nicknames
	 * @author iKeirNez
	 */
	
	// holds all player names and nicknames
	public static HashMap<String, String> nicknames = new HashMap<String, String>();
	
	public Nicknames(){
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	
	public static void init(CommandsEX plugin){
		// register the events
		new Nicknames();
		// we can't restore nicknames if the database is disabled
		if (!CommandsEX.sqlEnabled){
			return;
		}
		
		// create the tables and columns if they do not exist
		SQLManager.query((SQLManager.sqlType.equals("mysql") ? "" : "BEGIN; ") + "CREATE TABLE IF NOT EXISTS " + SQLManager.prefix + "nicknames (player_name varchar(32) NOT NULL, nickname varchar(32) NOT NULL)" + (SQLManager.sqlType.equals("mysql") ? " ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='stores players nicknames'" : "; COMMIT;"));
		
		// load database into HashMap
		try {
			ResultSet res = SQLManager.query_res("SELECT player_name, nickname FROM " + SQLManager.prefix + "nicknames");
			while (res.next()){
				nicknames.put(res.getString("player_name"), res.getString("nickname"));
			}
			
			res.close();
		} catch (SQLException ex){
			if (CommandsEX.getConf().getBoolean("debugMode")){
				ex.printStackTrace();
			}
		}
		
		// add shutdown functions
		CommandsEX.onDisableFunctions.add("com.github.zathrus_writer.commandsex.helpers.Nicknames#####onDisable");
	}
	
	/**
	 * Saves all nicknames to the database when the server is shutdown
	 */
	
	public static void onDisable(CommandsEX p){
		saveNicks();
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		Player player = e.getPlayer();
		showNick(player);
	}
	
	/***
	 * Set a players nickname
	 * @param pName
	 * @param nickname
	 */
	
	public static void setNick(String pName, String nickname){
		if (nicknames.containsKey(pName)){
			nicknames.remove(pName);
		}
		
		nicknames.put(pName, nickname);
		
		// if the player is online, set their nickname
		Player player = Bukkit.getPlayerExact(pName);
		if (player != null){
			showNick(player);
		}
	}
	
	/***
	 * Shows a players nickname
	 * @param player
	 * @param nickname
	 */
	
	public static void showNick(Player player){
		String nickname = getNick(player.getName());
		
		// set their display name a TAB list name
		player.setDisplayName(nickname + ChatColor.RESET);
		player.setPlayerListName(nickname + ChatColor.RESET);
	}
	
	/***
	 * Function to reset a players nickname
	 * @param player
	 */
	
	public static void resetNick(Player player){
		String pName = player.getName();
		if (nicknames.containsKey(pName)){
			nicknames.remove(pName);
		}
		
		// set their display name and TAB list name to their player name
		player.setDisplayName(pName);
		player.setPlayerListName(pName);
	}
	
	/***
	 * Function to reset a players nickname
	 * @param pName
	 */
	
	public static void resetNick(String pName){
		if (nicknames.containsKey(pName)){
			nicknames.remove(pName);
		}
	}
	
	/***
	 * Function to get a players nickname
	 * @param player
	 * @return
	 */
	
	public static String getNick(Player player){
		return getNick(player.getName());
	}
	
	/***
	 * Function to get a players nickname
	 * @param pName
	 * @return
	 */
	
	public static String getNick(String pName){
		String nickname = pName;
		if (nicknames.containsKey(pName)){
			nickname = Utils.replaceChatColors(CommandsEX.getConf().getString("nicknamePrefix")) + getRealNick(pName) + ChatColor.RESET;
		}
		
		return nickname;
	}
	
	/***
	 * Function to get a players real nickname, without the nicknamePrefix (default ~)
	 * @param pName
	 * @return
	 */
	
	public static String getRealNick(String pName){
		// their default nickname is their player name
		String nickname = pName;

		if (nicknames.containsKey(pName)){
			nickname = Utils.replaceChatColors(nicknames.get(pName));
		}

		return nickname;
	}
	
	/***
	 * Function to save all players nicknames from the HashMap to the database
	 */
	
	public static void saveNicks(){
		// we can't save the nicknames if the database is disabled
		if (CommandsEX.sqlEnabled){
			// delete rows from the database if the key is not in the HashMap, used for when a nickname is reset
			 // and the row is no longer in the table
			 try {
				 ResultSet rs = SQLManager.query_res("SELECT player_name FROM " + SQLManager.prefix + "nicknames ");
				 
				 if (rs != null){
					 while (rs.next()){
						 String rsName = rs.getString("player_name");
						 if (!Nicknames.nicknames.containsKey(rsName)){
							 SQLManager.query("DELETE FROM " + SQLManager.prefix + "nicknames WHERE player_name = ?", rsName);
						 }
					 }
				 }
				 
				 rs.close();
			 } catch (SQLException ex){
				 if (CommandsEX.getConf().getBoolean("debugMode")){
					 ex.printStackTrace();
				 }
			 }

			 for (String pName : Nicknames.nicknames.keySet()){
				 String nickname = Nicknames.nicknames.get(pName);

				 // insert or replace nickname into the database
				 try {
					 ResultSet res = SQLManager.query_res("SELECT player_name, nickname FROM " + SQLManager.prefix + "nicknames WHERE player_name = ?", pName);
					 // if a nickname is already in the database, overwrite it otherwise create it
					 if (!res.next()){
						 SQLManager.query("INSERT " + (SQLManager.sqlType.equals("mysql") ? "" : "OR REPLACE ") + "INTO " + SQLManager.prefix + "nicknames (player_name, nickname) SELECT ? AS player_name, ? AS nickname", pName, nickname);
					 } else {
						 SQLManager.query("UPDATE " + SQLManager.prefix + "nicknames SET player_name = ?, nickname = ? WHERE player_name = ?", pName, nickname, pName);
					 }
					 res.close();
				 } catch (SQLException e) {
				 }
			 }
		}
	}
}