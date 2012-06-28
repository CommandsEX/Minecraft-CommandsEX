package com.github.zathrus_writer.commandsex.helpers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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
		LogHelper.showInfo("calculatorHelpDivision", sender, ChatColor.YELLOW);
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
			sum = int1 + " + " + int2 + " = " + (int1 + int2);
		} else if (function.equalsIgnoreCase("subtract")){
			sum = int1 + " - " + int2 + " = " + (int1 - int2);
		} else if (function.equalsIgnoreCase("multiply")){
			sum = int1 + " * " + int2 + " = " + (int1 * int2);
		} else if (function.equalsIgnoreCase("divide")){
			sum = int1 + " / " + int2 + " = " + (int1 / int2);
		}
		
		return sum;
	}
}
