package com.github.zathrus_writer.commandsex.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.SQLManager;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_whois {

	public static Boolean run(CommandSender sender, String alias, String[] args){
		if (args.length > 2 || args.length == 0){
			Commands.showCommandHelpAndUsage(sender, "cex_whois", alias);
			return true;
		}

		String whoIs = args[0];
		String pName = null;
		String pNick = null;

		int results = 0;
		int resultNo = 0;

		HashMap<String, String> players = new HashMap<String, String>();
		ResultSet res = SQLManager.query_res("SELECT player_name, nickname FROM " + SQLManager.prefix + "nicknames WHERE nickname LIKE ?", "%" + whoIs + "%");

		try {
			while (res.next()){
				players.put(res.getString("player_name"), res.getString("nickname"));
			}

			res.close();
		} catch (SQLException ex){
			if (CommandsEX.getConf().getBoolean("debugMode")){
				ex.printStackTrace();
			}
		}

		for (String nickname : players.values()){
			if (nickname.toLowerCase().contains(whoIs.toLowerCase())){
				results++;
			}
		}
		
		if (results == 0){
			LogHelper.showInfo("whoisNoResults", sender, ChatColor.RED);
			return true;
		}

		if (args.length == 2){
			if (args[1].matches(CommandsEX.intRegex)){
				resultNo = Integer.valueOf(args[1]);
				
				if (resultNo == 0){
					LogHelper.showInfo("whoisResult0", sender, ChatColor.RED);
					return true;
				}
			} else {
				LogHelper.showInfo("whoisResultNoNumeric", sender, ChatColor.RED);
				return true;
			}
		}

		if (results > 1){
			if (resultNo == 0){
				LogHelper.showInfo("whoisMultipleMatches1", sender, ChatColor.RED);
				ArrayList<String> playersFormatted = new ArrayList<String>();

				int counter = 1;
				for (String plName : players.keySet()){
					playersFormatted.add(ChatColor.GRAY + String.valueOf(counter) + " " + ChatColor.GOLD
							+ players.get(plName) + ChatColor.AQUA + " (" + ChatColor.GOLD + plName + ChatColor.AQUA + ")");
					counter++;
				}

				LogHelper.showInfo("whoisMultipleMatches2#####[" + Utils.implode(playersFormatted, ", "), sender);
				return true;
			}

			pName = players.keySet().toArray()[resultNo - 1].toString();
			pNick = players.values().toArray()[resultNo - 1].toString();
		} else {
			pName = players.keySet().toArray()[0].toString();
			pNick = players.values().toArray()[0].toString();
		}
		
		LogHelper.showInfo("whoisName#####[" + ChatColor.GOLD + pName, sender);
		LogHelper.showInfo("whoisNick#####[" + ChatColor.GOLD + pNick, sender);
		if (sender.hasPermission("cex.whois.ip")){
			LogHelper.showInfo("whoisIP#####[" + ChatColor.GOLD + (Bukkit.getPlayer(pName) == null ? "#####whoisIPOffline" : CommandsEX.playerIPs.get(pName.toLowerCase())), sender);
		}

		return true;
	}
}
