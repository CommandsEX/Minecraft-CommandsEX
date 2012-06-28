package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Calculator;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_calculator extends Calculator {

	/***
	 * Calculator - A simple calculator to add, subtract, multiply and divide
	 * @author iKeirNez
	 * @param sender
	 * @param alias
	 * @param args
	 * @return
	 */
	
	public static boolean run(CommandSender sender, String alias, String[] args){
		
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (Utils.checkCommandSpam(player, "cex_calculator")){
				return true;
			}
		}
		
		if (args.length == 0){
			help(sender);
			return true;
		}
		
		String function = args[0];
		
		if (args.length != 3 && !function.equalsIgnoreCase("help")){
			Commands.showCommandHelpAndUsage(sender, "cex_calculator", alias);
			return true;
		}
		
		int int1;
		int int2;
		
		try {
			int1 = Integer.parseInt(args[1]);
			int2 = Integer.parseInt(args[2]);
		} catch (Exception e) {
			LogHelper.showInfo("calculatorErrorNumber", sender, ChatColor.RED);
			return true;
		}
		
		if (function.equalsIgnoreCase("help")){
			help(sender);
			return true;
		}
		
		if (!function.equalsIgnoreCase("add") && !function.equalsIgnoreCase("subtract") && !function.equalsIgnoreCase("multiply") && !function.equalsIgnoreCase("divide")){
			Commands.showCommandHelpAndUsage(sender, "cex_calculator", alias);
			return true;
		}
		
		LogHelper.showInfo("calculatorAnswer#####[" + calulation(sender, function, int1, int2), sender, ChatColor.AQUA);
		
		return true;
	}
}
