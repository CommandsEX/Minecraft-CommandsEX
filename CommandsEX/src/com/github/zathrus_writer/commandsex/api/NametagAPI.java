package com.github.zathrus_writer.commandsex.api;

import java.util.HashMap;

import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Nametags;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;

public class NametagAPI {

	public static boolean isNametagsEnabled(){
		try {
			new Nametags();
			return true;
		} catch (Exception e){
			return false;
		}
	}
	
	public static boolean isTagAPIEnabled(){
		return Nametags.tagAPIPresent;
	}
	
	/**
	 * Set a players nametag
	 * @param player The player to set the nickname for
	 * @param nametag The nickname to set the players nickname to
	 */
	
	public static void setnametag(Player player, String nametag){
		setNametag(player.getName(), nametag);
	}
	
	/**
	 * Set a players nametag
	 * @param player The player to set the nickname for
	 * @param nametag The nickname to set the players nickname to
	 */
	
	public static void setNametag(String player, String nametag){
		Nicknames.setNick(player, nametag);
	}
	
	/**
	 * Refresh's a players nametag
	 * @param player The player of which to refresh the nametag of
	 */
	
	public static void refreshNametag(Player player){
		Nametags.refreshTag(player);
	}
	
	/**
	 * Reset's a nametag back to their username
	 * @param player The player to reset the nametag of
	 */
	
	public static void resetNametag(Player player){
		resetNametag(player.getName());
	}
	
	/**
	 * Reset's a nametag back to their username
	 * @param player The player to reset the nametag of
	 */
	
	public static void resetNametag(String player){
		Nicknames.resetNick(player);
	}
	
	/**
	 * Gets a players nametag
	 * @param player The player to get the nametag of
	 */
	
	public static void getNametag(Player player){
		getNametag(player.getName());
	}
	
	/**
	 * Gets a players nametag
	 * @param player The player to get the nametag of
	 */
	
	public static void getNametag(String player){
		Nametags.getTag(player);
	}
	
	/**
	 * Gets all players nametags in a HashMap
	 * @return
	 */
	
	public static HashMap<String, String> getNametags(){
		return Nametags.nametags;
	}
	
	/**
	 * Saves all nametags to the CommandsEX database
	 */
	
	public static void saveNametagsToDatabase(){
		Nametags.saveTags();
	}

}
