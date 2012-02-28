package com.github.zathrus_writer.commandsex;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandsEX extends JavaPlugin {

	// our plugin :-)
	public static CommandsEX plugin;
	
	// regex to check if String is a number
	public final static String intRegex = "(-)?(\\d){1,10}(\\.(\\d){1,10})?";
	
	// logger
	public final static Logger LOGGER = Logger.getLogger("Minecraft");
	
	// language constants
	public final static String langInWorldCommandOnly = "This command can only be used in-world.";
	public final static String langInsufficientPerms = "You don't have sufficient permissions to use this command.";
	public final static String langEnableMsg = " has successfully started.";
	public final static String langDisableMsg = " has been disabled.";
	public final static String langVersion = "version";
	public final static String langUsage = "Usage";
	public final static String langInternalError = "An internal error has occured. Please try again or contact an administrator.";
	
	// list of commands this plugin should ignore - values come from a config file
	public List<Object> ignoredCommands = new ArrayList<Object>();
	
	// plugin description file, used at least on 2 places, so it's here :-P
	public static PluginDescriptionFile pdfFile;
	

	/***
	 * class constructor
	 */
	public CommandsEX() {
		plugin = this;
	}
	
	/***
	 * Checks if the CommandSender is a Player, gives the sender error message if he's not and returns Boolean value.
	 * @param cs
	 * @return
	 */
	public static Boolean checkIsPlayer(CommandSender cs) {
		if (cs instanceof Player) {
			return true;
		}

		cs.sendMessage(ChatColor.RED + langInWorldCommandOnly);
		return false;
	}
	
	
	/***
	 * Check whether given player has required permission.
	 * @param player
	 * @param customPerm
	 * @return
	 */
	public static Boolean checkPerms(Player player, String... customPerm) {
		// if we have custom permissions to check, the behaviour is as follows...
		// the first parameter MUST BE either "AND" or "OR" (exception = when only 1 node is being checked)
		// ... this will allow us to see if we should check whether the player has either one
		// of these permissions (OR) or whether they have all of them (ALL) and return result
		// accordingly. Every other parameter is a permission node itself.
		Boolean hasPerms = false;
		int cLength = customPerm.length;
		if (cLength == 1) {
			// only a single node is being checked
			hasPerms = player.hasPermission(customPerm[0]);
		} else if (cLength > 1) {
			// multiple nodes check
			if (customPerm[0].equals("OR") || customPerm[0].equals("AND")) {
				for (int i = 1; i < cLength; i++) {
					hasPerms = player.hasPermission(customPerm[i]);

					// only 1 permission node must be present, check if this one can pull it off
					if (customPerm[0].equals("OR") && hasPerms) {
						return true;
					}
					
					// all permissions must be true if we're handling "AND", check it here
					if (customPerm[0].equals("AND") && !hasPerms) {
						player.sendMessage(ChatColor.RED + langInsufficientPerms);
						return false;
					}
				}
			} else {
				hasPerms = false;
				LOGGER.severe("Custom permissions check failed for method '" + Thread.currentThread().getStackTrace()[3].getMethodName() + "' (first parameter is not one of: AND/OR - it was '" + customPerm[0] + "')");
			}
		} else {
			hasPerms = false;
			LOGGER.severe("Permissions check failed for method '" + Thread.currentThread().getStackTrace()[3].getMethodName() + "' (no paramaters seem to be present)");
		}
		
		if (!hasPerms) {
			player.sendMessage(ChatColor.RED + langInsufficientPerms);
		}
		
		return hasPerms;
	}


	/***
	 * OnEnable
	 */
	@Override
	public void onEnable() {
		pdfFile = this.getDescription();
		LOGGER.info("[" + pdfFile.getName() + "] " + langVersion + " " + pdfFile.getVersion() + langEnableMsg);
	}

	/***
	 * OnDisable
	 */
	@Override
	public void onDisable() {
		LOGGER.info("[" + this.getDescription().getName() + "] " + langDisableMsg);
	}
	
	/***
	 * NOTE: there is some room for improvement here, since iterating list of all commands isn't the quickest of searches
	 * ... this looks interesting but also a little too much as an overkill: http://www.roseindia.net/java/example/java/util/PartialSearcher.shtml
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String cmdAlias, String[] args) {
		String cmd = command.getName().toLowerCase();
		String alias = cmdAlias.toLowerCase();
		
		// first of all - check if this command shouldn't be ignored by our plugin
		if (this.ignoredCommands.contains(cmd) || this.ignoredCommands.contains(alias)) {
			return true;
		}
		
		// log the command if invoked by a player
		if ((sender instanceof Player) && (getConfig().getBoolean("logCommands") == true)) {
			String arguments = " ";
			if (args.length > 0) {
				for (String a : args) {
					arguments = arguments + a + " ";
				}
			}
			LOGGER.info("[" + sender.getName() + "] /" + alias + arguments);
		}

		try {
			// the only exception to dynamic calls for now - calling /cex command directly from this class
			if (cmd.equals("cex")) {
				return this.command_cex(sender, cmdAlias, args);
			} else {
				Class<?>[] proto = new Class[] {CommandSender.class, String.class, String[].class};
				Object[] params = new Object[] {sender, cmdAlias, args};
				Class<?> c = Class.forName("com.github.zathrus_writer.commandsex.commands.Command_" + cmd);
				Method method = c.getDeclaredMethod("run", proto);
				Object ret = method.invoke(null, params);
				return Boolean.TRUE.equals(ret);
			}
		} catch (Throwable e) {
			sender.sendMessage(ChatColor.RED + langInternalError);
			LOGGER.severe("Couldn't handle function call '" + cmd + "', error returned: " + e.getMessage());
    		return true;
    	}
	}
	
	
	/***
	 * Handles control over to CexCommands section,
	 * so the class can use our base class for managing configuration.
	 * @param sender
	 * @param alias
	 * @param args
	 * @return
	 */
	public Boolean command_cex(CommandSender sender, String alias, String[] args) {
		return CexCommands.handle_cex(this,	sender, alias, args);
	}
	
	
	/***
	 * Simply does what it advertises :-D
	 * @return
	 */
	public Boolean reloadConf() {
		reloadConfig();
		return true;
	}

	
	/***
	 * Gets help text and usage for a command and returns it in a 2-dimensional array for help purposes.
	 * @param commandName
	 * @return
	 */
	public static void showCommandHelpAndUsage(CommandSender sender, String commandName, String alias) {
		List<Command> cmdList = PluginCommandYamlParser.parse(plugin);
		for(int i = 0; i <= cmdList.size() - 1; i++) {
			if (cmdList.get(i).getLabel().equals(commandName)) {
				sender.sendMessage(ChatColor.WHITE + cmdList.get(i).getDescription());
				String usage = cmdList.get(i).getUsage().replaceAll("<command>", alias);
				if (usage.contains("\n") || usage.contains("\r")) {
					usage.replaceAll("\r", "\n").replaceAll("\n\n", "");
					String[] splitted = usage.split("\n");
					sender.sendMessage(ChatColor.WHITE + langUsage + ":");
					for (String rVal : splitted) {
						sender.sendMessage(ChatColor.WHITE + rVal);
					}
				} else {
					sender.sendMessage(ChatColor.WHITE + langUsage + ": " + usage);
				}
			}
	    }
	}
}