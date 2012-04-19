package com.github.zathrus_writer.commandsex.helpers.scripting;

import org.bukkit.command.CommandSender;

/*** 
 * @author tustin2121, Commander plugin (http://dev.bukkit.org/server-mods/commander)
 */
public abstract class EchoControl implements CommandSender {
	
	boolean echoEnabled = false;
	CommandSender wrappedSender;

	protected EchoControl(CommandSender sender) {
		this.wrappedSender = sender;
	}

	public CommandSender getWrappedSender() { return wrappedSender;}

	public boolean isEchoingEnabled() { return echoEnabled; }
	public void setEchoingEnabled(boolean e){
		echoEnabled = e;
	}

	@Override public void sendMessage(String message) {
		if (echoEnabled){
			wrappedSender.sendMessage(message);
		}
	}

	@Override public void sendMessage(String[] messages) {
		if (echoEnabled){
			wrappedSender.sendMessage(messages);
		}
	}
}
