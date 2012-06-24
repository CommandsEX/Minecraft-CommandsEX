package com.github.zathrus_writer.commandsex.helpers;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemSpawning {

	/***
	 * Used by /item and /give command to spawn items
	 * @author iKeirNez
	 * @param sender
	 * @param target
	 * @param material
	 * @param amount
	 * @param damage
	 */
	
	public static void giveItem(CommandSender sender, Player target, Material material, int amount, short damage){
		ItemStack stack = new ItemStack(material);
		
		stack.setDurability(damage);
		stack.setAmount(amount);
		
		target.getInventory().addItem(stack);
		
		if (sender != target){
			LogHelper.showInfo("itemYouGave#####[" + target.getName() + " " + amount + " " + Utils.userFriendlyNames(material.name()) + (damage != 0 ? " (" + damage + ")": ""), sender, ChatColor.AQUA);
			LogHelper.showInfo("itemGiveSuccess#####[" + amount + " " + Utils.userFriendlyNames(material.name()) + (damage != 0 ? " (" + damage + ")": "") + " #####itemFrom#####[" + sender.getName(), target, ChatColor.AQUA);
		} else {
			LogHelper.showInfo("itemGiveSuccess#####[" + amount + " " + Utils.userFriendlyNames(material.name()) + (damage != 0 ? " (" + damage + ")": ""), sender, ChatColor.AQUA);
		}
	}
	
}
