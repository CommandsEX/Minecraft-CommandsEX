package com.github.zathrus_writer.commandsex.api;

import java.util.HashMap;

import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;

public class NicknameAPI {

	/**
	 * Determines whether the Nicknames function has been enabled in the CommandsEX Builder
	 * @return
	 */
	
	public static boolean isNicknamesEnabled(){
		boolean toReturn;
		
		try {
			new Nicknames();
			toReturn =true;
		} catch (Exception e){
			toReturn = false;
		}
		
		return toReturn;
	}
	
	/**
	 * Returns the nickname prefix set in config.yml
	 * @return
	 */
	
	public static String getNicknamePrefix(){
		return CommandsEX.getConf().getString("nicknamePrefix");
	}
	
	/**
	 * Set a players nickname
	 * @param player The player to set the nickname for
	 * @param nickname The nickname to set the players nickname to
	 */
	
	public static void setNickname(Player player, String nickname){
		setNickname(player.getName(), nickname);
	}
	
	/**
	 * Set a players nickname
	 * @param player The player to set the nickname for
	 * @param nickname The nickname to set the players nickname to
	 */
	
	public static void setNickname(String player, String nickname){
		Nicknames.setNick(player, nickname);
	}
	
	/**
	 * Refresh's a players nickname in the event of another plugin over-riding it
	 * @param player The player of which to refresh the nickname of
	 */
	
	public static void refreshNickname(Player player){
		Nicknames.showNick(player);
	}
	
	/**
	 * Reset's a nickname back to their username
	 * @param player The player to reset the nickname of
	 */
	
	public static void resetNickname(Player player){
		resetNickname(player.getName());
	}
	
	/**
	 * Reset's a nickname back to their username
	 * @param player The player to reset the nickname of
	 */
	
	public static void resetNickname(String player){
		Nicknames.resetNick(player);
	}
	
	/**
	 * Gets a players nickname
	 * @param player The player to get the nickname of
	 */
	
	public static void getNickname(Player player){
		getNickname(player.getName());
	}
	
	/**
	 * Gets a players nickname
	 * @param player The player to get the nickname of
	 */
	
	public static void getNickname(String player){
		Nicknames.getRealNick(player);
	}
	
	/**
	 * Gets a players nickname with the nickname prefix
	 * @param player The player to get the prefixed nickname of
	 */
	
	public static void getPrefixedNickname(Player player){
		getPrefixedNickname(player.getName());
	}
	
	/**
	 * Gets a players nickname with the nickname prefix
	 * @param player The player to get the prefixed nickname of
	 */
	
	public static void getPrefixedNickname(String player){
		Nicknames.getNick(player);
	}
	
	/**
	 * Gets all players nicknames in a HashMap
	 * @return
	 */
	
	public static HashMap<String, String> getNicknames(){
		return Nicknames.nicknames;
	}
	
	/**
	 * Saves all nicknames to the CommandsEX database
	 */
	
	public static void saveNicknamesToDatabase(){
		Nicknames.saveNicks();
	}
	
}
