package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.ExperienceManager;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;

public class Command_cex_xp {

	public static Boolean run(CommandSender sender, String alias, String[] args){
		
		boolean validCommand;
		
		if (args.length == 1){
			if (args[0].equalsIgnoreCase("view")){
				validCommand = true;
			} else {
				validCommand = false;
			}
		} else if (args.length == 2 || args.length == 3){
			validCommand = true;
		} else {
			validCommand = false;
		}
		
		if (!validCommand){
			Commands.showCommandHelpAndUsage(sender, "cex_xp", alias);
			return true;
		}

		Player target;
		String function;
		String amount;
		int amountint = 0;

		if ((args.length == 3 && !args[1].equalsIgnoreCase("view")) || (args.length == 2 && args[1].equalsIgnoreCase("view"))){
			target = Bukkit.getPlayer(args[0]);
			function = args[1];
		} else {
			if (sender instanceof Player){
				target = ((Player) sender);
			} else {
				Commands.showCommandHelpAndUsage(sender, "cex_xp", alias);
				return true;
			}
			function = args[0];
		}

		if (target == null){
			LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
			return true;
		}

		if (!function.equalsIgnoreCase("view") && !function.equalsIgnoreCase("set") && !function.equalsIgnoreCase("add") && !function.equalsIgnoreCase("take")){
			Commands.showCommandHelpAndUsage(sender, "cex_xp", alias);
			return true;
		}

		if (!function.equalsIgnoreCase("view")){
			if (args.length == 2){
				amount = args[1];
			} else {
				amount = args[2];
			}

			if (amount.matches(CommandsEX.intRegex)){
				amountint = Integer.valueOf(amount);
			} else {
				LogHelper.showInfo("xpNotNumeric", sender, ChatColor.RED);
				return true;
			}
		}

		ExperienceManager expman = new ExperienceManager(target);

		if (function.equalsIgnoreCase("view")){
			LogHelper.showInfo((sender != target ? "#####[" + target.getName() + " #####xpHas#####[" : "xpViewSelf#####[") + expman.getCurrentExp() + " #####xpExperience", sender, ChatColor.AQUA);
			return true;
		}

		if (function.equalsIgnoreCase("set")){
			expman.setExp(amountint);
			if (sender != target) { LogHelper.showInfo("xpSet#####[" + target.getName() +  " #####xpTo#####[" + amountint, sender, ChatColor.AQUA); }
			LogHelper.showInfo((sender != target ? "#####[" + sender.getName() + " #####xpSetMsgToTarget1#####[" : "xpSetMsgToTarget2#####[") + amountint, target, ChatColor.AQUA);
		}

		if (function.equalsIgnoreCase("add")){
			expman.changeExp(amountint);
			if (sender != target) { LogHelper.showInfo("xpAdded#####[" + amountint + " #####xpExperience#####[ #####xpTo#####[" + target.getName(), sender, ChatColor.GREEN); }
			LogHelper.showInfo((sender != target ? "#####[" + sender.getName() + " #####xpAddedGave1#####[" : "xpAddedGave2#####[") + amountint + " #####xpExperience", target, ChatColor.AQUA);
		}

		if (function.equalsIgnoreCase("take")){
			boolean hasXP = expman.hasExp(amountint);
			int oldXP = expman.getCurrentExp();
			expman.changeExp(-(hasXP ? amountint : expman.getCurrentExp()));
			if (!hasXP) { LogHelper.showInfo("xpNotAllRemoved", sender, ChatColor.RED); }
			if (sender != target) { LogHelper.showInfo("xpTaken#####[" + (hasXP ? amountint : oldXP) + " #####xpExperience#####[ #####xpFrom#####[" + target.getName(), sender, ChatColor.GREEN); }
			LogHelper.showInfo((sender != target ? "#####[" + sender.getName() + " #####xpTakenTook1#####[" : "xpTakenTook2#####[") + (hasXP ? amountint : oldXP) + " #####xpExperience", target, ChatColor.AQUA);
		}
		return true;
	}

}
