package com.github.zathrus_writer.commandsex.api.economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.Vault;
import com.github.zathrus_writer.commandsex.helpers.Econ;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

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
		return Econ.balances.containsKey(player);
	}
	
	/**
	 * Gets a players balance, if the player does not have an account, the default balance will be returned
	 * @param player The player to check
	 * @return The players balance
	 */
	
	public static double getBalance(String player){
		if (hasAccount(player)){
			return Econ.balances.get(player);
		} else {
			return Econ.defaultBalance;
		}
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
		return getBalance(player) >= amount;
	}
	
	/**
	 * Creates an account for a player with the default balance
	 * @param player The player to create the account for
	 */
	
	public static void createAccount(String player){
		createAccount(player, Econ.defaultBalance);
	}
	
	/**
	 * Creates an account for a player with a custom balance
	 * @param player The player to create the account for
	 * @param amount The amount the player should have
	 */
	
	public static void createAccount(String player, double initialAmount){
		if (!hasAccount(player)){
			Econ.balances.put(player, initialAmount);
		}
	}

	/**
	 * Sets a players balance
	 * @param player The player to set the balance for
	 * @param amount The amount the player should have
	 */
	
	public static void setBalance(String player, double amount){
		PlayerEconomyBalanceChangeEvent event = new PlayerEconomyBalanceChangeEvent(player, amount);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		if (event.isCancelled()){
			return;
		}
		
		amount = event.getAmount();
		
		if (hasAccount(player)){
			Econ.balances.remove(player);
			Econ.balances.put(player, amount);
		} else {
			createAccount(player, amount);
		}
	}
	
	/**
	 * Withdraws money from a players bank account, even if they do not have that amount
	 * This could result in a player having a negative balance
	 * @param player The player to withdraw money from
	 * @param amount The amount of money to withdraw
	 */
	
	public static void withdraw(String player, double amount){
		if (hasAccount(player)){
			setBalance(player, getBalance(player) - amount);
		} else {
			createAccount(player, Econ.defaultBalance - amount);
		}
	}
	
	/**
	 * Adds an amount to the players balance
	 * @param player The player to add money to
	 * @param amount The amount to add
	 */
	
	public static void deposit(String player, double amount){
		if (hasAccount(player)){
			setBalance(player, getBalance(player) + amount);
		} else {
			createAccount(player, Econ.defaultBalance + amount);
		}
	}
	
	/**
	 * Transfers an amount from one account to another
	 * @param sender The player sending the money
	 * @param reciever The player receiving the money
	 * @param amount The money to send from the sender to the receiver
	 */
	
	public static void transfer(String sender, String reciever, double amount){
		if (!CommandsEX.vaultPresent || !Vault.ecoEnabled()){
			return;
		}
		
		Vault.econ.withdrawPlayer(sender, amount);
		Vault.econ.depositPlayer(reciever, amount);
		
		Player s = Bukkit.getPlayerExact(sender);
		Player r = Bukkit.getPlayerExact(reciever);
		
		String a = Vault.econ.format(amount).replaceFirst(String.valueOf(amount), fixDecimals(amount));
		
		if (s != null){
			LogHelper.showInfo("economyPay#####[" + a + " #####to#####[" + reciever, s);
		}
		
		if (r != null){
			LogHelper.showInfo("[" + sender + " #####economyPayNotify1#####[" + a + " #####economyPayNotify2", r);
		}
	}
	
	/**
	 * Fixes the number of decimal places in a number, 5.0 will become 5.00
	 * @param input The number to fix
	 * @return The fixed number
	 */
	
	public static String fixDecimals(double input){
		return Utils.twoDecimalPlaces(input);
	}
	
}
