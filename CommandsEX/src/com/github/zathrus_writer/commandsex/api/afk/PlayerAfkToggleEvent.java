package com.github.zathrus_writer.commandsex.api.afk;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerAfkToggleEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private Player player;
	private boolean isAfk;
	private boolean cancelled = false;
	
	public PlayerAfkToggleEvent(Player player, boolean state){
		this.player = player;
		isAfk = state;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean arg0) {
		cancelled = arg0;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public boolean isAfk(){
		return isAfk;
	}

}
