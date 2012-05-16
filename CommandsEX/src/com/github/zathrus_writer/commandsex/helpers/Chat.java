package com.github.zathrus_writer.commandsex.helpers;

import static com.github.zathrus_writer.commandsex.Language._;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;

/***
 * Contains set of commands and helpers connected to chat administration.
 * @author zathrus-writer
 *
 */
public class Chat implements Listener {
	
	public static Map<String, String> mutedPlayers = new HashMap<String, String>();
	public static List<String> spyActivePlayers = new ArrayList<String>();
	public static Chat plugin = null;

	/***
	 * store our plugin instance to handle events listening
	 */
	public Chat() {
		plugin = this;
	}
	
	/***
	 * MUTE - mutes a player, optionally for the given period of time only
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean mute(CommandSender sender, String[] args, String command, String alias) {
		// check if requested player is online
		Player p = Bukkit.getServer().getPlayer(args[0]);
		String pName = "";
		if (p == null) {
			// requested player not found
			LogHelper.showWarning("invalidPlayer", sender);
			return true;
		} else {
			pName = p.getName();
		}

		// if we don't have time parameter, mute player indefinitelly (until server restarts that it)
		Integer plusValue = 0;
		if (args.length == 1) {
			mutedPlayers.put(pName, "0-0");
		} else {
			// match time value with regex
			try {
				Map<String, Integer> t = Utils.parseTime(args);
				// save a timestamp after which our player should not be muted anymore
				Integer stamp = Utils.getUnixTimestamp(0L);
				plusValue = (t.get("days") * 86400) + (t.get("hours") * 3600) + (t.get("minutes") * 60) + t.get("seconds");

				if (plusValue > 0) {
					// start a timeout function to unmute the player after his mute period has passed
					Integer timerValue = CommandsEX.plugin.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new Runnable() {
						@Override
						public void run() {
							// run through all muted players and unmute those who should be unmuted
							Iterator<Entry<String, String>> it = Chat.mutedPlayers.entrySet().iterator();
							Integer stamp = Utils.getUnixTimestamp(0L);
							List<String> toUnmute = new ArrayList<String>();
							while (it.hasNext()) {
								Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
								String[] s = pairs.getValue().split("-");
								Integer muteStamp = Integer.parseInt(s[0]);
								
								if (muteStamp <= stamp) {
									toUnmute.add(pairs.getKey());
								}
							}
							
							if (toUnmute.size() > 0) {
								for (String s : toUnmute) {
									Chat.mutedPlayers.remove(s);
									// send message to player that he's been unmuted, if he's still online
									Player p = Bukkit.getServer().getPlayer(s);
									if (p != null) {
										LogHelper.showInfo("chatPlayerYouAreUnmuted", p);
									}
								}
							}
						}
					}, (20 * (plusValue + 4)));

					mutedPlayers.put(pName, (stamp + plusValue) + "-" + timerValue);
				} else {
					mutedPlayers.put(pName, "0-0");
				}
			} catch (Throwable e) {
				// Syntax error in the regular expression
				LogHelper.showWarning("internalError", sender);
				LogHelper.logSevere("[CommandsEX] " + _("timeRegexError", ""));
				LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
				return true;
			}
		}

		// create Chat class instance, if not instantiated yet to allow for event listening
		if (plugin == null) {
			new Chat();
		} else {
			// activate listener, we have new players to mute
			CommandsEX.plugin.getServer().getPluginManager().registerEvents(Chat.plugin, CommandsEX.plugin);
		}
		
		// inform muted player and command sender
		LogHelper.showInfo("[" + pName + " #####chatPlayerMuted#####" + ((plusValue > 0) ? "for#####" + "[ " + plusValue + "#####[ #####seconds" : "chatPlayerMutedIndefinite"), sender);
		LogHelper.showInfo("chatPlayerYouAreMuted", p);
		
		return true;
	}
	
	/***
	 * UNMUTE - unmutes a player
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean unmute(CommandSender sender, String[] args, String command, String alias) {
		// check if requested player is muted
		if (mutedPlayers.containsKey(args[0])) {
			// remove player's unmute timer as well, if set
			String[] s = mutedPlayers.get(args[0]).split("-");
			if (s[1] != "0") {
				CommandsEX.plugin.getServer().getScheduler().cancelTask(Integer.parseInt(s[1]));
			}
			mutedPlayers.remove(args[0]);
			LogHelper.showInfo("[" + args[0] + " #####chatPlayerUnmuted", sender);
			
			// unregister listener if we don't need it anymore
			if (mutedPlayers.size() == 0) {
				HandlerList.unregisterAll(Chat.plugin);
			}
		} else {
			// player is not muted
			LogHelper.showInfo("[" + args[0] + " #####chatPlayerNotMuted", sender);
		}
	
		return true;
	}
	
	/***
	 * Listens to chat events and mutes players that should be muted.
	 * @param e
	 * @return
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void checkMutes(PlayerChatEvent e) {
		if (e.isCancelled()) return;

		if (Chat.mutedPlayers.containsKey(e.getPlayer().getName())) {
			e.setCancelled(true);
		}
	}
}