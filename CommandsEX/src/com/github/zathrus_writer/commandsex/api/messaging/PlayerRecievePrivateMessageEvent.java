package com.github.zathrus_writer.commandsex.api.messaging;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerRecievePrivateMessageEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private CommandSender sender;
	private Player reciever;
	private String message;
	private boolean cancelled = false;
	private boolean isReply;
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public PlayerRecievePrivateMessageEvent(CommandSender sender, Player reciever, String message, boolean isReply){
		this.sender = sender;
		this.reciever = reciever;
		this.message = message;
		this.isReply = isReply;
	}
	
	public CommandSender getSender(){
		return sender;
	}
	
	public Player getReciever(){
		return reciever;
	}
	
	public String getMessage(){
		return message;
	}
	
	public void setMessage(String message){
		this.message = message;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public void setReply(boolean isReply){
		this.isReply = isReply;
	}
	
	public boolean isReply(){
		return isReply;
	}
}
