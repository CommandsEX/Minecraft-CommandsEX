package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_ptime {
	/***
	 * Player Time - changes the players time
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (sender instanceof Player && Utils.checkCommandSpam((Player) sender, "cex_ptime")){
			return true;
		}
		
		String function = "";
		Player target = null;
		long move = 0L;
		
		if (args.length == 0){
			Commands.showCommandHelpAndUsage(sender, "cex_ptime", alias);
			return true;
		}

		if (args.length > 0){
			function = args[0];

			if (function.equalsIgnoreCase("reset")){
				if (args.length == 2){
					target = Bukkit.getPlayer(args[1]);
				} else {
					if (!(sender instanceof Player) && args.length != 0){
						Commands.showCommandHelpAndUsage(sender, "cex_ptime", alias);
						return true;
					} else {
						target = (Player) sender;
					}
				}
				
				if (target == null){
					LogHelper.showWarning("invalidPlayer", sender);
					return true;
				}
				
				if (sender != target && !sender.hasPermission("cex.ptime.others")){
					LogHelper.showWarning("playerTimeOthersNoPerm", sender);
					return true;
				}
				
				target.resetPlayerTime();
				LogHelper.showInfo("playerTimeReset", sender);
				
				return true;
			} 
			
			if (args.length == 3){
				target = Bukkit.getPlayer(args[2]);
			} else if (args.length == 2){
				if (sender instanceof Player){
					target = (Player) sender;
				} else {
					Commands.showCommandHelpAndUsage(sender, "cex_ptime", alias);
					return true;
				}
			} else {
				Commands.showCommandHelpAndUsage(sender, "cex_ptime", alias);
				return true;
			}
		}

		if (target == null){
			LogHelper.showWarning("invalidPlayer", sender);
			return true;
		}
		
		if (target != sender && !sender.hasPermission("cex.ptime.others")){
			LogHelper.showWarning("playerTimeOthersNoPerm", sender);
			return true;
		}

		if (function.equalsIgnoreCase("add")){
			if (args.length == 2 || args.length == 3){
				if (!args[1].matches(CommandsEX.intRegex)){
					LogHelper.showWarning("timeAddOnlyWorksWithNumbers", sender);
					return true;
				}
				
				move = Long.parseLong(args[1]);
			}
			
			target.setPlayerTime(target.getPlayerTime() + move, false);
		} else if (function.equalsIgnoreCase("set")){
			if (args[1].equalsIgnoreCase("dawn") || args[1].equalsIgnoreCase("day")){
				move = 0L;
			} else if (args[1].equalsIgnoreCase("morning")){
				move = 2500L;
			} else if (args[1].equalsIgnoreCase("noon")){
				move = 6000L;
			} else if (args[1].equalsIgnoreCase("evening")){
				move = 11000L;
			} else if (args[1].equalsIgnoreCase("night")){
				move = 15000L;
			} else if (args[1].matches(CommandsEX.intRegex)){
				move = Long.parseLong(args[1]);
			} else {
				LogHelper.showWarning("timeInvalidStringValue", sender);
				return true;
			}

			target.setPlayerTime(move, false);
		} else {
			Commands.showCommandHelpAndUsage(sender, "cex_ptime", alias);
			return true;
		}

		// tell the player and anyone with permission to see what happened
		LogHelper.showInfo("playerTimeChanged#####[" + target.getPlayerTime() + " #####timeTicks" + (sender != target ? "#####for#####[ " + Nicknames.getNick(target.getName()) : ""), sender);
		
        return true;
	}
}
