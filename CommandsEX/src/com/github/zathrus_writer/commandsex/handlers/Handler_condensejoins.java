package com.github.zathrus_writer.commandsex.handlers;

import static com.github.zathrus_writer.commandsex.Language._;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Common;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;
import com.github.zathrus_writer.commandsex.helpers.Utils;
import com.github.zathrus_writer.commandsex.helpers.XMPPer;

public class Handler_condensejoins implements Listener {

	public static List<String> joins = new ArrayList<String>();
	public static Integer lastJoinTime = 0;
	public static List<String> leaves = new ArrayList<String>();
	public static Integer lastLeaveTime = 0;
	
	public static List<String> fakeJoins = new ArrayList<String>();
	public static List<String> fakeLeaves = new ArrayList<String>();
	
	static Integer joinStamp = Utils.getUnixTimestamp(0L);
	static Integer joinFlushTime = CommandsEX.getConf().getInt("joinSilentTime");
	
	static Integer leaveStamp = Utils.getUnixTimestamp(0L);
	static Integer leaveFlushTime = CommandsEX.getConf().getInt("joinSilentTime");
	
	public static Integer joinleaveCheckID;
	
	/***
	 * Activate event listeners.
	 */
	public Handler_condensejoins() {
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);

		// set up the periodic task
		Integer taskTime = CommandsEX.getConf().getInt("joinSilentTime");
		
