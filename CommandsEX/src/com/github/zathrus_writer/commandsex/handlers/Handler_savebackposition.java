package com.github.zathrus_writer.commandsex.handlers;

import org.bukkit.event.player.PlayerTeleportEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.commands.Command_cex_back;
import com.github.zathrus_writer.commandsex.listeners.PlayerTeleportListener;

public class Handler_savebackposition {
	
	/***
	 * Tells our main class which function we want to execute on PlayerTeleportEvent.
	 * @param plugin
	 */
	public static void init(CommandsEX plugin) {
		// check if /back command is present and skip this handler if it isn't
		try {
			Class.forName("com.github.zathrus_writer.commandsex.commands.Command_cex_back");
		} catch (Throwable e) {
			// exit here if the command doesn't exist
			return;
		}

		PlayerTeleportListener.plugin.addEvent("normal", "savebackposition", "saveBackPosition");
	}
	
	/***
	 * The main function which stores last known player location on a teleport event.
	 * @param e
	 * @return
	 */
	public static Boolean saveBackPosition(PlayerTeleportEvent e) {
		Command_cex_back.lastLocations.put(e.getPlayer().getName(), e.getFrom());
		return true;
	}
	
}
