package com.github.zathrus_writer.commandsex.helpers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Calculator {

	/***
	 * Calculation Help - Displays help and usage about the Calculator
	 * @param sender
	 */
	
	public static void help(CommandSender sender){
		LogHelper.showInfo("calculatorHelp", sender, ChatColor.YELLOW);
		LogHelper.showInfo("calculatorHelpIntro", sender, ChatColor.YELLOW);
		LogHelper.showInfo("calculatorHelpAddition", sender, ChatColor.YELLOW);
		LogHelper.showInfo("calculatorHelpSubtraction", sender, ChatColor.YELLOW);
		LogHelper.showInfo("calculatorHelpMultiplication", sender, ChatColor.YELLOW);
		LogHelper.showInfo("calcuklatorHelpDivision", sender, ChatColor.YELLOW);
	}
	
	/***
	 * Calculation - Method to work out a calculation and return it
	 * @param sender
	 * @param function
	 * @param int1
	 * @param int2
	 * @return
	 */
	
	public static String calulation(CommandSender sender, String function, int int1, int int2){
		String sum = null;
		
		if (function.equalsIgnoreCase("add")){
			if ((!(sender instanceof Player)) || ((Player) sender).hasPermission("cex.calculator.add")){
				// If the function is add then add them together and display it as int1 + int2 = (int1 + int2)
				sum = int1 + " + " + int2 + " = " + (int1 + int2);
			} else {
				LogHelper.showInfo("calculatorPermission#####calculatorWordsAdd", sender, ChatColor.RED);
			}
		} else if (function.equalsIgnoreCase("subtract")){
			if ((!(sender instanceof Player)) || ((Player) sender).hasPermission("cex.calculator.subtract")){
				// If the function is subtract then subtract them and display it as int1 - int2 = (int1 - int2)
				sum = int1 + " - " + int2 + " = " + (int1 - int2);
			} else {
				LogHelper.showInfo("calculatorPermission#####calculatorWordsSubtract", sender, ChatColor.RED);
			}
		} else if (function.equalsIgnoreCase("multiply")){
			if ((!(sender instanceof Player)) || ((Player) sender).hasPermission("cex.calculator.multiply")){
				// If the function is multiply then multiply them together and display it as int1 + int2 = (int1 + int2)
				sum = int1 + " * " + int2 + " = " + (int1 * int2);
			} else {
				LogHelper.showInfo("calculatorPermission#####calculatorWordsMultiply", sender, ChatColor.RED);
			}
		} else if (function.equalsIgnoreCase("divide")){
			if ((!(sender instanceof Player)) || ((Player) sender).hasPermission("cex.calculator.divide")){
				// If the function is divide then divide them and display it as int1 / int2 = (int1 / int2)
				sum = int1 + " / " + int2 + " = " + (int1 / int2);
			} else {
				LogHelper.showInfo("calculatorPermission#####calculatorWordsDivide", sender, ChatColor.RED);
			}
		}
		
		// Return the sum, will be in the format of (int1 (function-sign) int2 = (answer))
		return sum;
	}
}
