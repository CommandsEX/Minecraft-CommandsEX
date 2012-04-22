package com.github.zathrus_writer.commandsex.handlers;

import static com.github.zathrus_writer.commandsex.Language._;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Handler_condensejoins implements Listener {

	public static List<String> joins = new ArrayList<String>();
	public static Integer lastJoinTime = 0;
	public static List<String> leaves = new ArrayList<String>();
	public static Integer lastLeaveTime = 0;
	
	/***
	 * Loads existing chat replacements from the config file and activates event listeners.
	 */
	public Handler_condensejoins() {
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	
	/***
	 * Stores player's name that joined the game and outputs all stored joins
	 * if configured timeout has passed.
	 * @param e
	 * @return
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void condenseJoins(PlayerJoinEvent e) {
		if (lastJoinTime == 0) {
			lastJoinTime = Utils.getUnixTimestamp(0L);
			return;
		}

		// get player's name and store it
		String pName = e.getPlayer().getName();
		if (!joins.contains(pName)) {
			joins.add(pName);
		}
		
		// check if we haven't reached our flush interval
		Integer stamp = Utils.getUnixTimestamp(0L);
		Integer flushTime = CommandsEX.plugin.getConfig().getInt("joinSilentTime");
		
		// flush joins if interval is reached
		if ((stamp - lastJoinTime) >= flushTime) {
			// save the last name, as we put it to the end of list after an "and"
			Integer jSize = joins.size();
			if (jSize > 1) {
				String lName = (String) joins.get(jSize - 1);
				joins.remove(jSize - 1);
				CommandsEX.plugin.getServer().broadcast(ChatColor.YELLOW + Utils.implode(joins, ", ") + " " + _("and", "") + " " + lName + " " + _("chatJoins", ""), "cex.seejoins");
			} else {
				CommandsEX.plugin.getServer().broadcast(ChatColor.YELLOW + (String) joins.get(0) + " " + _("chatJoins", ""), "cex.seejoins");
			}
			
			// empty joins array
			joins.clear();
			
			// save the time when last join message was shown
			lastJoinTime = stamp;
		}
		
		// prevent join message to show up
		e.setJoinMessage("");
	}
	
	
	/***
	 * Stores player's name that left the game and outputs all stored leaves
	 * if configured timeout has passed.
	 * @param e
	 * @return
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void condenseLeaves(PlayerQuitEvent e) {
		if (lastLeaveTime == 0) {
			lastLeaveTime = Utils.getUnixTimestamp(0L);
			return;
		}

		// get player's name and store it
		String pName = e.getPlayer().getName();
		if (!leaves.contains(pName)) {
			leaves.add(pName);
		}
		
		// check if we haven't reached our flush interval
		Integer stamp = Utils.getUnixTimestamp(0L);
		Integer flushTime = CommandsEX.plugin.getConfig().getInt("joinSilentTime");
		
		// flush leaves if interval is reached
		if ((stamp - lastLeaveTime) >= flushTime) {
			// save the last name, as we put it to the end of list after an "and"
			Integer lSize = leaves.size();
			if (lSize > 1) {
				String lName = (String) leaves.get(lSize - 1);
				leaves.remove(lSize - 1);
				CommandsEX.plugin.getServer().broadcast(ChatColor.YELLOW + Utils.implode(leaves, ", ") + " " + _("and", "") + " " + lName + " " + _("chatLeaves", ""), "cex.seeleaves");
			} else {
				CommandsEX.plugin.getServer().broadcast(ChatColor.YELLOW + (String) leaves.get(0) + " " + _("chatLeaves", ""), "cex.seeleaves");
			}
			
			// empty leaves array
			leaves.clear();
			
			// save the time when last leave message was shown
			lastLeaveTime = stamp;
		}
		
		// prevent quit message to show up
		e.setQuitMessage("");
	}
	
}
