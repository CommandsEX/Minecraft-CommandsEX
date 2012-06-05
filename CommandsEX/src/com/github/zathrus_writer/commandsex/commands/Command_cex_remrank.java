package com.github.zathrus_writer.commandsex.commands;

import static com.github.zathrus_writer.commandsex.Language._;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.Vault;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_remrank {
	
	/***
	 * SETRANK - removes a player from the given group
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (!Utils.checkCommandSpam((Player) sender, "remrank") && (!(sender instanceof Player) || ((sender instanceof Player) && Permissions.checkPerms((Player) sender, "cex.remrank")))) {
			// Check for Vault/Permissions
			if(Vault.permsEnabled() != true) {
				 LogHelper.logSevere(_("permissionsNotFound", ""));
				 LogHelper.showWarning("permissionsNotFound", sender);
			}
			
			// Check they have specified a player
			if(args.length != 2) {
				Commands.showCommandHelpAndUsage(sender, "cex_remrank", alias);
				return true;
			}
			
			// Command variables
			Player player = Bukkit.getServer().getPlayerExact(args[0]);
			if (player == null) {
				LogHelper.showWarning("invalidPlayer", sender);
				return true;
			}
	
			// Set group
			String group = args[1];
			Vault.perms.playerRemoveGroup(player, group);
			
			// Notify sender and player
			LogHelper.showInfo("[" + player.getName() + " #####remrankToSender#####[" + group, sender);
			LogHelper.showInfo("remrankToPlayer#####[" + group, player);
		}
		
		return true;
	}
}
