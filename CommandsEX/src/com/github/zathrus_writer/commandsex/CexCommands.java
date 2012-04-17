package com.github.zathrus_writer.commandsex;

import static com.github.zathrus_writer.commandsex.Language._;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.commands.Command_cex_tpa;
import com.github.zathrus_writer.commandsex.commands.Command_cex_tpahere;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;

public class CexCommands {
	
	protected static String[] unconfigurables = {"enableDatabase", "sqlType", "database", "host", "port", "name", "password", "prefix", "chatReplaceFile", "playerCommandsReplaceFile", "consoleCommandsReplaceFile"};
	
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
				sender.sendMessage(ChatColor.YELLOW + CommandsEX.pdfFile.getName() + ", " + _("version", sender.getName()) + " " + CommandsEX.pdfFile.getVersion());
			}
		} else if ((aLength == 1) && args[0].equals("reload")) {

			/***
			 * RELOAD
			 */
			
			if (sender.getName().toLowerCase().equals("console") || ((sender instanceof Player) && Permissions.checkPerms((Player)sender, "cex.reload"))) {
				p.reloadConfig();
				sender.sendMessage(ChatColor.GREEN + _("configReloaded", sender.getName()));
			} else {
				LogHelper.logWarning("["+ CommandsEX.pdfFile.getName() +"]: Player " + sender.getName() + " tried to execute reload command without permission.");
			}
		} else if ((aLength == 1) && (args[0].equals("?") || args[0].equals("help"))) {

			/***
			 * USAGE HELP REQUEST
			 */
			Commands.showCommandHelpAndUsage(sender, "cex", "cex");
		} else if ((aLength < 3) && args[0].equals("config")) {
			
			/***
			 * SHOWING ALL AVAILABLE OPTIONS
			 */
			Set<String> s = p.getConfig().getKeys(false);
			Set<String> opts = new HashSet<String>();
			for (String ss : s) {
				Boolean canAdd = true;
				for (String u : unconfigurables) {
					if (u.equals(ss)) {
						canAdd = false;
						break;
					}
				}

				if (canAdd) {
					opts.add(ss);
				}
			}
			sender.sendMessage(ChatColor.WHITE + _("configAvailableNodes", sender.getName()) + opts.toString());
			sender.sendMessage(ChatColor.WHITE + _("configAvailableNodesUsage", sender.getName()));
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
				LogHelper.showWarning("configUnrecognizedAction", sender);
			} else {
				if (args[1].equals("get") || args[0].equals("cg")) {
					
					/***
					 * GETTING CONFIG VALUES
					 */
					String v = (args[0].equals("cg") ? args[1].toLowerCase() : args[2].toLowerCase());
					if (v.equals("disableversion")) {
						sender.sendMessage(ChatColor.YELLOW + _("configVersionDisableStatus", sender.getName()) + (!p.getConfig().getBoolean("disableVersion") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("logcommands")) {
						sender.sendMessage(ChatColor.YELLOW + _("configCommandsLoggingStatus", sender.getName()) + (p.getConfig().getBoolean("logCommands") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("defaultlang")) {
						sender.sendMessage(ChatColor.YELLOW + _("configDefaultLang", sender.getName()) + p.getConfig().getString("defaultLang"));
					} else if (v.equals("tpatimeout")) {
						sender.sendMessage(ChatColor.YELLOW + _("configTpaTimeout", sender.getName()) + p.getConfig().getString("tpaTimeout"));
					} else if (v.equals("tpaheretimeout")) {
						sender.sendMessage(ChatColor.YELLOW + _("configTpahereTimeout", sender.getName()) + p.getConfig().getString("tpahereTimeout"));
					} else if (v.equals("debugmode")) {
						sender.sendMessage(ChatColor.YELLOW + _("configDebugMode", sender.getName()) + p.getConfig().getString("debugMode"));
					} else if (v.equals("commandcooldowntime")) {
						sender.sendMessage(ChatColor.YELLOW + _("configCommandCooldownTime", sender.getName()) + p.getConfig().getInt("commandCooldownTime") + " " + _("seconds", sender.getName()));
					} else if (v.equals("homequalifytime")) {
						sender.sendMessage(ChatColor.YELLOW + _("configHomeQualifyTime", sender.getName()) + p.getConfig().getInt("homeQualifyTime") + " " + _("seconds", sender.getName()));
					} else if (v.equals("allowmultiworldhomes")) {
						sender.sendMessage(ChatColor.YELLOW + _("configAllowMultiworlds", sender.getName()) + (p.getConfig().getBoolean("allowMultiworldHomes") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("silentkicks")) {
						sender.sendMessage(ChatColor.YELLOW + _("configSilentKicks", sender.getName()) + (!p.getConfig().getBoolean("silentKicks") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("maxwarpsperplayer")) {
						sender.sendMessage(ChatColor.YELLOW + _("configMaxWarpsPerPlayer", sender.getName()) + p.getConfig().getString("maxWarpsPerPlayer"));
					} else if (v.equals("maxipholdtime")) {
						sender.sendMessage(ChatColor.YELLOW + _("configMaxIPholdTime", sender.getName()) + p.getConfig().getString("maxIPholdTime"));
					} else if (v.equals("mintempbanswarn")) {
						sender.sendMessage(ChatColor.YELLOW + _("configMinTempBansWarn", sender.getName()) + p.getConfig().getString("minTempBansWarn"));
					} else if (v.equals("silentbans")) {
						sender.sendMessage(ChatColor.YELLOW + _("configSilentBans", sender.getName()) + (!p.getConfig().getBoolean("silentBans") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else {
						LogHelper.showWarning("configUnrecognized", sender);
					}
				} else if (args[1].equals("set") || args[0].equals("cs")) {
					
					/***
					 * SETTING CONFIG VALUES
					 */
					String v = (args[0].equals("cs") ? args[1].toLowerCase() : args[2].toLowerCase());
					if (v.equals("disableversion")) {
						p.getConfig().set("disableVersion", !p.getConfig().getBoolean("disableVersion"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + (p.getConfig().getBoolean("disableVersion") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("logcommands")) {
						p.getConfig().set("logCommands", !p.getConfig().getBoolean("logCommands"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + (p.getConfig().getBoolean("logCommands") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("defaultlang")) {
						if ((aLength > 2) && args[2] != null) {
							p.getConfig().set("defaultLang", args[2]);
							p.saveConfig();
							Language.defaultLocale = args[2];
							sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("defaultLang"));
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("tpatimeout")) {
						if ((aLength > 2) && args[2] != null) {
							if (args[2].matches(CommandsEX.intRegex)) {
								p.getConfig().set("tpaTimeout", args[2]);
								p.saveConfig();
								try {
									Command_cex_tpa.tTimeout = Integer.parseInt(args[2]);
								} catch (Throwable e) {
									// the tpa command might not be present in the plugin
								}
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("tpaTimeout"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("tpaheretimeout")) {
						if ((aLength > 2) && args[2] != null) {
							if (args[2].matches(CommandsEX.intRegex)) {
								p.getConfig().set("tpahereTimeout", args[2]);
								p.saveConfig();
								try {
									Command_cex_tpahere.tTimeout = Integer.parseInt(args[2]);
								} catch (Throwable e) {
									// the tpahere command might not be present in the plugin
								}
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("tpahereTimeout"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("commandcooldowntime")) {
						if ((aLength > 2) && args[2] != null) {
							if (args[2].matches(CommandsEX.intRegex)) {
								p.getConfig().set("commandCooldownTime", args[2]);
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("commandCooldownTime"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("homequalifytime")) {
						if ((aLength > 2) && args[2] != null) {
							if (args[2].matches(CommandsEX.intRegex)) {
								p.getConfig().set("homeQualifyTime", args[2]);
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("homeQualifyTime"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("maxwarpsperplayer")) {
						if ((aLength > 2) && args[2] != null) {
							if (args[2].matches(CommandsEX.intRegex)) {
								p.getConfig().set("maxWarpsPerPlayer", args[2]);
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("maxWarpsPerPlayer"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("maxipholdtime")) {
						if ((aLength > 2) && args[2] != null) {
							if (args[2].matches(CommandsEX.intRegex)) {
								p.getConfig().set("maxIPholdTime", args[2]);
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("maxIPholdTime"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("mintempbanswarn")) {
						if ((aLength > 2) && args[2] != null) {
							if (args[2].matches(CommandsEX.intRegex)) {
								p.getConfig().set("minTempBansWarn", args[2]);
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("minTempBansWarn"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("allowmultiworldhomes")) {
						p.getConfig().set("allowMultiworldHomes", !p.getConfig().getBoolean("allowMultiworldHomes"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + (p.getConfig().getBoolean("allowMultiworldHomes") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("silentkicks")) {
						p.getConfig().set("silentKicks", !p.getConfig().getBoolean("silentKicks"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + (p.getConfig().getBoolean("silentKicks") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("silentbans")) {
						p.getConfig().set("silentBans", !p.getConfig().getBoolean("silentBans"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + (p.getConfig().getBoolean("silentBans") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("debugmode")) {
						p.getConfig().set("debugMode", !p.getConfig().getBoolean("debugMode"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + (p.getConfig().getBoolean("debugMode") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else {
						LogHelper.showWarning("configUnrecognized", sender);
					}
				}
			}
		} else {
			
			/***
			 * UNRECOGNIZED
			 */
			
			sender.sendMessage(ChatColor.RED + _("configUnrecognized", sender.getName()));
		}
		
		return true;
	}
}
