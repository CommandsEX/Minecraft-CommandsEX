package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.Vault;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

import static com.github.zathrus_writer.commandsex.Language._;

public class Command_cex_balance {
	
	/***
	 * BALANCE - displays a players balance
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender) && !Utils.checkCommandSpam((Player) sender, "balance") && Permissions.checkPerms((Player) sender, "cex.balance")) {
			if(Vault.ecoEnabled() != true) {
				 LogHelper.logSevere(_("economyNotFound", ""));
				 LogHelper.showInfo("economyNotFound", sender);
			}
	
			LogHelper.showInfo("economyBalance#####[" + Vault.econ.getBalance(sender.getName()) + "#####[ " + Vault.econ.currencyNamePlural(), sender);
		}
		
		return true;
	}
}
