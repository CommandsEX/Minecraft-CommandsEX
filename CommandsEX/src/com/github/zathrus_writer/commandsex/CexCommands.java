package com.github.zathrus_writer.commandsex;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CexCommands {
	public final static Logger LOGGER = Logger.getLogger("Minecraft");

	// language constants
	public final static String langConfigUpdated = "Config updated, new value: ";
	public final static String langConfigReloaded = "CommandsEX config successfully reloaded.";
	public final static String langConfigUnrecognized = "Unrecognized config parameter.";
	public final static String langConfigNotEnoughParams = "Not enough parameters.";
	public final static String langConfigUnrecognizedAction = "Unrecognized action.";
	public final static String langConfigAvailableNodes = "Available options: ";
	public final static String langConfigAvailableNodesUsage = "Usage: /cex config get [option]";
	public final static String langConfigVersionDisableStatus = "Version displaying enabled: ";
	public final static String langConfigCommandsLoggingStatus = "Commands logging (to console) enabled: ";
	public final static String langConfigDisabledCommands = "List of disabled commands: ";
	public final static String langConfigUnspecifiedError1 = "An error has occured.";
	public final static String langConfigUnspecifiedError2 = "Please make sure you use this syntax to add or amend values: ";
	public final static String langConfigUnspecifiedError3 = ChatColor.WHITE + "/cex config set <option> [add/remove [value]]";
	public final static String langConfigStatusTrue = "true";
	public final static String langConfigStatusFalse = "false";
	
	
	/***
	 * Constructor, sets the main plugin class locally.
	 * @param plugin
	 */
	public static void init(CommandsEX p) {
		// load config file and write defaults in case the file doesn't exist
		p.getConfig().options().copyDefaults(true); p.saveConfig();

		// load list of ignored commands
		p.ignoredCommands = p.getConfig().getList("disabledCommands");
	}
	
	
	/***
	 * Handles reactions on the /cex command.
	 * @param sender
	 * @param alias
	 * @param args
	 * @return
	 */
	public static Boolean handle_cex(CommandsEX p, CommandSender sender, String alias, String[] args) {
		int aLength = args.length;

		// normalize arguments
		if (aLength > 0) {
			for (int i = 0; i < aLength; i++) {
				args[i] = args[i].toLowerCase();
			}
		}
		
		if (aLength == 0) {

			/***
			 * VERSION
			 */

			if (!p.getConfig().getBoolean("disableVersion")) {
				sender.sendMessage(ChatColor.YELLOW + CommandsEX.pdfFile.getName() + ", " + CommandsEX.langVersion + " " + CommandsEX.pdfFile.getVersion());
			}
		} else if ((aLength == 1) && args[0].equals("reload")) {

			/***
			 * RELOAD
			 */
			
			if (sender.getName().toLowerCase().equals("console") || ((sender instanceof Player) && CommandsEX.checkPerms((Player)sender, "cex.reload"))) {
				p.reloadConf();
				sender.sendMessage(ChatColor.GREEN + langConfigReloaded);
			} else {
				LOGGER.warning("["+ CommandsEX.pdfFile.getName() +"]: Player " + sender.getName() + " tried to execute reload command without permission!");
			}
		} else if ((aLength == 1) && (args[0].equals("?") || args[0].equals("help"))) {

			/***
			 * USAGE HELP REQUEST
			 */
			CommandsEX.showCommandHelpAndUsage(sender, "cex", "cex");
		} else if ((aLength < 3) && args[0].equals("config")) {
			
			/***
			 * SHOWING ALL AVAILABLE OPTIONS
			 */
			
			sender.sendMessage(ChatColor.WHITE + langConfigAvailableNodes + p.getConfig().getKeys(false).toString());
			sender.sendMessage(ChatColor.WHITE + langConfigAvailableNodesUsage);
		} else if (
					((aLength >= 3) && args[0].equals("config"))
					||
					((aLength >= 2) && (args[0].equals("cs") || args[0].equals("cg")))
				) {
			
			/***
			 * CONFIGURATION GETTING / SETTING
			 */
			
			if (!args[1].equals("get") && !args[1].equals("set") && !args[0].equals("cs") && !args[0].equals("cg")) {
				// unrecognized config action
				sender.sendMessage(ChatColor.RED + langConfigUnrecognizedAction);
			} else {
				if (args[1].equals("get") || args[0].equals("cg")) {
					
					/***
					 * GETTING CONFIG VALUES
					 */
					switch ((args[0].equals("cg") ? args[1].toLowerCase() : args[2].toLowerCase())) {
						case "disableversion":	sender.sendMessage(ChatColor.YELLOW + langConfigVersionDisableStatus + (!p.getConfig().getBoolean("disableVersion") ? ChatColor.GREEN + langConfigStatusTrue : ChatColor.RED + langConfigStatusFalse));
												break;
						
						case "logcommands":		sender.sendMessage(ChatColor.YELLOW + langConfigCommandsLoggingStatus + (p.getConfig().getBoolean("logCommands") ? ChatColor.GREEN + langConfigStatusTrue : ChatColor.RED + langConfigStatusFalse));
												break;
						
						case "disabledcommands":sender.sendMessage(ChatColor.YELLOW + langConfigDisabledCommands + ChatColor.WHITE + p.getConfig().getList("disabledCommands").toString());
												break;
												
						default:				sender.sendMessage(ChatColor.RED + langConfigUnrecognized);
												break;
					}
				} else if (args[1].equals("set") || args[0].equals("cs")) {
					
					/***
					 * SETTING CONFIG VALUES
					 */
					if ((args[0].equals("cs") && ((aLength == 4) || (aLength == 2))) || (aLength == 5)) {
						switch ((args[0].equals("cs") ? args[1].toLowerCase() : args[2].toLowerCase())) {
							case "disableversion":	p.getConfig().set("disableVersion", !p.getConfig().getBoolean("disableVersion"));
													p.saveConfig();
													sender.sendMessage(ChatColor.YELLOW + langConfigUpdated + (!p.getConfig().getBoolean("disableVersion") ? ChatColor.GREEN + langConfigStatusTrue : ChatColor.RED + langConfigStatusFalse));
													break;
							
							case "logcommands":		p.getConfig().set("logCommands", !p.getConfig().getBoolean("logCommands"));
													p.saveConfig();
													sender.sendMessage(ChatColor.YELLOW + langConfigUpdated + (p.getConfig().getBoolean("logCommands") ? ChatColor.GREEN + langConfigStatusTrue : ChatColor.RED + langConfigStatusFalse));
													break;
													
							case "disabledcommands":if (args[3].equals("add") || args[2].equals("add")) {
														List<Object> l = p.getConfig().getList("disabledCommands");
														String toAdd = args[2].equals("add") ? args[3] : args[4];
														if (!l.contains(toAdd)) {
															l.add(args[2].equals("add") ? args[3] : args[4]);
															p.getConfig().set("disabledCommands", l);
															p.saveConfig();
														}
														sender.sendMessage(ChatColor.YELLOW + langConfigUpdated + ChatColor.WHITE + p.getConfig().getList("disabledCommands").toString());
													} else if (args[3].equals("remove") || (args[2].equals("remove"))) {
														List<Object> l = p.getConfig().getList("disabledCommands");
														String toRemove = args[2].equals("remove") ? args[3] : args[4];
														if (l.contains(toRemove)) {
															l.remove(args[2].equals("remove") ? args[3] : args[4]);
															p.getConfig().set("disabledCommands", l);
															p.saveConfig();
														}
														sender.sendMessage(ChatColor.YELLOW + langConfigUpdated + ChatColor.WHITE + p.getConfig().getList("disabledCommands").toString());
													} else {
														sender.sendMessage(ChatColor.RED + langConfigUnspecifiedError1);
														sender.sendMessage(ChatColor.RED + langConfigUnspecifiedError2);
														sender.sendMessage(ChatColor.RED + langConfigUnspecifiedError3);
													}
													break;
													
							default:				sender.sendMessage(ChatColor.RED + langConfigUnrecognized);
													break;
						}
					} else {
						sender.sendMessage(ChatColor.RED + langConfigNotEnoughParams);
					}
				}
			}
		} else {
			
			/***
			 * UNRECOGNIZED
			 */
			
			sender.sendMessage(ChatColor.RED + langConfigUnrecognized);
		}
		
		return true;
	}
}
