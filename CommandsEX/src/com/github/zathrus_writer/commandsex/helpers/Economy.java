package com.github.zathrus_writer.commandsex.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.SQLManager;

public class Economy {

	static HashMap<String, Double> balances = new HashMap<String, Double>();
	
	public static void init(CommandsEX plugin){
		// we can't store players balances without a database
		if (!CommandsEX.sqlEnabled){
			return;
		}
		
		SQLManager.query((SQLManager.sqlType.equals("mysql") ? "" : "BEGIN; ") + "CREATE TABLE IF NOT EXISTS " + SQLManager.prefix + "economy (player_name varchar(32), balance double precision)" + (SQLManager.sqlType.equals("mysql") ? " ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='stores player balances'" : "; COMMIT;"));
		ResultSet rs = SQLManager.query_res("SELECT * FROM " + SQLManager.prefix + "economy");
		
		// load balances into HashMap
		try {
			while (rs.next()){
				balances.put(rs.getString("player_name"), rs.getDouble("balance"));
			}
			
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// when CommandsEX is disabled, save the database
		CommandsEX.onDisableFunctions.add("com.github.zathrus_writer.commandsex.helpers.Economy#####onDisable");
	}
	
	public static void onDisable(CommandsEX p){
		saveDatabase();
	}
	
	public static void saveDatabase(){
		for (String account : balances.keySet()){
			double balance = balances.get(account);
			
			try {
				ResultSet rs = SQLManager.query_res("SELECT * FROM " + SQLManager.prefix + "economy WHERE player_name = ?", account);
				if (!rs.next()){
					SQLManager.query("INSERT " + (SQLManager.sqlType.equals("mysql") ? "" : "OR REPLACE ") + "INTO " + SQLManager.prefix + "economy (player_name, balance) SELECT ? AS player_name, ? AS balance", account, balance);
				}
				
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			try {
				ResultSet rs = SQLManager.query_res("SELECT player_name FROM " + SQLManager.prefix + "economy WHERE balance <> ?", balance);
				
				if (rs.next()){
					SQLManager.query("UPDATE " + SQLManager.prefix + "economy SET balance = ? WHERE player_name = ?", balance, account);
				}
				
				rs.close();
			} catch (SQLException e){
				e.printStackTrace();
			}
		}

		SQLManager.query("DELETE FROM " + SQLManager.prefix + "economy WHERE balance = ?", CommandsEX.getConf().getDouble("economy.defaultBalance"));
	}
	
	public static boolean hasAccount(String player){
		boolean hasAccount = false;
		
		try {
			ResultSet rs = SQLManager.query_res("SELECT FROM " + SQLManager.prefix + "economy WHERE player_name = ?", player);
			
			if (rs.next()){
				hasAccount = true;
			}
			
			rs.close();
			return hasAccount;
		} catch (SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static double getBalance(String player){
		if (hasAccount(player)){
			try {
				ResultSet rs = SQLManager.query_res("SELECT balance FROM " + SQLManager.prefix + "economy WHERE player_name = ?", player);
				rs.next();
				rs.close();
				return rs.getDouble("balance");
			} catch (SQLException e){
				e.printStackTrace();
				return CommandsEX.getConf().getDouble("economy.defaultBalance");
			}
		} else {
			return CommandsEX.getConf().getDouble("economy.defaultBalance");
		}
	}
	
	public static void createAccount(String player){
		createAccount(player, CommandsEX.getConf().getDouble("economy.defaultBalance"));
	}
	
	public static void createAccount(String player, double initialAmount){
		if (!hasAccount(player)){
			SQLManager.query("INSERT " + (SQLManager.sqlType.equals("mysql") ? "" : "OR REPLACE ") + "INTO " + SQLManager.prefix + "economy (player_name, balance) SELECT ? AS player_name, ? AS balance", player, initialAmount);
		}
	}
	
	public static boolean has(String player, double amount){
		double balance = getBalance(player);
		return balance >= amount;
	}
	
	public static void setBalance(String player, double amount){
		if (hasAccount(player)){
			SQLManager.query("UPDATE " + SQLManager.prefix + "economy SET balance = ? WHERE player_name = ?", amount, player);
		} else {
			SQLManager.query("INSERT " + (SQLManager.sqlType.equals("mysql") ? "" : "OR REPLACE ") + "INTO " + SQLManager.prefix + "economy (player_name, balance) SELECT ? AS player_name, ? AS balance", player, amount);
		}
	}
	
	public static void withdraw(String player, double amount){
		if (hasAccount(player)){
			try {
				ResultSet rs = SQLManager.query_res("SELECT balance FROM " + SQLManager.prefix + "economy WHERE player_name = ?", player);
				rs.next();
				SQLManager.query("UPDATE " + SQLManager.prefix + "economy SET balance = ? WHERE player_name = ?", rs.getDouble("balance") - amount, player);
				rs.close();
			} catch (SQLException e){
				e.printStackTrace();
			}
		} else {
			createAccount(player, CommandsEX.getConf().getDouble("economy.defaultBalance") - amount);
		}
	}
	
	public static void deposit(String player, double amount){
		if (hasAccount(player)){
			try {
				ResultSet rs = SQLManager.query_res("SELECT balance FROM " + SQLManager.prefix + "economy WHERE player_name = ?", player);
				rs.next();
				SQLManager.query("UPDATE " + SQLManager.prefix + "economy SET balance = ? WHERE player_name = ?", rs.getDouble("balance") + amount, player);
				rs.close();
			} catch (SQLException e){
				e.printStackTrace();
			}
		} else {
			createAccount(player, CommandsEX.getConf().getDouble("economy.defaultBalance") + amount);
		}
	}

}
