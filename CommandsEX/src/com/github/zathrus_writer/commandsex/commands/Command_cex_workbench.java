package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;

public class Command_cex_workbench {
	
	/***
	 * WORKBENCH - opens a virtual crafting table
	 * @author Kezz101 and iKeirNez
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		Player target;
		
		if (args.length == 0){
			if (sender instanceof Player){
				target = (Player) sender;
			} else {
				Commands.showCommandHelpAndUsage(sender, "cex_workbench", alias);
				return true;
			}
		} else if (args.length == 1){
			if (sender.hasPermission("cex.workbench.others")){
				target = Bukkit.getPlayer(args[0]);
				
				if (target == null){
					LogHelper.showWarning("invalidPlayer", sender);
					return true;
				}
			} else {
				LogHelper.showWarning("othersNoPerm", sender);
				return true;
			}
		} else {
			Commands.showCommandHelpAndUsage(sender, "cex_workbench", alias);
			return true;
		}
		
		target.openWorkbench(null, true);
		
		if (!sender.equals(target)){
			LogHelper.showInfo("[" + sender.getName() + "#####workbenchOther", target);
		}
		
		return true;
	}

}
