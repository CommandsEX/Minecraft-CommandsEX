package com.github.zathrus_writer.commandsex.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.SQLManager;

public class Nicknames implements Listener {

	/***
	 * Nicknames - A collection of methods to handle nicknames
	 */
	
	public Nicknames(){
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	
	public static void init(CommandsEX plugin){
		// we can't restore nicknames if the database is disabled
		if (!CommandsEX.sqlEnabled){
			return;
		}
		
		// create the tables and columns if they do not exist
		SQLManager.query((SQLManager.sqlType.equals("mysql") ? "" : "BEGIN; ") + "CREATE TABLE IF NOT EXISTS " + SQLManager.prefix + "nicknames (player_name varchar(32) NOT NULL, nickname varchar(32) NOT NULL)" + (SQLManager.sqlType.equals("mysql") ? " ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='stores players nicknames'" : "; COMMIT;"));
		
		// register events
		new Nicknames();
	}
	
	/***
	 * Set a players nickname
	 * @param pName
	 * @param nickname
	 */
	
	public static void setNick(String pName, String nickname){
		// we can't save the nickname if the database is disabled, but we won't disable this feature completely
		if (CommandsEX.sqlEnabled){
			// insert or replace nickname into the database
			ResultSet res = SQLManager.query_res("SELECT player_name, nickname FROM " + SQLManager.prefix + "nicknames WHERE player_name = ?", pName);
			
			try {
				// if a nickname is already in the database, overwrite it otherwise create it
				if (!res.next()){
					SQLManager.query("INSERT " + (SQLManager.sqlType.equals("mysql") ? "" : "OR REPLACE ") + "INTO " + SQLManager.prefix + "nicknames (player_name, nickname) SELECT ? AS player_name, ? AS nickname", pName, nickname);
				} else {
					SQLManager.query("UPDATE " + SQLManager.prefix + "nicknames SET player_name = ?, nickname = ? WHERE player_name = ?", pName, nickname, pName);
				}
			} catch (SQLException e) {
				if (CommandsEX.getConf().getBoolean("debugMode")){
					e.printStackTrace();
				}
			}
		}
		
		// if the player is online, set their nickname
		Player player = Bukkit.getPlayerExact(pName);
		if (!player.equals(null)){
			showNick(player, nickname);
		}
	}
	
	/***
	 * Shows a players nickname
	 * @param player
	 */
	
	public static void showNick(Player player){
		showNick(player, getNick(player));
	}
	
	/***
	 * Shows a players nickname
	 * @param player
	 * @param nickname
	 */
	
	public static void showNick(Player player, String nickname){
		// skip if nickname is equal to the players name
		if (nickname.equals(player.getName())){
			return;
		}
		
		// allow colours in nicknames
		nickname = Utils.replaceChatColors(nickname) + ChatColor.RESET;
		String prefix = CommandsEX.getConf().getString("nicknamePrefix");
		if (prefix.equals(null)){
			prefix = "";
		}
		
		// add the prefix
		nickname = prefix + nickname;
		
		// set their display name a TAB list name
		player.setDisplayName(nickname);
		player.setPlayerListName(nickname);
	}
	
	/***
	 * Function to reset a players nickname
	 * @param player
	 */
	
	public static void resetNick(Player player){
		String pName = player.getName();
		
		// set their display name and TAB list name to their player name
		player.setDisplayName(pName);
		player.setPlayerListName(pName);
		
		// remove the entry from the database
		SQLManager.query("DELETE FROM " + SQLManager.prefix + "nicknames WHERE player_name = ?", pName);
	}
	
	/***
	 * Function to get a players nickname
	 * @param player
	 * @return
	 */
	
	public static String getNick(Player player){
		// their default nickname is their player name
		String nickname = player.getName();

		if (CommandsEX.sqlEnabled){
			// get results from db
			ResultSet res = SQLManager.query_res("SELECT player_name, nickname FROM " + SQLManager.prefix + "nicknames WHERE player_name = ?", player.getName());

			try {
				if (res.next()){
					nickname = res.getString("nickname");
				}

				res.close();
			} catch (SQLException e) {
				if (CommandsEX.getConf().getBoolean("debugMode")){
					e.printStackTrace();
				}
			}
		}
		
		return nickname;
	}
	
}
