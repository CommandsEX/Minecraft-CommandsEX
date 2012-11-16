package com.github.zathrus_writer.commandsex.api.economy;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerEconomyBalanceChangeEvent extends Event implements Cancellable {

	private HandlerList handlers = new HandlerList();
	private boolean cancel = false;
	private String player;
	private double amount;

	public PlayerEconomyBalanceChangeEvent(String player, double amount){
		this.player = player;
		this.amount = amount;
	}

	public String getPlayer(){
		return player;
	}

	public double getAmount(){
		return amount;
	}

	public void setAmount(double amount){
		this.amount = amount;
	}

	public boolean isCancelled() {
		return cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	public HandlerList getHandlers() {
		return handlers;
	}



}
