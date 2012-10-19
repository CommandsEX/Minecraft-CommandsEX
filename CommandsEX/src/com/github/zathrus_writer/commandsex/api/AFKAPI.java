package com.github.zathrus_writer.commandsex.api;

import com.github.zathrus_writer.commandsex.helpers.AFK;

/**
 * The AFK API
 */

public class AFKAPI {

	/**
	 * Sets a player as AFK
	 * @param player The player to set AFK
	 */
	
	public static void setPlayerAFK(String player){
		AFK.setAfk(player);
	}
	
	/**
	 * Sets a player as not AFK
	 * @param player The player to set not AFK
	 */
	
	public static void setPlayerNotAFK(String player){
		AFK.setNotAfk(player);
	}
	
	/**
	 * Toggles a players AFK status
	 * @param player The player to toggle the AFK status for
	 */
	
	public static void togglePlayerAFK(String player){
		AFK.toggleAfk(player);
	}
	
	/**
	 * Gets a players idle time in seconds
	 * @param player The player to get the idle time of
	 * @return The players idle time, in seconds. Returns -1 if the player does not have an idle time
	 */
	
	public static long getIdleTime(String player){
		if (AFK.playerIdleTime.containsKey(player)){
			return AFK.playerIdleTime.get(player) / 1000;
		} else {
			return -1;
		}
	}
	
	/**
	 * Resets a players idle time
	 * @param player The player to reset the idle time for
	 */
	
	public static void resetPlayerIdleTime(String player){
		AFK.resetIdleTime(player);
	}
	
	/**
	 * Checks all players idle times and sets them AFK if they have been idle for long enough
	 */
	
	public static void checkPlayerIdleTimes(){
		AFK.checkPlayerIdleTimes();
	}
	
}
