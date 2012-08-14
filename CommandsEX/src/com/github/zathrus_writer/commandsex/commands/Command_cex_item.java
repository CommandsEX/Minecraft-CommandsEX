package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.command.CommandSender;

import com.github.zathrus_writer.commandsex.helpers.ItemSpawning;

public class Command_cex_item extends ItemSpawning {
	/***
	 * ITEM - Allows a player to spawn an item by using /i <item-name>:[damage-value] [amount]
	 * Could be improved in the future by adding support for enchantments
	 * @author iKeirNez
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		giveItem(sender, args, "item", alias);
		return true;
	}
}
