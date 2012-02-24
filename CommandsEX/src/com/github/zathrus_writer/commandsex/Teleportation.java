package com.github.zathrus_writer.commandsex;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
	public final static String langTpNoTeleporters = "Please enter the name of the player to teleport.";
	public final static String langTpInvalidPlayer = "Couldn't find the requested player...";
	public final static String langTpCannotTeleportSelf = "A player cannot be teleported to himself...";
	

	/***
	 * Constructor, sets the main plugin class locally.
	 * @param plugin
	 */
	public static void init(CommandsEX p) {
		// nothing to do :-)
	}
	
	
	/***
	 * TPCOMMON - used as base method for teleporting players one to another
	 * @param sender
	 * @param args
	 * @param command
	 * @return
	 */
	public static Boolean tp_common(Player player, String[] args, String command) {
		// check the number of arguments
		int aLength = args.length;
		if (aLength > 2) {
			player.sendMessage(ChatColor.RED + langTpTooManyArguments);
			return true;
		} else if (aLength == 0) {
			player.sendMessage(ChatColor.RED + langTpNoTeleporters);
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
	
	
	/***
	 * TP - teleports one player to another (either via arguments or the command sender to the player given)
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean command_tp(CommandSender sender, String alias, String[] args) {
		if (CommandsEX.checkIsPlayer(sender)) {
			Player player = (Player)sender;

			// check permissions and call to action
			if (args.length > 1) {
				// teleporting players one to another
				if (CommandsEX.checkPerms(player)) {
					tp_common(player, args, "tp");
				}
			} else {
				// teleporting sender to another player
				if (CommandsEX.checkPerms(player, "OR", "cex.tp", "cex.tpto")) {
					tp_common(player, args, "tp");
				}
			}
		}
        return true;
	}
	
	
	/***
	 * TPHERE - teleports another player to the sender
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean command_tphere(CommandSender sender, String alias, String[] args) {
		if (CommandsEX.checkIsPlayer(sender)) {
			Player player = (Player)sender;

			// check permissions and call to action
			if (CommandsEX.checkPerms(player, "OR", "cex.tp", "cex.tphere")) {
				tp_common(player, args, "tphere");
			}
		}
        return true;
	}


	/***
	 * TPTO - teleports sender to another player
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean command_tpto(CommandSender sender, String alias, String[] args) {
		if (CommandsEX.checkIsPlayer(sender)) {
			Player player = (Player)sender;

			// check permissions and call to action
			if (CommandsEX.checkPerms(player, "OR", "cex.tp", "cex.tpto")) {
				tp_common(player, args, "tp");
			}
		}
        return true;
	}
	
	
	/***
	 * TPLOC - teleports player to given coordinates
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean command_tploc(CommandSender sender, String alias, String[] args) {
		if (CommandsEX.checkIsPlayer(sender)) {
			Player player = (Player)sender;

			// first of all, check permissions
			if (CommandsEX.checkPerms(player)) {
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
		        } else if (!args[0].matches(CommandsEX.intRegex) || !args[1].matches(CommandsEX.intRegex) || !args[2].matches(CommandsEX.intRegex)) {
		        	// one of the coordinates is not a number
		        	player.sendMessage(ChatColor.RED + langTpCoordsMustBeNumeric);
		        } else {
		        	// all ok here, we can TP the player
		        	try {
		        		player.teleport(new Location(player.getWorld(), new Double(args[0]), new Double(args[1]), new Double(args[2])));
		        	} catch (Exception e) {
		        		player.sendMessage(ChatColor.RED + CommandsEX.langInternalError);
		        		LOGGER.severe("TPLOC returned an unexpected error for player " + player.getName() + ". Error message: " + e.getMessage());
		        		return false;
		        	}
		        }
			}
		}
        return true;
	}
}