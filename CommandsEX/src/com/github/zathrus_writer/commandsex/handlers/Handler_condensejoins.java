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
import com.github.zathrus_writer.commandsex.helpers.Common;
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
			if (!Common.invisiblePlayers.contains(pName)){
				joins.add(pName);
			}
		}
		
		// check if we haven't reached our flush interval
		Integer stamp = Utils.getUnixTimestamp(0L);
		Integer flushTime = CommandsEX.getConf().getInt("joinSilentTime");
		
		// flush joins if interval is reached
		if ((stamp - lastJoinTime) >= flushTime) {
			
			// remove a player if they are invisible
			if (Common.invisiblePlayers.contains(pName)){
				joins.remove(pName);
			}
			
			// save the last name, as we put it to the end of list after an "and"
			Integer jSize = joins.size();
			// make sure the list is not empty
			if (jSize != 0){
				if (jSize > 1) {
					String lName = (String) joins.get(jSize - 1);
					joins.remove(jSize - 1);
					String msg = ChatColor.WHITE + Utils.implode(joins, ", ") + " " + _("and", "") + " " + lName + " " + ChatColor.YELLOW + _("chatJoins", "");
					CommandsEX.plugin.getServer().broadcast(msg, "cex.seejoins");
					// forward the broadcast to XMPP connector, if present
					try {
						XMPPer.chatRoom.sendMessage(XMPPer.filterOutgoing(msg));
					} catch (Throwable e) {
						// nothing bad happens if we don't have XMPP module present :)
					}
				} else {
					String msg = ChatColor.WHITE + (String) joins.get(0) + " " + ChatColor.YELLOW + _("chatJoins", "");
					CommandsEX.plugin.getServer().broadcast(msg, "cex.seejoins");
					// forward the broadcast to XMPP connector, if present
					try {
						XMPPer.chatRoom.sendMessage(XMPPer.filterOutgoing(msg));
					} catch (Throwable e) {
						// nothing bad happens if we don't have XMPP module present :)
					}
				}
			}
			
			// empty joins array
			lastJoinTime = Utils.getUnixTimestamp(0L);
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
			if (!Common.invisiblePlayers.contains(pName)){
				leaves.add(pName);
			}
		}
		
		// check if we haven't reached our flush interval
		Integer stamp = Utils.getUnixTimestamp(0L);
		Integer flushTime = CommandsEX.getConf().getInt("joinSilentTime");
		
		// flush leaves if interval is reached
		if ((stamp - lastLeaveTime) >= flushTime) {
			
			// remove a player if they are invisible
			if (Common.invisiblePlayers.contains(pName)){
				leaves.remove(pName);
			}
			
			// save the last name, as we put it to the end of list after an "and"
			Integer lSize = leaves.size();
			// make sure the list is not empty
			if (lSize != 0){
				if (lSize > 1) {
					String lName = (String) leaves.get(lSize - 1);
					leaves.remove(lSize - 1);
					String msg = ChatColor.WHITE + Utils.implode(leaves, ", ") + " " + _("and", "") + " " + lName + " " + ChatColor.YELLOW + _("chatLeaves", "");
					CommandsEX.plugin.getServer().broadcast(msg, "cex.seeleaves");
					// forward the broadcast to XMPP connector, if present
					try {
						XMPPer.chatRoom.sendMessage(XMPPer.filterOutgoing(msg));
					} catch (Throwable e) {
						// nothing bad happens if we don't have XMPP module present :)
					}
				} else {
					String msg = ChatColor.WHITE + (String) leaves.get(0) + " " + ChatColor.YELLOW + _("chatLeaves", "");
					CommandsEX.plugin.getServer().broadcast(msg, "cex.seeleaves");
					// forward the broadcast to XMPP connector, if present
					try {
						XMPPer.chatRoom.sendMessage(XMPPer.filterOutgoing(msg));
					} catch (Throwable e) {
						// nothing bad happens if we don't have XMPP module present :)
					}
				}
			}
			
			// empty leaves array
			lastLeaveTime = Utils.getUnixTimestamp(0L);
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
