package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.Vault;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_pinfo {

	public static Boolean run(CommandSender sender, String alias, String[] args){
		
		if (sender instanceof Player && Utils.checkCommandSpam((Player) sender, "cex_pinfo")){
			return true;
		}
		
		if (args.length > 2){
			Commands.showCommandHelpAndUsage(sender, "cex_pinfo", alias);
			return true;
		}
		
		Player target;
		String function = null;
		
		if (args.length == 0){
			if (!(sender instanceof Player)){
				Commands.showCommandHelpAndUsage(sender, "cex_pinfo", alias);
				return true;
			}
			target = (Player) sender;
		}
		
		if (args.length == 1){
			if (args[0].equalsIgnoreCase("dispname") || args[0].equalsIgnoreCase("displayname") || args[0].equalsIgnoreCase("balance") || args[0].equalsIgnoreCase("health") || args[0].equalsIgnoreCase("hearts")
					|| args[0].equalsIgnoreCase("armour") || args[0].equalsIgnoreCase("armourpoints") || args[0].equalsIgnoreCase("helmet") || args[0].equalsIgnoreCase("chestplate")
					|| args[0].equalsIgnoreCase("leggings") || args[0].equalsIgnoreCase("boots") || args[0].equalsIgnoreCase("food") || args[0].equalsIgnoreCase("hunger")
					|| args[0].equalsIgnoreCase("xp") || args[0].equalsIgnoreCase("exp") || args[0].equalsIgnoreCase("experience") || args[0].equalsIgnoreCase("xpleveles")
					|| args[0].equalsIgnoreCase("explevels") || args[0].equalsIgnoreCase("experiencelevels") || args[0].equalsIgnoreCase("coords") || args[0].equalsIgnoreCase("coordinates")
					|| args[0].equalsIgnoreCase("world") || args[0].equalsIgnoreCase("x") || args[0].equalsIgnoreCase("y") || args[0].equalsIgnoreCase("z")){
				function = args[0];
				target = (Player) sender;
			} else {
				if (sender.hasPermission("cex.pinfo.others")){
					target = Bukkit.getPlayer(args[0]);
					if (target == null){
						LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
						return true;
					}
				} else {
					LogHelper.showInfo("pinfoOthersNoPerm", sender, ChatColor.RED);
					return true;
				}
			}
		} else if (args.length == 2){
			if (sender.hasPermission("cex.pinfo.others")){
				target = Bukkit.getPlayer(args[1]);
				if (target == null){
					LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
					return true;
				}
			} else {
				LogHelper.showInfo("pinfoOthersNoPerm", sender, ChatColor.RED);
				return true;
			}
			
			if (args[1].equalsIgnoreCase("dispname") || args[1].equalsIgnoreCase("displayname") || args[1].equalsIgnoreCase("balance") || args[1].equalsIgnoreCase("health") || args[1].equalsIgnoreCase("hearts")
					|| args[1].equalsIgnoreCase("armour") || args[1].equalsIgnoreCase("armourpoints") || args[1].equalsIgnoreCase("helmet") || args[1].equalsIgnoreCase("chestplate")
					|| args[1].equalsIgnoreCase("leggings") || args[1].equalsIgnoreCase("boots") || args[1].equalsIgnoreCase("food") || args[1].equalsIgnoreCase("hunger")
					|| args[1].equalsIgnoreCase("xp") || args[1].equalsIgnoreCase("exp") || args[1].equalsIgnoreCase("experience") || args[1].equalsIgnoreCase("xpleveles")
					|| args[1].equalsIgnoreCase("explevels") || args[1].equalsIgnoreCase("experiencelevels") || args[1].equalsIgnoreCase("coords") || args[1].equalsIgnoreCase("coordinates")
					|| args[1].equalsIgnoreCase("world") || args[1].equalsIgnoreCase("x") || args[1].equalsIgnoreCase("y") || args[1].equalsIgnoreCase("z")){
				function = args[1];
			} else {
				Commands.showCommandHelpAndUsage(sender, "cex_pinfo", alias);
				return true;
			}
		} else {
			Commands.showCommandHelpAndUsage(sender, "cex_pinfo", alias);
			return true;
		}
		
		if (function == null || function.equalsIgnoreCase("dispname") || function.equalsIgnoreCase("displayname")){ LogHelper.showInfo("pinfoDispName#####[" + ChatColor.GOLD + target.getDisplayName(), sender, ChatColor.GRAY); }
		if (function == null || function.equalsIgnoreCase("balance")){
			if (CommandsEX.vaultPresent && Vault.ecoEnabled()){
				LogHelper.showInfo("pinfoBalance#####[" + ChatColor.GOLD + Vault.econ.getBalance(target.getName()), sender, ChatColor.GRAY);
			} else {
				if (function != null){
					LogHelper.showInfo("xpEco", sender, ChatColor.RED);
				}
			}
		}
		
		return true;
	}
	
}
