package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.Spawning;

public class Command_cex_spawn extends Spawning {
	/***
	 * SPAWN - teleports player to current world's spawn point
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player)){
			if (args.length == 0){
				Commands.showCommandHelpAndUsage(sender, "cex_spawn", alias);
				return true;
			}
		}
		
		doSpawn(sender, args, "spawn", alias);
		return true;
	}
}
