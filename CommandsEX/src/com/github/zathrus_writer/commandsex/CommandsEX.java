package com.github.zathrus_writer.commandsex;

import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandsEX extends JavaPlugin {

	// regex to check if String is a number
	public static final String intRegex = "(-)?(\\d){1,10}(\\.(\\d){1,10})?";
	
	// language constants
	public static final String langInWorldCommandOnly = "This command can only be used in-world.";
	public static final String langTpNoCoords = "Please enter coordinates.";
	public static final String langTpMissingCoords = "Please enter all 3 teleport coordinates";
	public static final String langTpCoordsMustBeNumeric = "Teleport coordinates must be numeric.";
	public static final String langInternalError = "An internal error has occured. Please try again or contact an administrator.";
	public static final String langInsufficientPerms = "You don't have sufficient permissions to use this command.";
	public static final String langEnableMsg = " has successfully started.";
	public static final String langDisableMsg = " has been successfully disabled.";
	public static final String langVersion = "version";
	
	public void onEnable() {
		/***
		 * TPLOC
		 * teleports player to given coordinates
		 */
		getCommand("tploc").setExecutor(new CommandExecutor() {
            public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
            	if (cs instanceof Player) {
            		Player player = (Player)cs;

            		// check permissions before allowing player to teleport
                	if (!player.hasPermission("cex.tploc")) {
                		player.sendMessage(ChatColor.RED + langInsufficientPerms);
                        return true;
                    }

	                if (args.length <= 0) {
	                	// no coordinates
	                	player.sendMessage(ChatColor.RED + langTpNoCoords);
	                } else if (args.length != 3) {
	                	// too few or too many arguments
	                	player.sendMessage(ChatColor.RED + langTpMissingCoords);
	                	return false;
	                } else if (!args[0].matches(intRegex) || !args[1].matches(intRegex) || !args[2].matches(intRegex)) {
	                	// one of the coordinates is not a number
	                	player.sendMessage(ChatColor.RED + langTpCoordsMustBeNumeric);
	                } else {
	                	// all ok here, we can TP the player
	                	try {
	                		player.teleport(new Location(player.getWorld(), new Double(args[0]), new Double(args[1]), new Double(args[2])));
	                	} catch (Exception e) {
	                		player.sendMessage(ChatColor.RED + langInternalError);
	                		Logger.getLogger("Minecraft").severe("TPLOC returned an unexpected error for player " + player.getName() + ". Error message: " + e.getMessage());
	                		return false;
	                	}
	                }
            	} else {
            		cs.sendMessage(ChatColor.RED + langInWorldCommandOnly);
            	}
                return true;
            }
        });
		
		PluginDescriptionFile pdfFile = this.getDescription();
		Logger.getLogger("Minecraft").info(pdfFile.getName() + " " + langVersion + " " + pdfFile.getVersion() + langEnableMsg);
	}

	public void onDisable() {
		Logger.getLogger("Minecraft").info(this.getDescription().getName() + langDisableMsg);
	} 
}
