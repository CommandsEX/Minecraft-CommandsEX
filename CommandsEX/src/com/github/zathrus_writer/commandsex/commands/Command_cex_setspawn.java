package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Spawning;

public class Command_cex_setspawn extends Spawning {
	/***
	 * SPAWN - teleports player to current world's spawn point
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player = (Player)sender;
			if (Permissions.checkPerms(player, "cex.setspawn")) {
				setSpawn(sender, args, "setspawn", alias);
			}
		}
        return true;
	}
}
