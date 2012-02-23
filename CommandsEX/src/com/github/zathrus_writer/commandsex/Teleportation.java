package com.github.zathrus_writer.commandsex;

import java.lang.reflect.Method;
import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/***
 * Contains set of commands to be executed for teleportation purposes.
 * @author zathrus-writer
 *
 */
public class Teleportation {
	private static CommandsEX plugin;

	// language constants
	public final static String langTpNoCoords = "Please enter coordinates.";
	public final static String langTpMissingCoords = "Please enter all 3 teleport coordinates";
	public final static String langTpCoordsMustBeNumeric = "Teleport coordinates must be numeric.";
	public final static String langTpInvalidArgument = "Coordinates must be a comma-separated value (e.g. 0,0,0)";
	
	// list of methods in this class that should not be counted in as command functions
	private final String exFunctions[] = {};
	

	/***
	 * Constructor, sets the main plugin class locally.
	 * @param plugin
	 */
	public Teleportation(CommandsEX p) {
		plugin = p;

		// set command listeners for all available commands and add these into main class for reference
		Method[] methods = this.getClass().getDeclaredMethods();
		for (Method m : methods) {
			String n = m.getName().toLowerCase();
			if (Arrays.binarySearch(this.exFunctions, n) < 0) {
				plugin.getCommand(n).setExecutor(plugin);
				plugin.commands.add(n + ":" + this.getClass().getSimpleName());
			}
		}
	}
	
	
	
	/***
	 * TPLOC - teleports player to given coordinates
	 * @param player
	 * @param args
	 * @return
	 */
	public static Boolean tploc(Player player, String[] args) {
		// check permissions before allowing player to teleport
    	if (!plugin.checkPerms(player)) {
    		player.sendMessage(ChatColor.RED + plugin.langInsufficientPerms);
            return true;
        }

    	// alternative usage, all 3 coords separated by comma in 1 argument
    	if (args.length == 1) {
        	if (args[0].contains(",")) {
        		args = args[0].split(",");
        	} else {
        		// no commas found in the argument, return error
        		player.sendMessage(ChatColor.RED + langTpInvalidArgument);
        		return false;
        	}
        }
    	
        if (args.length <= 0) {
        	// no coordinates
        	player.sendMessage(ChatColor.RED + langTpNoCoords);
        } else if (args.length != 3) {
        	// too few or too many arguments
        	player.sendMessage(ChatColor.RED + langTpMissingCoords);
        	return false;
        } else if (!args[0].matches(plugin.intRegex) || !args[1].matches(plugin.intRegex) || !args[2].matches(plugin.intRegex)) {
        	// one of the coordinates is not a number
        	player.sendMessage(ChatColor.RED + langTpCoordsMustBeNumeric);
        } else {
        	// all ok here, we can TP the player
        	try {
        		player.teleport(new Location(player.getWorld(), new Double(args[0]), new Double(args[1]), new Double(args[2])));
        	} catch (Exception e) {
        		player.sendMessage(ChatColor.RED + plugin.langInternalError);
        		plugin.l.severe("TPLOC returned an unexpected error for player " + player.getName() + ". Error message: " + e.getMessage());
        		return false;
        	}
        }
        return true;
	}
}