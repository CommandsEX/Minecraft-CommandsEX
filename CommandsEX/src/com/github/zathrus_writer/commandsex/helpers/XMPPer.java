package com.github.zathrus_writer.commandsex.helpers;

import static com.github.zathrus_writer.commandsex.Language._;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.Occupant;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;
import org.jivesoftware.smackx.muc.SubjectUpdatedListener;
import org.jivesoftware.smackx.muc.UserStatusListener;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.handlers.Handler_condensejoins;
import com.github.zathrus_writer.commandsex.helpers.scripting.CommanderCommandSender;

public class XMPPer implements Listener, PacketListener, SubjectUpdatedListener, UserStatusListener, ParticipantStatusListener {
	
	public static String chatPrefix = "";
	public static Connection xmppConnection;
	public static MultiUserChat chatRoom;
	public static Map<String, String> participantNicks;
	public static String cmdPrefix;
	public static final CommanderCommandSender ccs = new CommanderCommandSender();

	public XMPPer() {
		LogHelper.logInfo("[CommandsEX] " + _("xmppConnecting", ""));
		FileConfiguration cnf = CommandsEX.plugin.getConfig();
		cmdPrefix = cnf.getString("xmppCommandPrefix", "#");
		participantNicks = new HashMap<String, String>();
		xmppConnection = new XMPPConnection(cnf.getString("xmppHost", "localhost"));
		try {
			xmppConnection.connect();
			if (cnf.getString("xmppUser", "").equals("")) {
				LogHelper.logInfo(_("xmppAnonymousLogin", ""));
				xmppConnection.loginAnonymously();
			} else {
				xmppConnection.login(cnf.getString("xmppUser"), cnf.getString("xmppPassword", ""));
			}
			// Only do this if the connection didn't fail
			DiscussionHistory history = new DiscussionHistory();
			history.setMaxStanzas(0);
			chatRoom = new MultiUserChat(xmppConnection, cnf.getString("xmppRoom.name"));
			chatRoom.addMessageListener(this);
			chatRoom.addParticipantStatusListener(this);
			chatRoom.addSubjectUpdatedListener(this);
			chatRoom.addUserStatusListener(this);
			try {
				chatRoom.join(cnf.getString("xmppBotNick", "CommandsEX"), cnf.getString("xmppRoom.password", ""), history, SmackConfiguration.getPacketReplyTimeout());
				for (Occupant occupant : chatRoom.getParticipants()) {
					participantNicks.put(occupant.getJid(), occupant.getNick());
				}
			} catch(XMPPException e) {
				LogHelper.logSevere("[CommandsEX] " + _("xmppUnableToJoinRoom", ""));
				LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
			}
		} catch (XMPPException e) {
			LogHelper.logSevere("[CommandsEX] " + _("xmppConnectionFailed", ""));
			LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
		}
		// disconnect on plugin disable
		CommandsEX.onDisableFunctions.add("com.github.zathrus_writer.commandsex.handlers.Handler_xmpp#####onDisable");
		// listen to chat events in this class
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	
	/***
	 * Closes XMPP connection on plugin disable.
	 */
	public static void onDisable(CommandsEX p) {
		if (xmppConnection.isConnected()) {
			xmppConnection.disconnect();
		}
		xmppConnection = null;
	}

	public static String filterOutgoing(String input) {
		StringBuilder in = new StringBuilder(input);
		while (in.indexOf("\u00a7") != -1) {
			in.replace(in.indexOf("\u00a7"), in.indexOf("\u00a7") + 2, "");
		}
		return in.toString();
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void sendJoinMessage(PlayerJoinEvent e) {
		try {
			chatRoom.sendMessage(filterOutgoing(e.getJoinMessage()));
		} catch(XMPPException ex) {
			LogHelper.logDebug("Message: " + ex.getMessage() + ", cause: " + ex.getCause());
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void interceptChat(PlayerChatEvent e) {
		try {
			chatRoom.sendMessage(filterOutgoing(String.format(e.getFormat(), e.getPlayer().getName(), e.getMessage())));
		} catch(XMPPException ex) {
			LogHelper.logDebug("Message: " + ex.getMessage() + ", cause: " + ex.getCause());
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void sendLeaveMessage(PlayerQuitEvent e) {
		try {
			chatRoom.sendMessage(filterOutgoing(e.getQuitMessage()));
		} catch(XMPPException ex) {
			LogHelper.logDebug("Message: " + ex.getMessage() + ", cause: " + ex.getCause());
		}
	}

	public void processPacket(Packet packet) {
		if (packet instanceof Message) {
			if (!chatRoom.getOccupant(packet.getFrom()).getNick().equals(chatRoom.getNickname())) {
				Message message = (Message)packet;
				if (message.getBody().startsWith(cmdPrefix)) {
					// execute command on the server as Console when this command comes from a trusted person
					if (CommandsEX.plugin.getConfig().getList("xmppAdmins").contains(chatRoom.getOccupant(message.getFrom()).getNick())) {
						String cmd = message.getBody().substring(cmdPrefix.length());
						CommandsEX.plugin.getServer().dispatchCommand(ccs, cmd);
					}
				} else {
					Occupant actor = chatRoom.getOccupant(message.getFrom());
					String actorNick = (actor.getRole().equals("moderator") ? "@" : actor.getRole().equals("participant") ? "+" : "") + actor.getNick();
					String body = message.getBody();
					CommandsEX.plugin.getServer().broadcastMessage(chatPrefix + actorNick + ": " + body);
				}
			}
		} else {
			LogHelper.logWarning(_("xmppUnknownPacket", "") + packet.getClass().toString());
		}
	}

	public void kicked(String actor, String reason) {
		/*String message = "Kicked by " + actor;
		if (reason != null && !reason.equals(""))
			message += " for the reason " + reason;
		CommandsEX.plugin.getServer().broadcastMessage(chatPrefix + "Kicked from room");
		*/
	}

	public void voiceGranted() {
	}

	public void voiceRevoked() {
	}

	public void banned(String actor, String reason) {
		/*String message = "Kicked by " + actor;
		if (reason != null && !reason.equals(""))
			message += " for the reason " + reason;
		CommandsEX.plugin.getServer().broadcastMessage(chatPrefix + "Banned from room");
		*/
	}

	public void membershipGranted() {
	}

	public void membershipRevoked() {
	}

	public void moderatorGranted() {
	}

	public void moderatorRevoked() {
	}

	public void ownershipGranted() {
	}

	public void ownershipRevoked() {
	}

	public void adminGranted() {
	}

	public void adminRevoked() {
	}

	public void subjectUpdated(String subject, String from) {
		//CommandsEX.plugin.getServer().broadcastMessage(chatPrefix + "Subject changed to " + subject);
	}

	public void joined(String participant) {
		Occupant actor = chatRoom.getOccupant(participant);
		String joiner = chatPrefix + actor.getNick();
		try {
			// try to condense this join if we have this plugin part available
			Handler_condensejoins.handleJoin(joiner);
		} catch (Throwable e) {
			if (!participantNicks.containsKey(participant)) {
				CommandsEX.plugin.getServer().broadcastMessage(joiner + " " + _("xmppJoin", ""));
				participantNicks.put(participant, actor.getNick());
			}
		}
	}

	public void left(String participant) {
		String actorNick = chatPrefix + participantNicks.get(participant);
		try {
			// try to condense this leave if we have this plugin part available
			Handler_condensejoins.handleLeave(actorNick);
		} catch (Throwable e) {
			CommandsEX.plugin.getServer().broadcastMessage(actorNick + " " + _("xmppLeave", ""));
			participantNicks.remove(participant);
		}
	}

	public void kicked(String participant, String actor, String reason) {
		/*String message = participantNicks.get(participant) + " was kicked by " + actor;
		if (reason != null && !reason.equals(""))
			message += " for the reason " + reason;
		CommandsEX.plugin.getServer().broadcastMessage(chatPrefix + message);
		participantNicks.remove(participant);
		*/
	}

	public void voiceGranted(String participant) {
	}

	public void voiceRevoked(String participant) {
	}

	public void banned(String participant, String actor, String reason) {
		/*
		String message = participantNicks.get(participant) + " was banned by " + actor;
		if (reason != null && !reason.equals(""))
			message += " for the reason " + reason;
		CommandsEX.plugin.getServer().broadcastMessage(chatPrefix + message);
		participantNicks.remove(participant);
		*/
	}

	public void membershipGranted(String participant) {
	}

	public void membershipRevoked(String participant) {
	}

	public void moderatorGranted(String participant) {
	}

	public void moderatorRevoked(String participant) {
	}

	public void ownershipGranted(String participant) {
	}

	public void ownershipRevoked(String participant) {
	}

	public void adminGranted(String participant) {
	}

	public void adminRevoked(String participant) {
	}

	public void nicknameChanged(String participant, String newNickname) {
		String message = participantNicks.get(participant) + " " + _("xmppNameChange", "") + newNickname;
		CommandsEX.plugin.getServer().broadcastMessage(chatPrefix + message);
		participantNicks.put(participant, newNickname);
	}
}
