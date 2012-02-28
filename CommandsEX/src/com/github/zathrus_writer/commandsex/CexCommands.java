package com.github.zathrus_writer.commandsex;

import static com.github.zathrus_writer.commandsex.CommandsEX._;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CexCommands {
	public final static Logger LOGGER = Logger.getLogger("Minecraft");
	
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
				sender.sendMessage(ChatColor.YELLOW + CommandsEX.pdfFile.getName() + ", " + _("version") + " " + CommandsEX.pdfFile.getVersion());
			}
		} else if ((aLength == 1) && args[0].equals("reload")) {

			/***
			 * RELOAD
			 */
			
			if (sender.getName().toLowerCase().equals("console") || ((sender instanceof Player) && CommandsEX.checkPerms((Player)sender, "cex.reload"))) {
				p.reloadConf();
				sender.sendMessage(ChatColor.GREEN + _("configReloaded"));
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
			
			sender.sendMessage(ChatColor.WHITE + _("configAvailableNodes") + p.getConfig().getKeys(false).toString());
			sender.sendMessage(ChatColor.WHITE + _("configAvailableNodesUsage"));
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
				sender.sendMessage(ChatColor.RED + _("configUnrecognizedAction"));
			} else {
				if (args[1].equals("get") || args[0].equals("cg")) {
					
					/***
					 * GETTING CONFIG VALUES
					 */
					switch ((args[0].equals("cg") ? args[1].toLowerCase() : args[2].toLowerCase())) {
						case "disableversion":	sender.sendMessage(ChatColor.YELLOW + _("configVersionDisableStatus") + (!p.getConfig().getBoolean("disableVersion") ? ChatColor.GREEN + _("configStatusTrue") : ChatColor.RED + _("configStatusFalse")));
												break;
						
						case "logcommands":		sender.sendMessage(ChatColor.YELLOW + _("configCommandsLoggingStatus") + (p.getConfig().getBoolean("logCommands") ? ChatColor.GREEN + _("configStatusTrue") : ChatColor.RED + _("configStatusFalse")));
												break;
						
						case "disabledcommands":sender.sendMessage(ChatColor.YELLOW + _("configDisabledCommands") + ChatColor.WHITE + p.getConfig().getList("disabledCommands").toString());
												break;
												
						case "defaultlang":		sender.sendMessage(ChatColor.YELLOW + _("configDefaultLang") + p.getConfig().getString("defaultLang"));
												break;
												
						default:				sender.sendMessage(ChatColor.RED + _("configUnrecognized"));
												break;
					}
				} else if (args[1].equals("set") || args[0].equals("cs")) {
					
					/***
					 * SETTING CONFIG VALUES
					 */
					if ((args[0].equals("cs") && ((aLength >= 2) && (aLength <= 4))) || (aLength == 5)) {
						switch ((args[0].equals("cs") ? args[1].toLowerCase() : args[2].toLowerCase())) {
							case "disableversion":	p.getConfig().set("disableVersion", !p.getConfig().getBoolean("disableVersion"));
													p.saveConfig();
													sender.sendMessage(ChatColor.YELLOW + _("configUpdated") + (!p.getConfig().getBoolean("disableVersion") ? ChatColor.GREEN + _("configStatusTrue") : ChatColor.RED + _("configStatusFalse")));
													break;
							
							case "logcommands":		p.getConfig().set("logCommands", !p.getConfig().getBoolean("logCommands"));
													p.saveConfig();
													sender.sendMessage(ChatColor.YELLOW + _("configUpdated") + (p.getConfig().getBoolean("logCommands") ? ChatColor.GREEN + _("configStatusTrue") : ChatColor.RED + _("configStatusFalse")));
													break;
													
							case "disabledcommands":if (args[3].equals("add") || args[2].equals("add")) {
														List<Object> l = p.getConfig().getList("disabledCommands");
														String toAdd = args[2].equals("add") ? args[3] : args[4];
														if (!l.contains(toAdd)) {
															l.add(args[2].equals("add") ? args[3] : args[4]);
															p.getConfig().set("disabledCommands", l);
															p.saveConfig();
														}
														sender.sendMessage(ChatColor.YELLOW + _("configUpdated") + ChatColor.WHITE + p.getConfig().getList("disabledCommands").toString());
													} else if (args[3].equals("remove") || (args[2].equals("remove"))) {
														List<Object> l = p.getConfig().getList("disabledCommands");
														String toRemove = args[2].equals("remove") ? args[3] : args[4];
														if (l.contains(toRemove)) {
															l.remove(args[2].equals("remove") ? args[3] : args[4]);
															p.getConfig().set("disabledCommands", l);
															p.saveConfig();
														}
														sender.sendMessage(ChatColor.YELLOW + _("configUpdated") + ChatColor.WHITE + p.getConfig().getList("disabledCommands").toString());
													} else {
														sender.sendMessage(ChatColor.RED + _("configUnspecifiedError1"));
														sender.sendMessage(ChatColor.RED + _("configUnspecifiedError2"));
														sender.sendMessage(ChatColor.RED + _("configUnspecifiedError3"));
													}
													break;
							
							case "defaultlang":		if (args[2] != null) {
														p.getConfig().set("defaultLang", args[2]);
														p.saveConfig();
														CommandsEX.defaultLocale = args[2];
														sender.sendMessage(ChatColor.YELLOW + _("configUpdated") + ChatColor.WHITE + p.getConfig().getString("defaultLang"));
													} else {
														sender.sendMessage(ChatColor.RED + _("configUnspecifiedError1"));
														sender.sendMessage(ChatColor.RED + _("configUnspecifiedError2"));
														sender.sendMessage(ChatColor.RED + _("configUnspecifiedError3"));
													}
													break;
													
							default:				sender.sendMessage(ChatColor.RED + _("configUnrecognized"));
													break;
						}
					} else {
						sender.sendMessage(ChatColor.RED + _("configNotEnoughParams"));
					}
				}
			}
		} else {
			
			/***
			 * UNRECOGNIZED
			 */
			
			sender.sendMessage(ChatColor.RED + _("configUnrecognized"));
		}
		
		return true;
	}
}
