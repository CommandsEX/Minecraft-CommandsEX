package com.github.zathrus_writer.commandsex;

import static com.github.zathrus_writer.commandsex.Language._;

import java.util.ArrayList;
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
		
		if (aLength == 0 || args[0].equalsIgnoreCase("about") || args[0].equalsIgnoreCase("version")) {

			/***
			 * ABOUT
			 */

			List<String> authors = new ArrayList<String>();
			for (String author : CommandsEX.pdfFile.getAuthors()){
				authors.add(author);
			}
			Integer aSize = authors.size();
			String lName = (String) authors.get(aSize - 1);
			authors.remove(aSize - 1);
			sender.sendMessage(ChatColor.GREEN + CommandsEX.pdfFile.getName() + " " + ChatColor.YELLOW
					+ (!p.getConfig().getBoolean("disableVersion") ? _("version", sender.getName())
					+ " " + ChatColor.GREEN + CommandsEX.pdfFile.getVersion().replaceFirst("CommandsEX ", "") + " " : "") + ChatColor.YELLOW + _("by", sender.getName())
					+ ChatColor.GREEN + Utils.implode(authors, ChatColor.YELLOW + ", " + ChatColor.GREEN) + ChatColor.YELLOW
					+ " " + _("and", sender.getName()) + ChatColor.GREEN + " " + lName);
			sender.sendMessage(ChatColor.GREEN + _("wiki", sender.getName()) + ChatColor.YELLOW +  "http://bit.ly/LS0sIj");
			sender.sendMessage(ChatColor.GREEN + _("bukkitDev", sender.getName()) + ChatColor.YELLOW +  "http://bit.ly/P2ZJHA");
			sender.sendMessage(ChatColor.GREEN + _("bukkitForums", sender.getName()) + ChatColor.YELLOW +  "http://bit.ly/MmhPjV");
			sender.sendMessage(ChatColor.GREEN + _("donate", sender.getName()) + ChatColor.YELLOW +  "http://bit.ly/LYCsrQ");
			sender.sendMessage(ChatColor.GREEN + _("builder", sender.getName()) + ChatColor.YELLOW +  "http://bit.ly/OdiROq");
			sender.sendMessage(ChatColor.GREEN + _("ticket", sender.getName()) + ChatColor.YELLOW +  "http://bit.ly/PPhI5I");
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
			 * CONFIGURATION GETTING / (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())
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
						sender.sendMessage(ChatColor.YELLOW + _("configKittyCannonExplosionStrength", sender.getName()) + p.getConfig().getInt("KittyCannonExplosionStrength"));
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
						sender.sendMessage(ChatColor.YELLOW + _("configSpawnMobLimit", sender.getName()) + p.getConfig().getInt("spawnMobLimit"));
					} else if (v.equals("pluginmetrics")) {
						sender.sendMessage(ChatColor.YELLOW + _("configPluginMetrics", sender.getName()) + p.getConfig().getBoolean("pluginMetrics"));
					} else if (v.equals("deathpvp")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathPvP"));
					} else if (v.equals("deathdrown")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathDrown"));
					} else if (v.equals("deathfall")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathFall"));
					} else if (v.equals("deathfire")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathFire"));
					} else if (v.equals("deathlava")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathLava"));
					} else if (v.equals("deathmagic")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathMagic"));
					} else if (v.equals("deathstarvation")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathStarvation"));
					} else if (v.equals("deathpoison")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathPoison"));
					} else if (v.equals("deathsuffocate")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathSuffocate"));
					} else if (v.equals("deathsuicide")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathSuicide"));
					} else if (v.equals("deathvoid")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathVoid"));
					} else if (v.equals("deathshotbyskeleton")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathShotBySkeleton"));
					} else if (v.equals("deathshotbyplayer")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathShotByPlayer"));
					} else if (v.equals("deathshotbyother")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("death"));
					} else if (v.equals("deathlightning")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathLightning"));
					} else if (v.equals("deathtnt")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathtnt"));
					} else if (v.equals("deathcreeper")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathcreeper"));
					} else if (v.equals("deathotherexplosion")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathotherexplosion"));
					} else if (v.equals("deathghast")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathGhast"));
					} else if (v.equals("deathshotbyblaze")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathShotByBlaze"));
					} else if (v.equals("deathblaze")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathBlaze"));
					} else if (v.equals("deathzombie")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathZombie"));
					} else if (v.equals("deathspider")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathSpider"));
					} else if (v.equals("deathcavespider")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathCaveSpider"));
					} else if (v.equals("deathwolf")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathWolf"));
					} else if (v.equals("deathpigzombie")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathPigZombie"));
					} else if (v.equals("deathenderman")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathEnderman"));
					} else if (v.equals("deathlavaslime")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathLavaSlime"));
					} else if (v.equals("deathsilverfish")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathSilverfish"));
					} else if (v.equals("deathvillagergolem")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathVillagerGolem"));
					} else if (v.equals("deathslime")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathSlime"));
					} else if (v.equals("deathgiant")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathGiant"));
					} else if (v.equals("deathenderdragon")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathEnderDragon"));
					} else if (v.equals("deathunknown")){
						sender.sendMessage(ChatColor.YELLOW + _("configDeathMessages", sender.getName()) + p.getConfig().getString("deathUnknown"));
					} else if (v.equals("announcedevelopers")){
						sender.sendMessage(ChatColor.YELLOW + _("configAnnounceDevelopers", sender.getName()) + p.getConfig().getBoolean("announceDevelopers"));
					} else if (v.equals("explodestrength")){
						sender.sendMessage(ChatColor.YELLOW + _("configExplodeStrength", sender.getName()) + p.getConfig().getString("explodeStrength"));
					} else if (v.equals("shutdownkickmessage")){
						sender.sendMessage(ChatColor.YELLOW + _("configShutdownKickMessage", sender.getName()) + p.getConfig().getString("shutdownKickMessage"));
					} else {
						LogHelper.showWarning("configUnrecognized", sender);
					}
				} else if (args[1].equals("set") || args[0].equals("cs")) {
					
					/***
					 * (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) CONFIG VALUES
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
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							p.getConfig().set("defaultLang", (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()));
							p.saveConfig();
							Language.defaultLocale = (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase());
							sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("defaultLang"));
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("tpatimeout")) {
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("tpaTimeout", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
								p.saveConfig();
								try {
									Command_cex_tpa.tTimeout = Integer.parseInt(args[2]);
								} catch (Throwable e) {
									// the tpa command might not be present in the plugin
								}
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("tpaTimeout"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("tpaheretimeout")) {
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("tpahereTimeout", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
								p.saveConfig();
								try {
									Command_cex_tpahere.tTimeout = Integer.parseInt((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()));
								} catch (Throwable e) {
									// the tpahere command might not be present in the plugin
								}
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("tpahereTimeout"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("commandcooldowntime")) {
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("commandCooldownTime", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("commandCooldownTime"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("homequalifytime")) {
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("homeQualifyTime", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("homeQualifyTime"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("quizrepeattime")) {
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("quizRepeatTime", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("quizRepeatTime"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("quizdelay")) {
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("quizDelay", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("quizDelay"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("quizduration")) {
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("quizDuration", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("quizDuration"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("weathernotifytime")) {
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("weatherNotifyTime", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("weatherNotifyTime"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("defaultslapheight")) {
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("defaultSlapHeight", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("defaultSlapHeight"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("maxwarpsperplayer")) {
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("maxWarpsPerPlayer", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("maxWarpsPerPlayer"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("maxipholdtime")) {
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("maxIPholdTime", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("maxIPholdTime"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("mintempbanswarn")) {
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("minTempBansWarn", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("minTempBansWarn"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("joinsilenttime")) {
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("joinSilentTime", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("joinSilentTime"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("jailarea")) {
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("jailArea", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("jailArea"));
							} else {
								// area not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("kamikazetimeout")) {
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("kamikazeTimeout", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("kamikazeTimeout"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("deathgroupscleanuptime")) {
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("deathGroupsCleanupTime", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("deathGroupsCleanupTime"));
							} else {
								// timeout not numeric
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("timedpromotetasktime")) {
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("timedPromoteTaskTime", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
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
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("ecoPromoteTaskTime", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
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
									}, (20 * Integer.parseInt((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()))), (20 * Integer.parseInt((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()))));
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
						if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).equals("add") || (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).equals("add")) {
							@SuppressWarnings("unchecked")
							List<String> l = (List<String>) p.getConfig().getList("timedPromoteExclude");
							String toAdd = (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).equals("add") ? args[3] : args[4];
							if (!l.contains(toAdd)) {
								l.add((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).equals("add") ? args[3] : args[4]);
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
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							p.getConfig().set("motd", (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()));
							p.saveConfig();
							sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + Utils.replaceChatColors(p.getConfig().getString("motd")));
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("motdnewplayer")) {
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							p.getConfig().set("motdNewPlayer", (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()));
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
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("nanoSuitSpeed", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
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
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("nanoSuitJump", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("nanoSuitJump"));
							} else {
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("nanosuitdamage")) {
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("nanoSuitDamage", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("nanoSuitDamage"));
							} else {
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("nanosuittime")) {
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("nanoSuitTime", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("nanoSuitTime"));
							} else {
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("nanosuitrechargetime")) {
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("nanoSuitRechargeTime", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
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
						p.getConfig().set("info", (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()));
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
						p.getConfig().set("ServerOwner", (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("ServerOwner"));
					} else if (v.equals("kittycannonexplosionstrength")) {
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("KittyCannonExplosionStrength", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("KittyCannonExplosionStrength"));
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
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("spawnMobLimit", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("spawnMobLimit"));
							} else {
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("pluginmetrics")) {
						p.getConfig().set("pluginMetrics", !p.getConfig().getBoolean("pluginMetrics"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + (p.getConfig().getBoolean("pluginMetrics") ? ChatColor.GREEN + _("configStatusTrue", sender.getName()) : ChatColor.RED + _("configStatusFalse", sender.getName())));
					} else if (v.equals("deathpvp")) {
						p.getConfig().set("deathPvP", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathPvP"));
					} else if (v.equals("deathdrown")) {
						p.getConfig().set("deathDrown", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathDrown"));
					} else if (v.equals("deathfall")) {
						p.getConfig().set("deathFall", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathFall"));
					} else if (v.equals("deathfire")) {
						p.getConfig().set("deathFire", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathFire"));
					} else if (v.equals("deathlava")) {
						p.getConfig().set("deathLava", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathLava"));
					} else if (v.equals("deathmagic")) {
						p.getConfig().set("deathMagic", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathMagic"));
					} else if (v.equals("deathstarvation")) {
						p.getConfig().set("deathStarvation", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathStarvation"));
					} else if (v.equals("deathpoison")) {
						p.getConfig().set("deathPoison", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathPoison"));
					} else if (v.equals("deathsuffocate")) {
						p.getConfig().set("deathSuffocate", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathSuffocate"));
					} else if (v.equals("deathsuicide")) {
						p.getConfig().set("deathSuicide", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathSuicide"));
					} else if (v.equals("deathvoid")) {
						p.getConfig().set("deathVoid", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathVoid"));
					} else if (v.equals("deathshotbyskeleton")) {
						p.getConfig().set("deathShotBySkeleton", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathShotBySkeleton"));
					} else if (v.equals("deathshotbyplayer")) {
						p.getConfig().set("deathShotByPlayer", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathShotByPlayer"));
					} else if (v.equals("deathshotbyother")) {
						p.getConfig().set("deathShotByOther", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathShotByOther"));
					} else if (v.equals("deathlightning")) {
						p.getConfig().set("deathLightning", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathLightning"));
					} else if (v.equals("deathtnt")) {
						p.getConfig().set("deathTNT", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathTNT"));
					} else if (v.equals("deathcreeper")) {
						p.getConfig().set("deathCreeper", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathCreeper"));
					} else if (v.equals("deathotherexplosion")) {
						p.getConfig().set("deathOtherExplosion", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathOtherExplosion"));
					} else if (v.equals("deathghast")) {
						p.getConfig().set("deathGhast", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathGhast"));
					} else if (v.equals("deathshotbyblaze")) {
						p.getConfig().set("deathShotByBlaze", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathShotByBlaze"));
					} else if (v.equals("deathblaze")) {
						p.getConfig().set("deathBlaze", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathBlaze"));
					} else if (v.equals("deathzombie")) {
						p.getConfig().set("deathZombie", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathZombie"));
					} else if (v.equals("deathspider")) {
						p.getConfig().set("deathSpider", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathSpider"));
					} else if (v.equals("deathcavespider")) {
						p.getConfig().set("deathCaveSpider", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathCaveSpider"));
					} else if (v.equals("deathwolf")) {
						p.getConfig().set("deathWolf", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathWolf"));
					} else if (v.equals("deathpigzombie")) {
						p.getConfig().set("deathPigZombie", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathPigZombie"));
					} else if (v.equals("deathenderman")) {
						p.getConfig().set("deathEnderman", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathEnderman"));
					} else if (v.equals("deathlavaslime")) {
						p.getConfig().set("deathLavaSlime", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathLavaSlime"));
					} else if (v.equals("deathsilverfish")) {
						p.getConfig().set("deathSilverfish", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathSilverfish"));
					} else if (v.equals("deathvillagergolem")) {
						p.getConfig().set("deathVillagerGolem", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathVillagerGolem"));
					} else if (v.equals("deathslime")) {
						p.getConfig().set("deathSlime", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathSlime"));
					} else if (v.equals("deathgiant")) {
						p.getConfig().set("deathGiant", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathGiant"));
					} else if (v.equals("deathenderdragon")) {
						p.getConfig().set("deathEnderDragon", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathEnderDragon"));
					} else if (v.equals("deathunknown")) {
						p.getConfig().set("deathUnknown", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("deathUnknown"));
					} else if (v.equals("announcedevelopers")) {
						p.getConfig().set("announceDevelopers", !p.getConfig().getBoolean("announceDevelopers"));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getBoolean("announceDevelopers"));
					} else if (v.equals("explodestrength")) {
						if ((aLength > 2) && (args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()) != null) {
							if ((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase()).matches(CommandsEX.intRegex)) {
								p.getConfig().set("explodeStrength", Integer.valueOf((args[0].equals("cs") ? args[2].toLowerCase() : args[3].toLowerCase())));
								p.saveConfig();
								sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getInt("explodeStrength"));
							} else {
								LogHelper.showWarning("configProvideNumericValue", sender);
							}
						} else {
							LogHelper.showWarnings(sender, "configUnspecifiedError1", "configUnspecifiedError2", "configUnspecifiedError3");
						}
					} else if (v.equals("shutdownkickmessage")) {
						p.getConfig().set("shutdownKickMessage", (args[0].equals("cs") ? args[2] : args[3]));
						p.saveConfig();
						sender.sendMessage(ChatColor.YELLOW + _("configUpdated", sender.getName()) + ChatColor.WHITE + p.getConfig().getString("shutdownKickMessage"));
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
