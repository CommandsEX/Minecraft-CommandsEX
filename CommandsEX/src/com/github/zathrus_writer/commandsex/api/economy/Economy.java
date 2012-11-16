package com.github.zathrus_writer.commandsex.api.economy;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Econ;

public class Economy {

	/**
	 * Checks if the Economy feature in CommandsEX is enabled
	 * @return The Economy enabled status
	 */
	
	public static boolean isEnabled(){
		return CommandsEX.loadedClasses.contains("Init_Econ");
	}
	
	/**
	 * Gets the currency symbol, default $
	 * @return
	 */
	
	public static String getCurrencySymbol(){
		return CommandsEX.getConf().getString("economy.currencySymbol");
	}
	
	/**
	 * Gets the Currency name in Plural form
	 * @return The currency name, in Plural form
	 */
	
	public static String getCurrencyPlural(){
		return CommandsEX.getConf().getString("economy.currencyPlural");
	}
	
	/**
	 * Gets the Currency name in Singular form
	 * @return The Currency name, in Singular form
	 */
	
	public static String getCurrencySingular(){
		return CommandsEX.getConf().getString("economy.currencySingular");
	}
	
	/**
	 * Checks if a player has an account
	 * @param player The player to check if they have an account
	 * @return Does the player have an account
	 */
	
	public static boolean hasAccount(String player){
		return Econ.hasAccount(player);
	}
	
	/**
	 * Gets a players balance, if the player does not have an account, the default balance will be returned
	 * @param player The player to check
	 * @return The players balance
	 */
	
	public static double getBalance(String player){
		return Econ.getBalance(player);
	}
	
	/**
	 * Gets a players balance with the currency symbol
	 * @param player The player to check the balance of
	 * @return The balance with the currency symbol
	 */
	
	public static String getBalanceWithSymbol(String player){
		return getCurrencySymbol() + getBalance(player);
	}
	
	/**
	 * Gets an amount of money with the appropriate suffix
	 * @param amount The amount
	 * @return Returns the amount with the appropriate suffix
	 */
	
	public static String getAmountWithSuffix(double amount){
		return amount + " " + (amount == 1 ? getCurrencySingular() : getCurrencyPlural());
	}
	
	/**
	 * Gets a players balance with the suffix
	 * @param player The player to check the balance of
	 * @return The balance with the suffix
	 */
	
	public static String getBalanceWithSuffix(String player){
		return getAmountWithSuffix(getBalance(player));
	}
	
	/**
	 * Checks if a player has an amount
	 * @param player The player to check
	 * @param amount The amount to see if the player has
	 * @return Does the player have that amount or more?
	 */
	
	public static boolean has(String player, double amount){
		return Econ.has(player, amount);
	}
	
	/**
	 * Creates an account for a player with the default balance
	 * @param player The player to create the account for
	 */
	
	public static void createAccount(String player){
		Econ.createAccount(player);
	}
	
	/**
	 * Creates an account for a player with a custom balance
	 * @param player The player to create the account for
	 * @param amount The amount the player should have
	 */
	
	public static void createAccount(String player, double amount){
		Econ.createAccount(player, amount);
	}
	
	/**
	 * Sets a players balance
	 * @param player The player to set the balance for
	 * @param amount The amount the player should have
	 */
	
	public static void setBalance(String player, double amount){
		Econ.setBalance(player, amount);
	}
	
	/**
	 * Withdraws money from a players bank account, even if they do not have that amount
	 * This could result in a player having a negative balance
	 * @param player The player to withdraw money from
	 * @param amount The amount of money to withdraw
	 */
	
	public static void withdraw(String player, double amount){
		Econ.withdraw(player, amount);
	}
	
	/**
	 * Adds an amount to the players balance
	 * @param player The player to add money to
	 * @param amount The amount to add
	 */
	
	public static void deposit(String player, double amount){
		Econ.deposit(player, amount);
	}
	
	/**
	 * Fixes the number of decimal places in a number, 5.0 will become 5.00
	 * @param input The number to fix
	 * @return The fixed number
	 */
	
	public static String fixDecimals(double input){
		return Econ.fixDecimals(input);
	}
	
}
