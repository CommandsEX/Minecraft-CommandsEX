package com.github.zathrus_writer.commandsex.api.messaging;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Nicknames;

public class Messaging {

	private static HashMap<String, String> lastMessageFrom = new HashMap<String, String>();
	
	/**
	 * Sends a message to another player
	 * @param sender The user who sent the message
	 * @param target The target to display the message to
	 * @param message The message to display
	 * @param isReply Whether this is a reply or not
	 * @return Whether sending of the message was successful
	 */
	
	public static boolean sendMessage(CommandSender sender, Player target, String message, boolean isReply){
		// call event
		PlayerRecievePrivateMessageEvent event = new PlayerRecievePrivateMessageEvent(sender, target, message, isReply);
		Bukkit.getServer().getPluginManager().callEvent(event);

		if (event.isCancelled()){
			return false;
		}
		
		target.sendMessage(ChatColor.GRAY + "(" + Nicknames.getNick(sender.getName()) + ChatColor.GRAY + " -> " + Nicknames.getNick(target.getName()) + ChatColor.GRAY + ") " + ChatColor.AQUA + event.getMessage());
		sender.sendMessage(ChatColor.GRAY + "(" + Nicknames.getNick(sender.getName()) + ChatColor.GRAY + " -> " + Nicknames.getNick(target.getName()) + ChatColor.GRAY + ") " + ChatColor.AQUA + event.getMessage());

		if (sender instanceof Player){
			if (lastMessageFrom.containsKey(target.getName())){
				lastMessageFrom.remove(target.getName());
			}
			
			lastMessageFrom.put(target.getName(), sender.getName());
		}
		
		return true;
	}
	
	/**
	 * Get's the last player that sent another player a message
	 * @param player The player to get the last recieved message sender from
	 * @return The last recieved message sender
	 */
	
	public static String getLastMessageFrom(Player player){
		if (!hasLastMessageFrom(player)){
			return null;
		}
		
		return lastMessageFrom.get(player.getName());
	}
	
	/**
	 * Checks if another player has sent this player a message in this session
	 * @param player The player to check if they have recieved a message during this session
	 * @return Whether they recieved a message from a player during this session
	 */
	
	public static boolean hasLastMessageFrom(Player player){
		return lastMessageFrom.containsKey(player.getName());
	}
	
	/**
	 * Sets the last player to send a message to another player
	 * @param player The player to set the last recieved sender for
	 * @param from The player to set as the last message sender
	 */
	
	public static void setLastMessageFrom(Player player, Player from){
		removeLastMessageFrom(player);
		lastMessageFrom.put(player.getName(), from.getName());
	}
	
	/**
	 * Clears the last message sender
	 * @param player The player to clear the last message sender for
	 */
	
	public static void removeLastMessageFrom(Player player){
		if (lastMessageFrom.containsKey(player.getName())){
			lastMessageFrom.remove(player.getName());
		}
	}

}
