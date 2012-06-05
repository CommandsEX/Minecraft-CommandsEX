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

public class Command_cex_setmyrank {
	
	/***
	 * SETMYRANK - sets your rank
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender) && !Utils.checkCommandSpam((Player) sender, "setmyrank")) {
			if ((args.length == 1) && (Permissions.checkPerms((Player) sender, "cex.setmyrank.*") || Permissions.checkPerms((Player) sender, "cex.setmyrank." + args[0]))) {
				// Command Variables
				Player player = (Player)sender;
				String rank = args[0];
				
				// Check for Vault/Permissions
			    if(Vault.permsEnabled() != true) {
					LogHelper.logSevere(_("permissionsNotFound", ""));
					LogHelper.showWarning("permissionsNotFound", sender);
					return true;
			    }

				Vault.perms.playerAddGroup(player, rank);
				LogHelper.showInfo("setmyrankSucess#####[" + rank, sender);
				return true;
			} else if (args.length != 1) {
				// show usage
				Commands.showCommandHelpAndUsage(sender, "cex_setmyrank", alias);
			}
		}

		return true;
	}
}
