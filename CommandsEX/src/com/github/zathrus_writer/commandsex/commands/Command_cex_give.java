package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.command.CommandSender;

import com.github.zathrus_writer.commandsex.helpers.ItemSpawning;

public class Command_cex_give extends ItemSpawning {

	/***
	 * Give - Gives a player an item
	 * Could be improved in the future by adding enchantment support
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static boolean run(CommandSender sender, String alias, String[] args){
		giveItem(sender, args, "give", alias);
		return true;
	}
	
}
