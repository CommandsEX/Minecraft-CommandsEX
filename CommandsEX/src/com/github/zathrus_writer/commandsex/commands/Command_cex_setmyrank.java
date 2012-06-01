package com.github.zathrus_writer.commandsex.commands;

import static com.github.zathrus_writer.commandsex.Language._;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.Vault;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;

public class Command_cex_setmyrank {
	
	/***
	 * SETMYRANK - sets your rank
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			if(args.length == 1) {
				// Command Variables
				Player player = (Player)sender;
				String rank = args[0];
				
				// Check for Vault/Permissions
			    if(Vault.permsEnabled() != true) {
					LogHelper.logSevere(_("permissionsNotFound", ""));
					LogHelper.showWarning("permissionsNotFound", sender);
					return true;
			    }
			    
			    // First check for star permission, then for group permission
			    if(Vault.perms.has(sender, "cex.setmyrank.*") || Vault.perms.has(sender, "cex.setmyrank." + rank)) {
					Vault.perms.playerAddGroup(player, rank);
					LogHelper.showInfo("setmyrankSucess#####[" + rank, sender);
					return true;
			    }
			} else {
				// show usage
				Commands.showCommandHelpAndUsage(sender, "cex_setmyrank", alias);
			}
		}

		return true;
	}
}
