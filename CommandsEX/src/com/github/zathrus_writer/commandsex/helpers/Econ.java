package com.github.zathrus_writer.commandsex.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.SQLManager;

public class Econ {

	public static HashMap<String, Double> balances = new HashMap<String, Double>();
	public static double defaultBalance = CommandsEX.getConf().getDouble("economy.defaultBalance");
	
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
		CommandsEX.onDisableFunctions.add("com.github.zathrus_writer.commandsex.helpers.Econ#####onDisable");
	}
	
	public static void onDisable(CommandsEX p){
		saveDatabase();
	}
	
	public static void saveDatabase(){
		// purge accounts with default balance before we begin saving the database
		purgeAccounts();
		
		for (String account : balances.keySet()){
			double balance = balances.get(account);
			
			if (balance != defaultBalance){
				// insert the player into the economy database if they are in the HashMap but not in the database
				try {
					ResultSet rs = SQLManager.query_res("SELECT * FROM " + SQLManager.prefix + "economy WHERE player_name = ?", account);
					if (!rs.next()){
						SQLManager.query("INSERT " + (SQLManager.sqlType.equals("mysql") ? "" : "OR REPLACE ") + "INTO " + SQLManager.prefix + "economy (player_name, balance) SELECT ? AS player_name, ? AS balance", account, balance);
					}
					
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			// update their balance if it needs updated in the database
			try {
				ResultSet rs = SQLManager.query_res("SELECT player_name FROM " + SQLManager.prefix + "economy WHERE player_name = ? AND balance <> ?", account, balance);
				
				if (rs.next()){
					SQLManager.query("UPDATE " + SQLManager.prefix + "economy SET balance = ? WHERE player_name = ?", balance, account);
				}
				
				rs.close();
			} catch (SQLException e){
				e.printStackTrace();
			}
		}
		
		// delete all accounts from the database that are no longer in the HashMap
		// they might be deleted because of their account being purged
		try {
			ResultSet rs = SQLManager.query_res("SELECT player_name FROM " + SQLManager.prefix + "economy");
			while (rs.next()){
				String player = rs.getString("player_name");
				
				if (!balances.containsKey(player)){
					SQLManager.query("DELETE FROM " + SQLManager.prefix + "economy WHERE player_name = ?", player);
				}
			}
		} catch (SQLException e){
			e.printStackTrace();
		}

		SQLManager.query("DELETE FROM " + SQLManager.prefix + "economy WHERE balance = ?", defaultBalance);
	}
	
	/**
	 * Deletes all accounts that have a default balance
	 * Returns the amount of accounts purged
	 */
	
	public static int purgeAccounts(){
		int count = 0;
		for (String account : balances.keySet()){
			double balance = balances.get(account);
			if (balance == defaultBalance){
				balances.remove(account);
				count++;
			}
		}
		
		LogHelper.logInfo("Successfully purged " + count + " economy accounts with default balance");
		return count;
	}
}
