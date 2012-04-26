package com.github.zathrus_writer.commandsex.handlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.commands.Command_cex_back;

public class Handler_savebackposition implements Listener {
	
	// contains a list of players whose positions should not be stored
	// ... this is used for instance when putting people into jails and such
	public static List<String> omittedPlayers = new ArrayList<String>();
	
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
	public void saveBackPosition(PlayerTeleportEvent e) {
		String pName = e.getPlayer().getName();
		if (e.isCancelled() || omittedPlayers.contains(pName)) return;
		Command_cex_back.lastLocations.put(pName, e.getFrom());
	}
	
}
