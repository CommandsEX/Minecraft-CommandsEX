package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_invsee {

	public static Boolean run(CommandSender sender, String alias, String[] args){
		
		if (!(sender instanceof Player)){
			LogHelper.showInfo("inWorldCommandOnly", sender, ChatColor.RED);
			return true;
		}
		
		if (args.length != 1){
			Commands.showCommandHelpAndUsage(sender, "cex_invsee", alias);
			return true;
		}
		
		Player target = Bukkit.getPlayerExact(args[0]);
		Player player = (Player) sender;
		
		if (target == null){
			target = Utils.getOfflinePlayer(args[0]);
			if (target == null){
				target = Bukkit.getPlayer(args[0]);
				if (target == null){
					LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
					return true;
				}
			}
		}
		
		Inventory inv = target.getInventory();
		player.openInventory(inv);
		LogHelper.showInfo("invSeeNowEditing#####[" + target.getName(), sender, ChatColor.AQUA);
		
		return true;
	}
}
