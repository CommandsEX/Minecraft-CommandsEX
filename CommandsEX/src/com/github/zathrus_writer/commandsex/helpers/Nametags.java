package com.github.zathrus_writer.commandsex.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.SQLManager;

public class Nametags implements Listener {

	/***
	 * Common methods used with nametags
	 * Most code from the Nicknames class
	 * @author iKeirNez
	 */

	// stores all nametags
	public static HashMap<String, String> nametags = new HashMap<String, String>();
	public static boolean tagAPIPresent = false;

	public Nametags(){
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
		// we can't restore nametags if the database is disabled
		if (!CommandsEX.sqlEnabled){
			return;
		}

		// create the tables and columns if they do not exist
		SQLManager.query((SQLManager.sqlType.equals("mysql") ? "" : "BEGIN; ") + "CREATE TABLE IF NOT EXISTS " + SQLManager.prefix + "nametags (player_name varchar(32) NOT NULL, nametag varchar(32) NOT NULL)" + (SQLManager.sqlType.equals("mysql") ? " ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='stores players nametags'" : "; COMMIT;"));

		// load database into HashMap
		try {
			ResultSet res = SQLManager.query_res("SELECT player_name, nametag FROM " + SQLManager.prefix + "nametags");
			while (res.next()){
				nametags.put(res.getString("player_name"), res.getString("nametag"));
			}

			res.close();
		} catch (SQLException ex){
			if (CommandsEX.getConf().getBoolean("debugMode")){
				ex.printStackTrace();
			}
		}
	}

	public static void init(CommandsEX plugin){
		Plugin pl = Bukkit.getPluginManager().getPlugin("TagAPI");
		if (pl != null && pl.isEnabled()){
			tagAPIPresent = true;
			new Nametags();
		}
	}

	@EventHandler
	public void onNameTag(org.kitteh.tag.PlayerReceiveNameTagEvent e){
		Player tagPlayer = e.getNamedPlayer();
		String pName = tagPlayer.getName();
		String tag = Nametags.getTag(pName);
		if (!tag.equals(pName)){
			tag = Utils.replaceChatColors(tag);
			e.setTag(tag);
		}
	}
	
	/***
	 * Set a players nametag
	 * @param pName
	 * @param nametag
	 */
	
	public static void setTag(String pName, String nametag){
		if (nametags.containsKey(pName)){
			nametags.remove(pName);
		}
		
		nametags.put(pName, nametag);
		Player player = Bukkit.getPlayerExact(pName);
		if (player != null){
			org.kitteh.tag.TagAPI.refreshPlayer(player);
		}
	}
	
	/**
	 * Refresh's a players nametag
	 * @param player The players nametag to be refreshed
	 */
	
	public static void refreshTag(Player player){
		org.kitteh.tag.TagAPI.refreshPlayer(player);
	}
	
	/***
	 * Function to reset a players nametag
	 * @param player
	 */
	
	public static void resetTag(Player player){
		String pName = player.getName();
		resetTag(pName);
	}
	
	/***
	 * Function to reset a players nametag
	 * @param pName
	 */
	
	public static void resetTag(String pName){
		if (nametags.containsKey(pName)){
			nametags.remove(pName);
		}
		
		Player player = Bukkit.getPlayerExact(pName);
		if (player != null){
			org.kitteh.tag.TagAPI.refreshPlayer(player);
		}
	}
	
	/***
	 * Function to get a players nametag
	 * @param player
	 * @return
	 */
	
	public static String getTag(Player player){
		return getTag(player.getName());
	}
	
	/***
	 * Function to get a players nickname
	 * @param pName
	 * @return
	 */
	
	public static String getTag(String pName){
		String nametag = pName;
		if (nametags.containsKey(pName)){
			nametag = nametags.get(pName) + ChatColor.RESET;
		}
		
		return nametag;
	}
		
	/***
	 * Function to save all players nametags from the HashMap to the database
	 */
	
	public static void saveTags(){
		// we can't save the nametags if the database is disabled
		if (CommandsEX.sqlEnabled){
			// delete rows from the database if the key is not in the HashMap, used for when a nametag is reset
			// and the row is no longer in the table
			try {
				ResultSet rs = SQLManager.query_res("SELECT player_name FROM " + SQLManager.prefix + "nametags");
				while (rs.next()){
					String rsName = rs.getString("player_name");
					if (!nametags.containsKey(rsName)){
						SQLManager.query("DELETE FROM " + SQLManager.prefix + "nametags WHERE player_name = ?", rsName);
					}
				}
			} catch (SQLException ex){
				if (CommandsEX.getConf().getBoolean("debugMode")){
					ex.printStackTrace();
				}
			}
			
			for (String pName : nametags.keySet()){
				String nametag = nametags.get(pName);
				
				// insert or replace nickname into the database
				try {
					ResultSet res = SQLManager.query_res("SELECT player_name, nametag FROM " + SQLManager.prefix + "nametags WHERE player_name = ?", pName);
					// if a nickname is already in the database, overwrite it otherwise create it
					if (!res.next()){
						SQLManager.query("INSERT " + (SQLManager.sqlType.equals("mysql") ? "" : "OR REPLACE ") + "INTO " + SQLManager.prefix + "nametags (player_name, nametag) SELECT ? AS player_name, ? AS nametag", pName, nametag);
					} else {
						SQLManager.query("UPDATE " + SQLManager.prefix + "nametags SET player_name = ?, nametag = ? WHERE player_name = ?", pName, nametag, pName);
					}
					res.close();
				} catch (SQLException e) {
				}
			}
		}
	}
}
