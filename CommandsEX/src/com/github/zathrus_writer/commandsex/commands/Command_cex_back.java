package com.github.zathrus_writer.commandsex.commands;


import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.handlers.Handler_savebackposition;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Teleportation;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_back {
	
	public static Map<String, Location> lastLocations = new HashMap<String, Location>();
	
	public static class DelayedBackLocationRemoval implements Runnable {
    	private Player p;
    	
    	public DelayedBackLocationRemoval(Player p) {
    		this.p = p;
    	}
    	
    	public void run() {
    		if (lastLocations.containsKey(p)) {
    			lastLocations.remove(p);
    		}
    	}
    }
	
	/***
	 * BACK - teleports player to the last know location
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player = (Player)sender;
			
			if (!Utils.checkCommandSpam(player, "tp-back") && Permissions.checkPerms(player, "cex.back")) {
				// check if we have last position saved
				String pName = player.getName();
				if (lastLocations.containsKey(pName)) {
					try {Handler_savebackposition.omittedPlayers.add(pName);} catch (Throwable e) {}
					// teleport player to the location
					Teleportation.delayedTeleport(player, lastLocations.get(pName), new DelayedBackLocationRemoval(player));
				} else {
					// player's last location not present
					LogHelper.showWarning("tpLastLocationUnknown", sender);
				}
			}
		}
        return true;
	}
}
