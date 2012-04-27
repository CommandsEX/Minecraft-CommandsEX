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
import com.github.zathrus_writer.commandsex.helpers.XMPPer;

public class Handler_condensejoins implements Listener {

	public static List<String> joins = new ArrayList<String>();
	public static Integer lastJoinTime = 0;
	public static List<String> leaves = new ArrayList<String>();
	public static Integer lastLeaveTime = 0;
	
	/***
	 * Activate event listeners.
	 */
	public Handler_condensejoins() {
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	
	public static void handleJoin(String pName) {
		if (lastJoinTime == 0) {
			lastJoinTime = Utils.getUnixTimestamp(0L);
			return;
		}

		// get player's name and store it
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
				String msg = Utils.implode(joins, ", ") + " " + _("and", "") + " " + lName + " " + _("chatJoins", "");
				CommandsEX.plugin.getServer().broadcast(ChatColor.YELLOW + msg, "cex.seejoins");
				// forward the broadcast to XMPP connector, if present
				try {
					XMPPer.chatRoom.sendMessage(XMPPer.filterOutgoing(msg));
				} catch (Throwable e) {
					// nothing bad happens if we don't have XMPP module present :)
				}
			} else {
				String msg = (String) joins.get(0) + " " + _("chatJoins", "");
				CommandsEX.plugin.getServer().broadcast(ChatColor.YELLOW + msg, "cex.seejoins");
				// forward the broadcast to XMPP connector, if present
				try {
					XMPPer.chatRoom.sendMessage(XMPPer.filterOutgoing(msg));
				} catch (Throwable e) {
					// nothing bad happens if we don't have XMPP module present :)
				}
			}
			
			// empty joins array
			joins.clear();
			
			// save the time when last join message was shown
			lastJoinTime = stamp;
		}
	}
	
	/***
	 * Stores player's name that joined the game and outputs all stored joins
	 * if configured timeout has passed.
	 * @param e
	 * @return
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void condenseJoins(PlayerJoinEvent e) {
		handleJoin(e.getPlayer().getName());
		// prevent join message to show up
		e.setJoinMessage("");
	}
	
	public static void handleLeave(String pName) {
		if (lastLeaveTime == 0) {
			lastLeaveTime = Utils.getUnixTimestamp(0L);
			return;
		}

		// get player's name and store it
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
				String msg = Utils.implode(leaves, ", ") + " " + _("and", "") + " " + lName + " " + _("chatLeaves", "");
				CommandsEX.plugin.getServer().broadcast(ChatColor.YELLOW + msg, "cex.seeleaves");
				// forward the broadcast to XMPP connector, if present
				try {
					XMPPer.chatRoom.sendMessage(XMPPer.filterOutgoing(msg));
				} catch (Throwable e) {
					// nothing bad happens if we don't have XMPP module present :)
				}
			} else {
				String msg = (String) leaves.get(0) + " " + _("chatLeaves", "");
				CommandsEX.plugin.getServer().broadcast(ChatColor.YELLOW + msg, "cex.seeleaves");
				// forward the broadcast to XMPP connector, if present
				try {
					XMPPer.chatRoom.sendMessage(XMPPer.filterOutgoing(msg));
				} catch (Throwable e) {
					// nothing bad happens if we don't have XMPP module present :)
				}
			}
			
			// empty leaves array
			leaves.clear();
			
			// save the time when last leave message was shown
			lastLeaveTime = stamp;
		}
	}
	
	/***
	 * Stores player's name that left the game and outputs all stored leaves
	 * if configured timeout has passed.
	 * @param e
	 * @return
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void condenseLeaves(PlayerQuitEvent e) {
		handleLeave(e.getPlayer().getName());		
		// prevent quit message to show up
		e.setQuitMessage("");
	}
	
}
