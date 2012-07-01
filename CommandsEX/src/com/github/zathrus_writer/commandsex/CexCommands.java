package com.github.zathrus_writer.commandsex;

import static com.github.zathrus_writer.commandsex.Language._;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.commands.Command_cex_tpa;
import com.github.zathrus_writer.commandsex.commands.Command_cex_tpahere;
import com.github.zathrus_writer.commandsex.handlers.Handler_economypromote;
import com.github.zathrus_writer.commandsex.handlers.Handler_playtimepromote;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class CexCommands {
	
	protected static String[] unconfigurables = {"enableDatabase", "sqlType", "database", "host", "port", "name", "password", "prefix", "chatReplaceFile", "playerCommandsReplaceFile", "consoleCommandsReplaceFile", "replacements", "xmppUser", "xmppHost", "xmppPassword", "xmppRoom.name", "xmppRoom.password", "xmppBotNick", "xmppCommandPrefix", "xmppAdmins", "timedPromote", "ecoPromote", "quizDiff", "quizzes", "kits", "deathGroupChanges"};
	
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
		} else if ((aLength == 1) && args[0].equals("null")) {
			// does nothing, prints nothing - used for commands replacements/aliasing
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
					} else if (v.equals("weathernotifyenabled")) {
						sender.sendMessage(ChatColor.YELLOW + _("configWeatherNotifyEnabled", sender.getName()) + (p.getConfig().getBoolean("weatherNotifyEnabled") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("maxwarpsperplayer")) {
						sender.sendMessage(ChatColor.YELLOW + _("configMaxWarpsPerPlayer", sender.getName()) + p.getConfig().getString("maxWarpsPerPlayer"));
					} else if (v.equals("maxipholdtime")) {
						sender.sendMessage(ChatColor.YELLOW + _("configMaxIPholdTime", sender.getName()) + p.getConfig().getString("maxIPholdTime"));
					} else if (v.equals("mintempbanswarn")) {
						sender.sendMessage(ChatColor.YELLOW + _("configMinTempBansWarn", sender.getName()) + p.getConfig().getString("minTempBansWarn"));
					} else if (v.equals("silentbans")) {
						sender.sendMessage(ChatColor.YELLOW + _("configSilentBans", sender.getName()) + (!p.getConfig().getBoolean("silentBans") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("slappreventdamage")) {
						sender.sendMessage(ChatColor.YELLOW + _("configSlapPreventDamage", sender.getName()) + (p.getConfig().getBoolean("slapPreventDamage") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("joinsilenttime")) {
						sender.sendMessage(ChatColor.YELLOW + _("configJoinSilentTime", sender.getName()) + p.getConfig().getString("joinSilentTime") + " " + _("seconds", sender.getName()));
					} else if (v.equals("jailarea")) {
						sender.sendMessage(ChatColor.YELLOW + _("configJailArea", sender.getName()) + p.getConfig().getString("jailArea") + " " + _("blocks", sender.getName()));
					} else if (v.equals("kamikazeinstakill")) {
						sender.sendMessage(ChatColor.YELLOW + _("configKamikazeInstaKill", sender.getName()) + (p.getConfig().getBoolean("kamikazeInstaKill") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("kamikazetimeout")) {
						sender.sendMessage(ChatColor.YELLOW + _("configKamikazeTimeout", sender.getName()) + p.getConfig().getString("kamikazeTimeout") + " " + _("seconds", sender.getName()));
					} else if (v.equals("timedpromotetasktime")) {
						sender.sendMessage(ChatColor.YELLOW + _("configTimedPromoteTaskTime", sender.getName()) + p.getConfig().getString("timedPromoteTaskTime") + " " + _("seconds", sender.getName()));
					} else if (v.equals("ecopromotetasktime")) {
						sender.sendMessage(ChatColor.YELLOW + _("configEcoPromoteTaskTime", sender.getName()) + p.getConfig().getString("ecoPromoteTaskTime") + " " + _("seconds", sender.getName()));
					}  else if (v.equals("ecopromoteautodemote")) {
						sender.sendMessage(ChatColor.YELLOW + _("configEcoPromoteAutoDemote", sender.getName()) + (p.getConfig().getBoolean("ecoPromoteAutoDemote") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("ecopromoteexclude")) {
						sender.sendMessage(ChatColor.YELLOW + _("configEcoPromoteExclude", sender.getName()) + Utils.implode(p.getConfig().getList("ecoPromoteTaskTime"), ", "));
					} else if (v.equals("timedpromoteexclude")) {
						sender.sendMessage(ChatColor.YELLOW + _("configTimedPromoteExclude", sender.getName()) + Utils.implode(p.getConfig().getList("timedPromoteTaskTime"), ", "));
					} else if (v.equals("privatemsgcommands")) {
						sender.sendMessage(ChatColor.YELLOW + _("configPrivateMsgCommands", sender.getName()) + Utils.implode(p.getConfig().getList("privateMsgCommands"), ", "));
					} else if (v.equals("quizrepeattime")) {
						sender.sendMessage(ChatColor.YELLOW + _("configQuizRepeatTime", sender.getName()) + p.getConfig().getInt("quizRepeatTime") + " " + _("seconds", sender.getName()));
					} else if (v.equals("quizdelay")) {
						sender.sendMessage(ChatColor.YELLOW + _("configQuizDelay", sender.getName()) + p.getConfig().getInt("quizDelay") + " " + _("seconds", sender.getName()));
					} else if (v.equals("quizduration")) {
						sender.sendMessage(ChatColor.YELLOW + _("configQuizDuration", sender.getName()) + p.getConfig().getInt("quizDuration") + " " + _("seconds", sender.getName()));
					} else if (v.equals("weathernotifytime")) {
						sender.sendMessage(ChatColor.YELLOW + _("configWeatherNotifyTime", sender.getName()) + p.getConfig().getInt("weatherNotifyTime") + " " + _("seconds", sender.getName()));
					} else if (v.equals("defaultslapheight")) {
						sender.sendMessage(ChatColor.YELLOW + _("configDefaultSlapHeight", sender.getName()) + p.getConfig().getInt("defaultSlapHeight") + " " + _("blocks", sender.getName()));
					} else if (v.equals("deathgroupscleanuptime")) {
						sender.sendMessage(ChatColor.YELLOW + _("configDeathGroupsCleanupTime", sender.getName()) + p.getConfig().getInt("deathGroupsCleanupTime") + " " + _("days", sender.getName()));
					} else if (v.equals("motd")) {
						sender.sendMessage(ChatColor.YELLOW + _("configMotd", sender.getName()) + p.getConfig().getString("motd"));
					} else if (v.equals("motdnewplayer")) {
						sender.sendMessage(ChatColor.YELLOW + _("configMotdNewPlayer", sender.getName()) + p.getConfig().getString("motdNewPlayer"));
					} else if (v.equals("kitsBroadcastGifts")) {
						sender.sendMessage(ChatColor.YELLOW + _("configKitsBroadcastGifts", sender.getName()) + p.getConfig().getBoolean("kitsBroadcastGifts"));
					} else if (v.equals("nanosuitpumpkin")) {
						sender.sendMessage(ChatColor.YELLOW + _("configNanoSuitPumpkin", sender.getName()) + p.getConfig().getBoolean("nanoSuitPumpkin"));
					} else if (v.equals("nanosuitspeed")) {
						sender.sendMessage(ChatColor.YELLOW + _("configNanoSuitSpeed", sender.getName()) + p.getConfig().getInt("nanoSuitSpeed"));
					} else if (v.equals("nanosuitjump")) {
						sender.sendMessage(ChatColor.YELLOW + _("configNanoSuitJump", sender.getName()) + p.getConfig().getInt("nanoSuitJump"));
					} else if (v.equals("nanosuitdamage")) {
						sender.sendMessage(ChatColor.YELLOW + _("configNanoSuitDamage", sender.getName()) + p.getConfig().getInt("nanoSuitDamage"));
					} else if (v.equals("nanosuitrechargetime")) {
						sender.sendMessage(ChatColor.YELLOW + _("configNanoSuitRechargeTime", sender.getName()) + p.getConfig().getInt("nanoSuitDamage"));
					} else if (v.equals("showmessagesonsmite")) {
						sender.sendMessage(ChatColor.YELLOW + _("configShowMessagesOnSmite", sender.getName()) + p.getConfig().getBoolean("showMessagesOnSmite"));
					} else if (v.equals("showmessagesonexplode")) {
						sender.sendMessage(ChatColor.YELLOW + _("configShowMessagesOnExplode", sender.getName()) + p.getConfig().getBoolean("showMessagesOnExplode"));
					} else if (v.equals("info")) {
						sender.sendMessage(ChatColor.YELLOW + _("configInfo", sender.getName()) + p.getConfig().getString("info"));
					} else if (v.equals("fakequitmessage")) {
						sender.sendMessage(ChatColor.YELLOW + _("configFakeQuitMessage", sender.getName()) + p.getConfig().getBoolean("fakeQuitMessage"));
					} else if (v.equals("fakejoinmessage")) {
						sender.sendMessage(ChatColor.YELLOW + _("configFakeJoinMessage", sender.getName()) + p.getConfig().getBoolean("fakeJoinMessage"));
					} else if (v.equals("serverowner")) {
						sender.sendMessage(ChatColor.YELLOW + _("configServerOwner", sender.getName()) + p.getConfig().getString("ServerOwner"));
					} else if (v.equals("kittycannonexplosionstrength")) {
						sender.sendMessage(ChatColor.YELLOW + _("configKittyCannonExplosionStrength", sender.getName()) + p.getConfig().getInt("kittyCannonExplosionStrength"));
					} else if (v.equals("blockmobexplosiondamage")) {
						sender.sendMessage(ChatColor.YELLOW + _("configBlockMobExplosionDamage", sender.getName()) + p.getConfig().getBoolean("blockMobExplosionDamage"));
					} else if (v.equals("blockcreeperexplosions")) {
						sender.sendMessage(ChatColor.YELLOW + _("configBlockCreeperExplosions", sender.getName()) + p.getConfig().getBoolean("blockCreeperExplosions"));
					} else if (v.equals("blocktntexplosions")) {
						sender.sendMessage(ChatColor.YELLOW + _("configBlockTNTExplosions", sender.getName()) + p.getConfig().getBoolean("blockTNTExplosions"));
					} else if (v.equals("blockFireballExplosions")) {
						sender.sendMessage(ChatColor.YELLOW + _("configBlockFireballExposions", sender.getName()) + p.getConfig().getBoolean("blockFireballExplosions"));
					} else if (v.equals("startuptimer")) {
						sender.sendMessage(ChatColor.YELLOW + _("configStartupTimer", sender.getName()) + p.getConfig().getBoolean("startupTimer"));
					} else if (v.equals("spawnmoblimit")) {
						sender.sendMessage(ChatColor.YELLOW + _("spawnMobLimit", sender.getName()) + p.getConfig().getInt("spawnMobLimit"));
					} else {
						LogHelper.showWarning("configUnrecognized", sender);
					}
				} else if (args[1].equals("set") || args[0].equals("cs")) {
					
					/***
					 * SETTING CONFIG VALUES
					 */
					String v = (args[0].equals("cs") ? args[1].toLowerCase() : args[2].toLowerCase());
					String setting = (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase());
					if (v.equals("disableversion")) {
						p.getConfig().set("disableVersion", !p.getConfig().getBoolean("disableVersion"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + (p.getConfig().getBoolean("disableVersion") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("logcommands")) {
						p.getConfig().set("logCommands", !p.getConfig().getBoolean("logCommands"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + (p.getConfig().getBoolean("logCommands") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("defaultlang")) {
						if ((aLength > 2) && setting != null) {
							p.getConfig().set("defaultLang", setting);
							p.saveConfig();
							Language.defaultLocale = setting;
							sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("defaultLang"));
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("tpatimeout")) {
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("tpaTimeout", setting);
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
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("tpahereTimeout", setting);
								p.saveConfig();
								try {
									Command_cex_tpahere.tTimeout = Integer.parseInt(setting);
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
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("commandCooldownTime", setting);
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
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("homeQualifyTime", setting);
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("homeQualifyTime"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("quizrepeattime")) {
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("quizRepeatTime", setting);
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("quizRepeatTime"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("quizdelay")) {
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("quizDelay", setting);
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("quizDelay"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("quizduration")) {
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("quizDuration", setting);
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("quizDuration"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("weathernotifytime")) {
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("weatherNotifyTime", setting);
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("weatherNotifyTime"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("defaultslapheight")) {
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("defaultSlapHeight", setting);
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("defaultSlapHeight"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("maxwarpsperplayer")) {
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("maxWarpsPerPlayer", setting);
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
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("maxIPholdTime", setting);
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
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("minTempBansWarn", setting);
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("minTempBansWarn"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("joinsilenttime")) {
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("joinSilentTime", setting);
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("joinSilentTime"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("jailarea")) {
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("jailArea", setting);
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("jailArea"));
							} else {
								// area not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("kamikazetimeout")) {
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("kamikazeTimeout", setting);
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("kamikazeTimeout"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("deathgroupscleanuptime")) {
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("deathGroupsCleanupTime", setting);
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathGroupsCleanupTime"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("timedpromotetasktime")) {
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("timedPromoteTaskTime", setting);
								p.saveConfig();

								// cancel old task and create a new one with this new timeout value
								try {
									CommandsEX.plugin.getServer().getScheduler().cancelTask(Handler_playtimepromote.promotionTaskID);
									Handler_playtimepromote.promotionTaskID = CommandsEX.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(CommandsEX.plugin, new Runnable() {
										@Override
										public void run() {
											// create ExecutorService to manage threads                        
											ExecutorService threadExecutor = Executors.newFixedThreadPool(1);
											threadExecutor.execute(new Runnable() {
												@Override
												public void run() {
													Handler_playtimepromote.checkTimedPromotions();
												}
											});
											threadExecutor.shutdown(); // shutdown worker threads
										}
									}, (20 * Integer.parseInt(args[2])), (20 * Integer.parseInt(args[2])));
								} catch (Throwable ex) {}
								
								// show message
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("timedPromoteTaskTime"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("ecopromotetasktime")) {
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("ecoPromoteTaskTime", setting);
								p.saveConfig();

								// cancel old task and create a new one with this new timeout value
								try {
									CommandsEX.plugin.getServer().getScheduler().cancelTask(Handler_economypromote.promotionTaskID);
									Handler_economypromote.promotionTaskID = CommandsEX.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(CommandsEX.plugin, new Runnable() {
										@Override
										public void run() {
											// create ExecutorService to manage threads                        
											ExecutorService threadExecutor = Executors.newFixedThreadPool(1);
											threadExecutor.execute(new Runnable() {
												@Override
												public void run() {
													Handler_economypromote.checkTimedPromotions();
												}
											});
											threadExecutor.shutdown(); // shutdown worker threads
										}
									}, (20 * Integer.parseInt(setting)), (20 * Integer.parseInt(setting)));
								} catch (Throwable ex) {}
								
								// show message
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("ecoPromoteTaskTime"));
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
					} else if (v.equals("kamikazeinstakill")) {
						p.getConfig().set("kamikazeInstaKill", !p.getConfig().getBoolean("kamikazeInstaKill"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + (p.getConfig().getBoolean("kamikazeInstaKill") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("weathernotifyenabled")) {
						p.getConfig().set("weatherNotifyEnabled", !p.getConfig().getBoolean("weatherNotifyEnabled"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + (p.getConfig().getBoolean("weatherNotifyEnabled") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("slappreventdamage")) {
						p.getConfig().set("slapPreventDamage", !p.getConfig().getBoolean("slapPreventDamage"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + (p.getConfig().getBoolean("slapPreventDamage") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("debugmode")) {
						p.getConfig().set("debugMode", !p.getConfig().getBoolean("debugMode"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + (p.getConfig().getBoolean("debugMode") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("timedpromoteexclude")) {
						if (setting.equals("add") || setting.equals("add")) {
							@SuppressWarnings("unchecked")
							List<String> l = (List<String>) p.getConfig().getList("timedPromoteExclude");
							String toAdd = setting.equals("add") ? args[3] : args[4];
							if (!l.contains(toAdd)) {
								l.add(setting.equals("add") ? args[3] : args[4]);
								p.getConfig().set("timedPromoteExclude", l);
								p.saveConfig();
							}
							sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + p.getConfig().getList("timedPromoteExclude").toString());
						} else if (args[3].equals("remove") || (args[2].equals("remove"))) {
							@SuppressWarnings("unchecked")
							List<String> l = (List<String>) p.getConfig().getList("timedPromoteExclude");
							String toRemove = args[2].equals("remove") ? args[3] : args[4];
							if (l.contains(toRemove)) {
								l.remove(args[2].equals("remove") ? args[3] : args[4]);
								p.getConfig().set("timedPromoteExclude", l);
								p.saveConfig();
							}
							sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + p.getConfig().getList("timedPromoteExclude").toString());
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("ecopromoteexclude")) {
						if (args[3].equals("add") || args[2].equals("add")) {
							@SuppressWarnings("unchecked")
							List<String> l = (List<String>) p.getConfig().getList("ecoPromoteExclude");
							String toAdd = args[2].equals("add") ? args[3] : args[4];
							if (!l.contains(toAdd)) {
								l.add(args[2].equals("add") ? args[3] : args[4]);
								p.getConfig().set("ecoPromoteExclude", l);
								p.saveConfig();
							}
							sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + p.getConfig().getList("ecoPromoteExclude").toString());
						} else if (args[3].equals("remove") || (args[2].equals("remove"))) {
							@SuppressWarnings("unchecked")
							List<String> l = (List<String>) p.getConfig().getList("ecoPromoteExclude");
							String toRemove = args[2].equals("remove") ? args[3] : args[4];
							if (l.contains(toRemove)) {
								l.remove(args[2].equals("remove") ? args[3] : args[4]);
								p.getConfig().set("ecoPromoteExclude", l);
								p.saveConfig();
							}
							sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + p.getConfig().getList("ecoPromoteExclude").toString());
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("privatemsgcommands")) {
						if (args[3].equals("add") || args[2].equals("add")) {
							@SuppressWarnings("unchecked")
							List<String> l = (List<String>) p.getConfig().getList("privateMsgCommands");
							String toAdd = args[2].equals("add") ? args[3] : args[4];
							if (!l.contains(toAdd)) {
								l.add(args[2].equals("add") ? args[3] : args[4]);
								p.getConfig().set("privateMsgCommands", l);
								p.saveConfig();
							}
							sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + p.getConfig().getList("privateMsgCommands").toString());
						} else if (args[3].equals("remove") || (args[2].equals("remove"))) {
							@SuppressWarnings("unchecked")
							List<String> l = (List<String>) p.getConfig().getList("privateMsgCommands");
							String toRemove = args[2].equals("remove") ? args[3] : args[4];
							if (l.contains(toRemove)) {
								l.remove(args[2].equals("remove") ? args[3] : args[4]);
								p.getConfig().set("privateMsgCommands", l);
								p.saveConfig();
							}
							sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + p.getConfig().getList("privateMsgCommands").toString());
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("motd")) {
						if ((aLength > 2) && setting != null) {
							p.getConfig().set("motd", setting);
							p.saveConfig();
							sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + Utils.replaceChatColors(p.getConfig().getString("motd")));
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("motdnewplayer")) {
						if ((aLength > 2) && setting != null) {
							p.getConfig().set("motdNewPlayer", setting);
							p.saveConfig();
							sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + Utils.replaceChatColors(p.getConfig().getString("motdNewPlayer")));
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("kitsbroadcastgifts")) {
						p.getConfig().set("kitsBroadcastGifts", !p.getConfig().getBoolean("kitsBroadcastGifts"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + (p.getConfig().getBoolean("kitsBroadcastGifts") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("nanosuitpumpkin")) {
						p.getConfig().set("nanosuitpumpkin", !p.getConfig().getBoolean("nanosuitpumpkin"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + (p.getConfig().getBoolean("nanoSuitPumpkin") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("nanosuitspeed")) {
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("nanoSuitSpeed", setting);
								p.saveConfig();
								// show message
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("nanoSuitSpeed"));
							} else {
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("nanosuitjump")) {
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("nanoSuitJump", setting);
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("nanoSuitJump"));
							} else {
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("nanosuitdamage")) {
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("nanoSuitDamage", setting);
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("nanoSuitDamage"));
							} else {
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("nanosuittime")) {
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("nanoSuitTime", setting);
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("nanoSuitTime"));
							} else {
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("nanosuitrechargetime")) {
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("nanoSuitRechargeTime", setting);
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("nanoSuitRechargeTime"));
							} else {
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("showmessagesonsmite")) {
						p.getConfig().set("showMessagesOnSmite", !p.getConfig().getBoolean("showMessagesOnSmite"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + (p.getConfig().getBoolean("showMessagesOnSmite") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equalsIgnoreCase("showmessagesonexplode")) {
						p.getConfig().set("showMessagesOnExplode", !p.getConfig().getBoolean("showMessagesOnExplode"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + (p.getConfig().getBoolean("showMessagesOnExplode") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("info")) {
						p.getConfig().set("info", setting);
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + Utils.replaceChatColors(p.getConfig().getString("info")));
					} else if (v.equals("fakequitmessage")) {
						p.getConfig().set("fakeQuitMessage", !p.getConfig().getBoolean("fakeQuitMessage"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + (p.getConfig().getBoolean("fakeQuitMessage") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("fakejoinmessage")) {
						p.getConfig().set("fakeJoinMessage", !p.getConfig().getBoolean("fakeJoinMessage"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + (p.getConfig().getBoolean("fakeJoinMessage") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("serverowner")) {
						p.getConfig().set("ServerOwner", setting);
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("ServerOwner"));
					} else if (v.equals("kittycannonexplosionstrength")) {
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("kittyCannonExplosionStrength", setting);
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("kittyCannonExplosionStrength"));
							} else {
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("blockmobexplosiondamage")) {
						p.getConfig().set("blockMobExplosionDamage", !p.getConfig().getBoolean("blockMobExplosionDamage"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + (p.getConfig().getBoolean("blockMobExplosionDamage") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("blockcreeperexplosions")) {
						p.getConfig().set("blockCreeperExplosions", !p.getConfig().getBoolean("blockCreeperExplosions"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + (p.getConfig().getBoolean("blockCreeperExplosions") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("blocktntexplosions")) {
						p.getConfig().set("blockTNTExplosions", !p.getConfig().getBoolean("blockTNTExplosions"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + (p.getConfig().getBoolean("blockTNTExplosions") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("blockfireballexplosions")) {
						p.getConfig().set("blockFireballExplosions", !p.getConfig().getBoolean("blockFireballExplosions"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + (p.getConfig().getBoolean("blockFireballExplosions") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("startuptimer")) {
						p.getConfig().set("startupTimer", !p.getConfig().getBoolean("startupTimer"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + (p.getConfig().getBoolean("startupTimer") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("spawnmoblimit")) {
						if ((aLength > 2) && setting != null) {
							if (setting.matches(CommandsEX.intRegex)) {
								p.getConfig().set("spawnMobLimit", setting);
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("spawnMobLimit"));
							} else {
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
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
