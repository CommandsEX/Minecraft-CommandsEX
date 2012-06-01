package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;

public class Command_cex_info {
	
	/***
	 * INFO - displays text from info.txt
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		
		// Command Variables
		Player player = (Player)sender;
		String player1 = player.getDisplayName();
		String info = CommandsEX.getConf().getString("Info");
		
		info = info.replaceAll("&0", ChatColor.BLACK + "");
		info = info.replaceAll("&1", ChatColor.DARK_BLUE + "");
		info = info.replaceAll("&2", ChatColor.DARK_GREEN + "");
		info = info.replaceAll("&3", ChatColor.DARK_AQUA + "");
		info = info.replaceAll("&4", ChatColor.DARK_RED + "");
		info = info.replaceAll("&5", ChatColor.DARK_PURPLE + "");
		info = info.replaceAll("&6", ChatColor.GOLD + "");
		info = info.replaceAll("&7", ChatColor.GRAY + "");
		info = info.replaceAll("&8", ChatColor.DARK_GRAY + "");
		info = info.replaceAll("&9", ChatColor.BLUE + "");
		info = info.replaceAll("&a", ChatColor.GREEN + "");
		info = info.replaceAll("&b", ChatColor.AQUA + "");
		info = info.replaceAll("&c", ChatColor.RED + "");
		info = info.replaceAll("&d", ChatColor.LIGHT_PURPLE + "");
		info = info.replaceAll("&e", ChatColor.YELLOW + "");
		info = info.replaceAll("&f", ChatColor.WHITE + "");
		info = info.replaceAll("&k", ChatColor.MAGIC + "");
		info = info.replaceAll("&l", ChatColor.BOLD + "");
		info = info.replaceAll("&m", ChatColor.STRIKETHROUGH + "");
		info = info.replaceAll("&n", ChatColor.UNDERLINE + "");
		info = info.replaceAll("&o", ChatColor.ITALIC + "");
		info = info.replaceAll("&r", ChatColor.RESET + "");
		info = info.replaceAll("%PLAYER%", player1);
		
		// Show info
		player.sendMessage(info);
		
		return true;
	}
}
