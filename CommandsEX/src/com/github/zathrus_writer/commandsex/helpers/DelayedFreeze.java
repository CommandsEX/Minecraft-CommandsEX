package com.github.zathrus_writer.commandsex.helpers;

import org.bukkit.command.CommandSender;

public class DelayedFreeze implements Runnable {
	
	private CommandSender sender;
	private String[] args;
	private String command;
	private String alias;
	private Boolean omitMessage;
	
	public DelayedFreeze(CommandSender sender, String[] args, String command, String alias, Boolean... omitMessage) {
		this.sender = sender;
		this.args = args;
		this.command = command;
		this.alias = alias;
		this.omitMessage = (omitMessage.length > 0);
	}
	
	public void run() {
		Common.freeze(this.sender, this.args, this.command, this.alias, this.omitMessage);
	}
}
