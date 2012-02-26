package com.github.zathrus_writer.commandsex;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/***
 * Contains set of commands to be executed for teleportation purposes.
 * @author zathrus-writer
 *
 */
public class Teleportation {
	public final static Logger LOGGER = Logger.getLogger("Minecraft");

	// language constants
	public final static String langTpNoCoords = "Please enter coordinates.";
	public final static String langTpMissingCoords = "Please enter all 3 teleport coordinates";
	public final static String langTpCoordsMustBeNumeric = "Teleport coordinates must be numeric.";
	public final static String langTpInvalidArgument = "Coordinates must be a comma-separated value (e.g. 0,0,0)";
	public final static String langTpTooManyArguments = "Please use max 2 player names.";
	public final static String langTpInvalidPlayer = "Couldn't find the requested player...";
	public final static String langTpCannotTeleportSelf = "A player cannot be teleported to himself...";
	
	// the CommandsEx plugin
	public static CommandsEX plugin;
	

	/***
	 * Constructor, sets the main plugin class locally.
	 * @param plugin
	 */
	public static void init(CommandsEX p) {
		plugin = p;
	}
	
	
	/***
	 * TPCOMMON - used as base method for teleporting players one to another
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean tp_common(CommandSender sender, String[] args, String command, String alias) {
		Player player = (Player)sender;
		// check the number of arguments
		int aLength = args.length;
		if (aLength > 2) {
			player.sendMessage(ChatColor.RED + langTpTooManyArguments);
			return true;
		} else if (aLength == 0) {
			CommandsEX.showCommandHelpAndUsage(sender, "cex_" + command, alias);
			return true;
		}

		// check if given players are online
		Player player1 = Bukkit.getServer().getPlayer(args[0]);
		Player player2;
		if (aLength > 1) {
			player2 = Bukkit.getServer().getPlayer(args[1]);
		} else {
			// set the command sender as second player in case we need them later (for /tpto)
			player2 = player;
		}

		if ((player1 == null) || (player2 == null)) {
			player.sendMessage(ChatColor.RED + langTpInvalidPlayer);
			return true;
		}
		
		// also, we cannot teleport player to himself
		if (player1.getName().equals(player2.getName())) {
			player.sendMessage(ChatColor.RED + langTpCannotTeleportSelf);
			return true;
		}
		
		/***
		 * Teleportation fun starts here :-P
		 */
		if ((command.equals("tp") && (aLength == 1)) || command.equals("tpto")) {
			// simple tp to another person
			player2.teleport(player1);
		} else if ((command.equals("tphere")) || (command.equals("tp") && (aLength > 1))) {
			// teleporting another player to our position OR first player to second player (via arguments)
			player1.teleport(player2);
		}
        return true;
	}
}