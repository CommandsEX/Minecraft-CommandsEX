package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.Common;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;

public class Command_cex_fly extends Common {

	public static Boolean run(CommandSender sender, String alias, String[] args){
		if (args.length == 0){
			if (!(sender instanceof Player)){
				Commands.showCommandHelpAndUsage(sender, "cex_fly", alias);
				return true;
			}
			
			Player player = (Player) sender;
			setFlyMode(player);
			LogHelper.showInfo("flyOwnSuccess", sender, ChatColor.AQUA);
		} else if (args.length == 1){
			boolean hasPerms;
			// Determine whether the player has permission to set others fly modes.
			if (!(sender instanceof Player)){
				hasPerms = true;
			} else if (((Player) sender).hasPermission("cex.fly.others")){
				hasPerms = true;
			} else {
				hasPerms = false;
			}
			
			Player target = Bukkit.getPlayer(args[0]);
			
			if (target != sender){
				if (hasPerms){
					if (target == null){
						LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
						return true;
					}
					
					setFlyMode(target);
					LogHelper.showInfo("flyMsgToTarget#####[" + sender.getName(), target, ChatColor.AQUA);
					LogHelper.showInfo("flyOtherSuccess#####[" + target.getName(), sender, ChatColor.AQUA);
				} else {
					LogHelper.showInfo("flyOtherNoPerm", sender, ChatColor.RED);
				}
			} else {
				setFlyMode(target);
			}
		} else {
			Commands.showCommandHelpAndUsage(sender, "cex_fly", alias);
		}
		return true;
	}
	
}
