package com.github.zathrus_writer.commandsex.api;

import java.util.Collection;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.Occupant;

import com.github.zathrus_writer.commandsex.handlers.Handler_xmpp;
import com.github.zathrus_writer.commandsex.helpers.XMPPer;

public class XMPPAPI {

	/**
	 * Determines whether or not XMPP/Jabber has been enabled in the CommandsEX Builder
	 * @return Returns the XMPP/Jabber enabled status
	 */
	
	public static boolean isXMPPEnabled(){
		try {
			new Handler_xmpp();
			return true;
		} catch (Exception ex){
			return false;
		}
	}
	
	/**
	 * Sends a message to the XMPP chatroom
	 * @param message The message to be sent
	 */
	
	public static void sendMessage(String message){
		try {
			XMPPer.chatRoom.sendMessage(XMPPer.filterOutgoing(message));
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Bans a user from the chat room
	 * @param jid The user's JID to ban
	 * @param banReason The reason for the ban
	 */
	
	public static void banUser(String jid, String banReason){
		try {
			XMPPer.chatRoom.banUser(jid, banReason);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Bans multiple users at once
	 * @param jids A collection of JIDs in String format to ban
	 */
	
	public static void banUsers(Collection <String> jids){
		try {
			XMPPer.chatRoom.banUsers(jids);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the chatroom subject
	 * @param subject The message to set the subject to
	 */
	
	public static void setSubject(String subject){
		try {
			XMPPer.chatRoom.changeSubject(subject);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets all participents
	 * @return Returns a Collection of Occupants
	 */
	
	public static Collection<Occupant> getParticipents(){
		try {
			return XMPPer.chatRoom.getParticipants();
		} catch (XMPPException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Gets all online admins in the chat room
	 * @return Returns a Collection of Affiliate's
	 */
	
	public static Collection<Affiliate> getAdmins(){
		try {
			return XMPPer.chatRoom.getAdmins();
		} catch (XMPPException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Gets the chat room
	 * @return Returns the instance of MultiUserChat for the chatroom
	 */
	
	public static MultiUserChat getChatRoom(){
		return XMPPer.chatRoom;
	}
}
