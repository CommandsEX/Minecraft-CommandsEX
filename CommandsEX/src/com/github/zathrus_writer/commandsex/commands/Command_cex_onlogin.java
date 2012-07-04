package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.SQLManager;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_onlogin {
/***
 * ONLOGIN - Creates an entry in the sql table for onlogin by using
 *  /onlogin <player> <command goes here> 
 * @author EnvisionRed
 * @param sender
 * @param args
 * @return
 */
	public static Boolean run(CommandSender sender, String alias, String[] args){
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if(Utils.checkCommandSpam(player, "cex_onlogin")) {
				return true;
			}
		}
		if (args.length == 0) {
			Commands.showCommandHelpAndUsage(sender, "cex_onlogin", alias);
			return true;
		}
		StringBuilder sb = new StringBuilder(300);
		for (String arg : args) {
			sb.append(arg).append(" ");
		}
		String command = sb.toString();
		String targetName = args[0];
		Server server = sender.getServer();
		Player target = server.getPlayer(targetName);
		if (target != null) {
			if(target.hasPermission("cex.onlogin.exempt")) {
				LogHelper.showInfo("onLoginTargetExempt", sender, ChatColor.RED);
				return true;
			} else{
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
				return true;
			}
		} else {
			long TimeStamp = System.currentTimeMillis();
			if(!CommandsEX.sqlEnabled) return true;
			String sqlPrefix = CommandsEX.getConf().getString("prefix", "");
			try {	
			SQLManager.query("CREATE TABLE IF NOT EXISTS " + sqlPrefix + "onlogin (Player VARCHAR(16), Command VARCHAR(300), TimeStamp BIGINT);");
			SQLManager.query("INSERT INTO " +sqlPrefix+"onlogin (Player, Command, TimeStamp) values ('" + targetName 
					+ "', '" + command + "', '" + TimeStamp + "');");
			return true;
			} catch (Exception e) {
				LogHelper.showInfo("onLoginSyntaxError", sender, ChatColor.RED);				
			}
		}
		return true;
	}
}