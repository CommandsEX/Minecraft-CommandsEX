package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_burn {

	/***
	 * Burn - Burns a player for a specified amount of seconds
	 * @author iKeirNez
	 * @param sender
	 * @param alias
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args){
		
		if (sender instanceof Player && Utils.checkCommandSpam((Player) sender, "cex_burn")){
			return true;
		}
		
		Player target = null;
		int time = 1200;
		
		if (args.length > 2){
			Commands.showCommandHelpAndUsage(sender, "cex_burn", alias);
			return true;
		}
		
		if (args.length == 0){
			if (!(sender instanceof Player)){
				Commands.showCommandHelpAndUsage(sender, "cex_burn", alias);
				return true;
			}
			
			target = (Player) sender;
		}
		
		if (args.length == 1){
			if (args[0].matches(CommandsEX.intRegex)){
				if (Bukkit.getPlayerExact(args[0]) != null){
					if (sender.hasPermission("cex.burn.others")){
						target = Bukkit.getPlayerExact(args[0]);
					} else {
						LogHelper.showInfo("burnOthersNoPerm", sender, ChatColor.RED);
						return true;
					}
				} else {
					time = Integer.parseInt(args[0]) * 20;
					
					if (!(sender instanceof Player)){
						Commands.showCommandHelpAndUsage(sender, "cex_burn", alias);
						return true;
					}
					target = (Player) sender;
				}
			} else {
				target = Bukkit.getPlayer(args[0]);
				if (target == null){
					LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
					return true;
				}
			}
		}
		
		if (args.length == 2){
			if (sender.hasPermission("cex.burn.others")){
				target = Bukkit.getPlayer(args[0]);
			} else {
				LogHelper.showInfo("burnOthersNoPerm", sender, ChatColor.RED);
				return true;
			}
			
			if (target == null){
				LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
				return true;
			}
			
			if (args[1].matches(CommandsEX.intRegex)){
				time = Integer.valueOf(args[1]) * 20;
			} else {
				LogHelper.showInfo("burnInteger", sender, ChatColor.RED);
				return true;
			}
		}
		
		target.setFireTicks(time);
		if (target != sender){ LogHelper.showInfo("burnSuccess#####[" + target.getName() + " #####for#####[ " + time / 20 + " #####seconds", sender, ChatColor.AQUA); }
		LogHelper.showInfo("burnNotify#####[" + target.getName() + " #####for#####[ " + time / 20 + " #####seconds", target, ChatColor.AQUA);
		return true;
	}
	
}
