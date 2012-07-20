package com.github.zathrus_writer.commandsex.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Handler_serverstop extends Thread {

	public Handler_serverstop(){
		// custom shutdown kick messages
		Runtime.getRuntime().addShutdownHook(new shutdownHook());
	}
}

class shutdownHook extends Thread {
	
	public void run() {
		String kickMsg = Utils.replaceChatColors(CommandsEX.getConf().getString("shutdownKickMessage"));
		for (Player player : Bukkit.getOnlinePlayers()){
			player.kickPlayer(kickMsg);
		}
	}
	
}