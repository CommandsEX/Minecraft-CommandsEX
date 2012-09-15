package com.github.zathrus_writer.commandsex.api;

import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Common;

public class FreezeAPI {

	/**
	 * Is a player frozen or not
	 * @param player The player to check if frozen
	 * @return
	 */
	
	public static boolean isPlayerFrozen(Player player){
		return Common.frozenPlayers.contains(player.getName());
	}
	
	/**
	 * Toggles a player's frozen state, if frozen they will be unfrozen and vice versa
	 * @param player The player to toggle the frozen state of
	 */
	
	public static void toggleFrozen(Player player){
		// initialize event listeners if they have not been enabled already
		if (Common.plugin == null){
			new Common();
		}
		
		if (isPlayerFrozen(player)){
			Common.frozenPlayers.remove(player.getName());
		} else {
			Common.frozenPlayers.add(player.getName());
		}
	}
}
