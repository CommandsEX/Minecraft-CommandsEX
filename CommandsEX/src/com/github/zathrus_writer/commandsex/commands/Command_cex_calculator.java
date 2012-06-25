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
		
		String no1 = null;
		String no2 = null;
		
		if (args.length == 3){
			no1 = args[1];
			no2 = args[2];
		}
		
		if (function.equalsIgnoreCase("add")){
			if (args.length != 3){
				LogHelper.showInfo("calculatorHelpAddition", sender, ChatColor.AQUA);
			} else {
				String answer = add(sender, no1, no2);
				if (answer != null) {
					LogHelper.showInfo("calculatorAnswer#####[" + no1 + " + " + no2 + " #####calculatorWordsIs#####[ " + ChatColor.BOLD + answer, sender, ChatColor.AQUA);
				} else {
					LogHelper.showInfo("calculatorErrorNumber", sender, ChatColor.RED);
				}
			}
		} else if (function.equalsIgnoreCase("subtract")){
			if (args.length != 3){
				LogHelper.showInfo("calculatorHelpSubtraction", sender, ChatColor.AQUA);
			} else {
				String answer = subtract(sender, no1, no2);
				if (answer != null) {
					LogHelper.showInfo("calculatorAnswer#####[" + no1 + " - " + no2 + " #####calculatorWordsIs#####[ " + ChatColor.BOLD + answer, sender, ChatColor.AQUA);
				} else {
					LogHelper.showInfo("calculatorErrorNumber", sender, ChatColor.RED);
				}
			}
		} else if (function.equalsIgnoreCase("multiply")){
			if (args.length != 3){
				LogHelper.showInfo("calculatorHelpDivision", sender, ChatColor.AQUA);
			} else {
				String answer = multiply(sender, no1, no2);
				if (answer != null) {
					LogHelper.showInfo("calculatorAnswer#####[" + no1 + " \u00D7 " + no2 + " #####calculatorWordsIs#####[ " + ChatColor.BOLD + answer, sender, ChatColor.AQUA);
				} else {
					LogHelper.showInfo("calculatorErrorNumber", sender, ChatColor.RED);
				}
			}
		} else if (function.equalsIgnoreCase("divide")){
			if (args.length != 3){
				LogHelper.showInfo("calculatorHelpDivision", sender, ChatColor.AQUA);
			} else {
				String answer = divide(sender, no1, no2);
				if (answer != null) {
					LogHelper.showInfo("calculatorAnswer#####[" + no1 + " \u00F7 " + no2 + " #####calculatorWordsIs#####[ " + ChatColor.BOLD + answer, sender, ChatColor.AQUA);
				} else {
					LogHelper.showInfo("calculatorErrorNumber", sender, ChatColor.RED);
				}
			}
		} else if (function.equalsIgnoreCase("help")){
			if (args.length != 2){
				Commands.showCommandHelpAndUsage(sender, "cex_calculator", alias);
			} else {
				help(sender);
			}
		} else {
			Commands.showCommandHelpAndUsage(sender, "cex_calculator", alias);
		}
		
		return true;
	}
}
