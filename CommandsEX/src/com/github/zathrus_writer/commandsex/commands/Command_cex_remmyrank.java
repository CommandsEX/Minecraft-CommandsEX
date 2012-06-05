package com.github.zathrus_writer.commandsex.commands;

import static com.github.zathrus_writer.commandsex.Language._;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.Vault;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_remmyrank {
	
	/***
	 * REMMYRANK - removes the command sender from the given group
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender) && !Utils.checkCommandSpam((Player) sender, "remmyrank")) {
			if ((args.length == 1) && (Permissions.checkPerms((Player) sender, "cex.remmyrank.*") || Permissions.checkPerms((Player) sender, "cex.remmyrank." + args[0]))) {
				// Command Variables
				Player player = (Player)sender;
				String rank = args[0];
				
				// Check for Vault/Permissions
			    if(Vault.permsEnabled() != true) {
					LogHelper.logSevere(_("permissionsNotFound", ""));
					LogHelper.showWarning("permissionsNotFound", sender);
					return true;
			    }
			    
				Vault.perms.playerRemoveGroup(player, rank);
				LogHelper.showInfo("remmyrankSucess#####[" + rank, sender);
				return true;
			} else if (args.length != 1) {
				// show usage
				Commands.showCommandHelpAndUsage(sender, "cex_remmyrank", alias);
			}
		}

		return true;
	}
}
