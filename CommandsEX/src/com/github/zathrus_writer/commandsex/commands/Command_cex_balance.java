package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.command.CommandSender;

import com.github.zathrus_writer.commandsex.Vault;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import static com.github.zathrus_writer.commandsex.Language._;

public class Command_cex_balance {
	
	/***
	 * BALANCE - displays a players balance
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		
		if(Vault.ecoEnabled() != true) {
			 LogHelper.logSevere(_("economyNotFound", ""));
			 LogHelper.showInfo("economyNotFound", sender);
		}

		LogHelper.showInfo("economyBalance#####[" + Vault.econ.getBalance(sender.getName()) + "#####[ " + Vault.econ.currencyNamePlural(), sender);
		return true;
	}
}
