package com.github.zathrus_writer.commandsex.handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.commands.Command_cex_back;

public class Handler_savebackposition implements Listener {
	
	/***
	 * Tells our main class which function we want to execute on PlayerTeleportEvent.
	 * @param plugin
	 */
	public Handler_savebackposition() {
		// check if /back command is present and skip this handler if it isn't
		try {
			Class.forName("com.github.zathrus_writer.commandsex.commands.Command_cex_back");
			CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
		} catch (Throwable e) {}
	}
	
	/***
	 * The main function which stores last known player location on a teleport event.
	 * @param e
	 * @return
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public static Boolean saveBackPosition(PlayerTeleportEvent e) {
		Command_cex_back.lastLocations.put(e.getPlayer().getName(), e.getFrom());
		return true;
	}
	
}
