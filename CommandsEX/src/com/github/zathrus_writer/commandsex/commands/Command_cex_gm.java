package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.scripting.CommanderCommandSender;

public class Command_cex_gm {
	/***
	 * GM - changes a game mode of a player (wrapper to Bukkit's /gamemode command)
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		String pName;
		Player p;
		Boolean selfGM = false;
		// check if we have any parameters
		if (args.length > 0) {
			// we have a player name to change game mode for 
			p = Bukkit.getPlayer(args[0]);
			
			if (p == null) {
				LogHelper.showWarning("invalidPlayer", sender);
				return true;
			} else {
				pName = p.getName();
			}
		} else {
			// check if we have permissions
			if ((sender instanceof Player) && !Permissions.checkPerms((Player)sender, "cex.gm.self")) {
				return true;
			}
			selfGM = true;
			pName = sender.getName();
		}
		
		// used to get game mode of the player
		p = Bukkit.getPlayer(pName);

		if (p == null) {
			LogHelper.showWarning("invalidPlayer", sender);
			return true;
		}
		
		if (sender instanceof Player) {
			if (p.getGameMode() == GameMode.CREATIVE) {
				if (selfGM) {
					((Player)sender).setGameMode(GameMode.SURVIVAL);
				} else {
					((Player) sender).performCommand("gamemode " + pName + " 0");
				}
			} else {
				if (selfGM) {
					((Player)sender).setGameMode(GameMode.CREATIVE);
				} else {
					((Player) sender).performCommand("gamemode " + pName + " 1");
				}
			}
		} else {
			final CommanderCommandSender ccs = new CommanderCommandSender();
			if (p.getGameMode() == GameMode.CREATIVE) {
				CommandsEX.plugin.getServer().dispatchCommand(ccs, "gamemode " + pName + " 0");
			} else {
				CommandsEX.plugin.getServer().dispatchCommand(ccs, "gamemode " + pName + " 1");
			}
		}
		
        return true;
	}
}
