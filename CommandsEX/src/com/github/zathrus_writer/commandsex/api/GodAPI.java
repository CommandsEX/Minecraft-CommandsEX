package com.github.zathrus_writer.commandsex.api;

import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Common;

public class GodAPI {

	/**
	 * Checks whether a player has god mode enabled or not
	 * @param player The player to check has god mode
	 * @return
	 */
	
	public static boolean isPlayerGod(Player player){
		return Common.godPlayers.contains(player.getName());
	}
	
	/**
	 * Toggles a player's god state, if god they will be un-goded and vice versa
	 * @param player The player to toggle the god state of
	 */
	
	public static void toggleGod(Player player){
		// initialize event listeners if they have not already been enabled
		if (Common.plugin == null){
			new Common();
		}
		
		if (isPlayerGod(player)){
			Common.godPlayers.remove(player.getName());
		} else {
			Common.godPlayers.add(player.getName());
		}
	}
	
}
