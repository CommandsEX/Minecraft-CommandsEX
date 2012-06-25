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
	
	/***
	 * Add - Adds 2 numbers together and returns the answer in a STRING
	 * I did it this way so that the answer can be null if both numbers are not integers
	 * @author iKeirNez
	 * @param sender
	 * @param number1
	 * @param number2
	 * @return
	 */
	
	public static String add(CommandSender sender, String number1, String number2) {
		int int1 = 0;
		int int2 = 0;
		
		boolean error = false;
		
		try {
			int1 = Integer.valueOf(number1);
		} catch (Exception e) {
			LogHelper.showInfo("calculatorErrorNumber", sender, ChatColor.AQUA);
			error = true;
		}
		
		try {
			int2 = Integer.valueOf(number2);
		} catch (Exception e) {
			LogHelper.showInfo("calculatorErrorNumber", sender, ChatColor.AQUA);
			error = true;
		}
		
		if (!error){
			int answer = int1 + int2;
			String toReturn = answer + "";
			return toReturn;
		} else {
			return null;
		}
	}
	
	/***
	 * Subtract - Subtracts one number from another and returns the answer in a STRING
	 * I did it this way so that the answer can be null if both numbers are not integers
	 * @author iKeirNez
	 * @param sender
	 * @param number1
	 * @param number2
	 * @return
	 */
	
	public static String subtract(CommandSender sender, String number1, String number2) {
		int int1 = 0;
		int int2 = 0;
		
		boolean error = false;
		
		try {
			int1 = Integer.valueOf(number1);
		} catch (Exception e) {
			LogHelper.showInfo("calculatorErrorNumber", sender, ChatColor.AQUA);
			error = true;
		}
		
		try {
			int2 = Integer.valueOf(number2);
		} catch (Exception e) {
			LogHelper.showInfo("calculatorErrorNumber", sender, ChatColor.AQUA);
			error = true;
		}
		
		if (!error){
			// Yes this is meant to be the other way round
			int answer = int2 - int1;
			String toReturn = answer + "";
			return toReturn;
		} else {
			return null;
		}
	}
	
	/***
	 * Multiply - Multiplies one number with another and returns the answer in a STRING
	 * I did it this way so that the answer can be null if both numbers are not integers
	 * @author iKeirNez
	 * @param sender
	 * @param number1
	 * @param number2
	 * @return
	 */
	
	public static String multiply(CommandSender sender, String number1, String number2) {
		int int1 = 0;
		int int2 = 0;
		
		boolean error = false;
		
		try {
			int1 = Integer.valueOf(number1);
		} catch (Exception e) {
			LogHelper.showInfo("calculatorErrorNumber", sender, ChatColor.AQUA);
			error = true;
		}
		
		try {
			int2 = Integer.valueOf(number2);
		} catch (Exception e) {
			LogHelper.showInfo("calculatorErrorNumber", sender, ChatColor.AQUA);
			error = true;
		}
		
		if (!error){
			int answer = int1 * int2;
			String toReturn = answer + "";
			return toReturn;
		} else {
			return null;
		}
	}
	
	/***
	 * Divide - Divides one number from another and returns the answer in a STRING
	 * I did it this way so that the answer can be null if both numbers are not integers
	 * @author iKeirNez
	 * @param sender
	 * @param number1
	 * @param number2
	 * @return
	 */
	
	public static String divide(CommandSender sender, String number1, String number2) {
		int int1 = 0;
		int int2 = 0;
		
		boolean error = false;
		
		try {
			int1 = Integer.valueOf(number1);
		} catch (Exception e) {
			LogHelper.showInfo("calculatorErrorNumber", sender, ChatColor.AQUA);
			error = true;
		}
		
		try {
			int2 = Integer.valueOf(number2);
		} catch (Exception e) {
			LogHelper.showInfo("calculatorErrorNumber", sender, ChatColor.AQUA);
			error = true;
		}
		
		if (!error){
			int answer = int1 / int2;
			String toReturn = answer + "";
			return toReturn;
		} else {
			return null;
		}
	}
	
}
