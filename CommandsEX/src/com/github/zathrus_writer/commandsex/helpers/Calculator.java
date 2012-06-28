package com.github.zathrus_writer.commandsex.helpers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Calculator {

	/***
	 * Help - Simple method to send help messages to the sender, so I don't have to type it twice
	 * @author iKeirNez
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
	
	public static void calulation(CommandSender sender, String function, String number1, String number2){
		
	}
	
}