		// This function checks joins and leaves and displays the join/leave messages to all users if there are any in the queue
		joinleaveCheckID = CommandsEX.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(CommandsEX.plugin, new Runnable() {
			@Override
			public void run() {
				checkLeaves();
				checkJoins();
			}
		}, (20 * taskTime), (20 * taskTime));
	}

	public static void handleJoin(String pName) {
		// get player's name and store it
		if (!joins.contains(pName)) {
			joins.add(pName);
		}
		
		if (lastJoinTime == 0) {
			lastJoinTime = Utils.getUnixTimestamp(0L);
			return;
		}
		
		// check if we haven't reached our flush interval

		// flush joins if interval is reached
		if ((joinStamp - lastJoinTime) >= joinFlushTime) {
			checkJoins();
		}
	}
	
	// Checks the joins and sends them to chat
	public static void checkJoins(){
		List<String> joinNicks = new ArrayList<String>();
		// remove a player if they are invisible
		for(int x=0; x < joins.size(); x++){
			String pName = joins.get(x);
			if (!pName.startsWith("#")){
				if (fakeJoins.contains(pName)){
					fakeJoins.remove(pName);
					
					String nickname = pName;
					try {
						nickname = Nicknames.getNick(pName);
					} catch (Exception ex){}
					joinNicks.add(nickname);
				} else if (Common.isInvisible(pName)){
					joins.remove(pName);
				} else {
					String nickname = pName;
					try {
						nickname = Nicknames.getNick(pName);
					} catch (Exception ex){}
					joinNicks.add(nickname);
				}
			} else {
				joinNicks.add(pName.replaceFirst("#", ""));
			}
		}
		
		joins.clear();

		// save the last name, as we put it to the end of list after an "and"
		Integer jSize = joinNicks.size();
		// make sure the list is not empty
		if (jSize != 0){
			if (jSize > 1) {
				String lName = (String) joinNicks.get(jSize - 1);
				joinNicks.remove(jSize - 1);
				
				List<String> toSend = new ArrayList<String>();
				for (String s : joinNicks){
					if (s.startsWith("#")){
						toSend.add(s.replaceFirst("#", ""));
					} else {
						toSend.add(s);
					}
				}
				
				// send each player the join message in their own language
				for (Player p : Bukkit.getOnlinePlayers()){
					if (p.hasPermission("cex.seejoins")){
						String msg = ChatColor.WHITE + Utils.implode(joinNicks, ", ") + " " + ChatColor.YELLOW + _("and", "") + ChatColor.WHITE + " " + lName + " " + ChatColor.YELLOW + _("chatJoins", p.getName());
						p.sendMessage(msg);
					}
				}
				
				List<String> ingameNicks = new ArrayList<String>();
				for (String s : joinNicks){
					if (!s.startsWith("#")){
						ingameNicks.add(s);
					}
				}
				
				if (ingameNicks.size() > 0){
					// forward the broadcast to XMPP connector, if present
					try {
						// send xmpp message in default language
						String xmppMessage = ChatColor.WHITE + Utils.implode(ingameNicks, ", ") + " " + ChatColor.YELLOW + _("and", "") + ChatColor.WHITE + " " + lName + " " + ChatColor.YELLOW + _("chatJoins", "");
						XMPPer.chatRoom.sendMessage(XMPPer.filterOutgoing(xmppMessage));
					} catch (Throwable e) {
						// nothing bad happens if we don't have XMPP module present :)
					}
				}
			} else {
				// send each player the join message in their own language
				for (Player p : Bukkit.getOnlinePlayers()){
					if (p.hasPermission("cex.seejoins")){
						String msg = ChatColor.WHITE + (String) (joinNicks.get(0).startsWith("#") ? joinNicks.get(0).replaceFirst("#", "") : joinNicks.get(0)) + " " + ChatColor.YELLOW + _("chatJoins", p.getName());
						p.sendMessage(msg);
					}
				}
				
				if (!joinNicks.get(0).startsWith("#")){
					// forward the broadcast to XMPP connector, if present
					try {
						// send the xmpp message in default language
						String xmppMessage = ChatColor.WHITE + (String) joinNicks.get(0) + " " + ChatColor.YELLOW + _("chatJoins", "");
						XMPPer.chatRoom.sendMessage(XMPPer.filterOutgoing(xmppMessage));
					} catch (Throwable e) {
						// nothing bad happens if we don't have XMPP module present :)
					}
				}
			}
		}

		// empty joins array
		lastJoinTime = Utils.getUnixTimestamp(0L);
		joinNicks.clear();

		// save the time when last join message was shown
		lastJoinTime = joinStamp;
	}

	/***
	 * Stores player's name that joined the game and outputs all stored joins
	 * if configured timeout has passed.
	 * @param e
	 * @return
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void condenseJoins(PlayerJoinEvent e) {
		// check if another plugin has already cancelled the join message
		if (e.getJoinMessage() != null || !e.getJoinMessage().equals("")){
		    handleJoin(e.getPlayer().getName());
		    // prevent join message to show up
		    e.setJoinMessage("");	
		}
	}
	
	public static void handleLeave(String pName) {
		// get player's name and store it
		if (!leaves.contains(pName)) {
			leaves.add(pName);
		}

		if (lastLeaveTime == 0) {
			lastLeaveTime = Utils.getUnixTimestamp(0L);
			return;
		}

		// check if we haven't reached our flush interval
		Integer stamp = Utils.getUnixTimestamp(0L);
		Integer flushTime = CommandsEX.getConf().getInt("joinSilentTime");

		// flush leaves if interval is reached
		if ((stamp - lastLeaveTime) >= flushTime) {
			checkLeaves();
		}
	}

	// Checks the leaves and sends them to chat
	public static void checkLeaves(){
		List<String> leaveNicks = new ArrayList<String>();
		// remove a player if they are invisible
		for(int x=0; x < leaves.size(); x++){
			String pName = leaves.get(x);
			if (!pName.startsWith("#")){
				if (fakeLeaves.contains(pName)){
					fakeLeaves.remove(pName);
					
					String nickname = pName;
					try {
						nickname = Nicknames.getNick(pName);
					} catch (Exception ex){}
					leaveNicks.add(nickname);
				} else if (Common.isInvisible(pName)){
					leaves.remove(pName);
				} else {
					String nickname = pName;
					try {
						nickname = Nicknames.getNick(pName);
					} catch (Exception ex){}
					leaveNicks.add(nickname);
				}
			} else {
				leaveNicks.add(pName);
			}
		}
		
		leaves.clear();
		
		// save the last name, as we put it to the end of list after an "and"
		Integer lSize = leaveNicks.size();
		// make sure the list is not empty
		if (lSize != 0){
			if (lSize > 1) {
				String lName = (String) leaveNicks.get(lSize - 1);
				leaveNicks.remove(lSize - 1);
				
				List<String> toSend = new ArrayList<String>();
				for (String s : leaveNicks){
					if (s.startsWith("#")){
						toSend.add(s.replaceFirst("#", ""));
					} else {
						toSend.add(s);
					}
				}
				
				// send the leave message to the player in their own language
				for (Player p : Bukkit.getOnlinePlayers()){
					if (p.hasPermission("cex.seeleaves")){
						String msg = ChatColor.WHITE + Utils.implode(leaveNicks, ", ") + " " + ChatColor.YELLOW + _("and", "") + ChatColor.WHITE + " " + lName + " " + ChatColor.YELLOW + _("chatLeaves", p.getName());
						p.sendMessage(msg);
					}
				}
				
				List<String> ingameNicks = new ArrayList<String>();
				for (String s : leaveNicks){
					if (!s.startsWith("#")){
						ingameNicks.add(s);
					}
				}
				
				if (ingameNicks.size() > 0){
					// forward the broadcast to XMPP connector, if present
					try {
						// send the xmpp message in the default language
						String xmppMessage = ChatColor.WHITE + Utils.implode(ingameNicks, ", ") + " " + _("and", "") + " " + lName + " " + ChatColor.YELLOW + _("chatLeaves", "");
						XMPPer.chatRoom.sendMessage(XMPPer.filterOutgoing(xmppMessage));
					} catch (Throwable e) {
						// nothing bad happens if we don't have XMPP module present :)
					}
				}
			} else {
				// send the leave message to the player in their own language
				for (Player p : Bukkit.getOnlinePlayers()){
					if (p.hasPermission("cex.seeleaves")){
						String msg = ChatColor.WHITE + (String) (leaveNicks.get(0).startsWith("#") ? leaveNicks.get(0).replaceFirst("#", "") : leaveNicks.get(0)) + " " + ChatColor.YELLOW + _("chatLeaves", p.getName());
						p.sendMessage(msg);
					}
				}
				
				if (!leaveNicks.get(0).startsWith("#")){
					// forward the broadcast to XMPP connector, if present
					try {
						// send the xmpp message in the default language
						String xmppMessage = ChatColor.WHITE + (String) leaveNicks.get(0) + " " + ChatColor.YELLOW + _("chatLeaves", "");
						XMPPer.chatRoom.sendMessage(XMPPer.filterOutgoing(xmppMessage));
					} catch (Throwable e) {
						// nothing bad happens if we don't have XMPP module present :)
					}
				}
			}

			// empty leaves array
			lastLeaveTime = Utils.getUnixTimestamp(0L);
			leaveNicks.clear();

			// save the time when last leave message was shown
			lastLeaveTime = leaveStamp;
		}
	}

	/***
	 * Stores player's name that left the game and outputs all stored leaves
	 * if configured timeout has passed.
	 * @param e
	 * @return
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void condenseLeaves(PlayerQuitEvent e) {
		// check if another plugin has not already cancelled the quit message
		if (e.getQuitMessage() != null || !e.getQuitMessage().equals("")){
			handleLeave(e.getPlayer().getName());		
			// prevent quit message to show up
			e.setQuitMessage("");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void condenseLeaves1(PlayerKickEvent e){
		// prevent kick message showing
		e.setLeaveMessage("");
	}
	
}
