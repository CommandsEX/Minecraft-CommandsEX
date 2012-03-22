package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Spawning;

public class Command_cex_setspawn extends Spawning {
	/***
	 * SETSPAWN - sets spawning point of the current world to where the Player is standing
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
