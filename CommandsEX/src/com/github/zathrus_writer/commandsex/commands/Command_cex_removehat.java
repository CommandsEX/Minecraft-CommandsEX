package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;

public class Command_cex_removehat {
	
	/***
	 * REMOVEHAT - sets your helmet to id 0
	 * HAPPY 1000th (BUKKIT DEV) DOWNLOAD
	 * @author Kezz101
	 * @param sender
	 * @param args
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		
		// Check if player
		if(!(PlayerHelper.checkIsPlayer(sender))) {
			LogHelper.logWarning("noConsole");
			return true;
		}
		
		// Variables
		Player player = (Player)sender;
		PlayerInventory inv = player.getInventory();
		ItemStack head = inv.getHelmet();
		int emptySlot = inv.firstEmpty();
		
		// If no helmet
		if(head == null) {
			LogHelper.showWarning("removehatNoHat", sender);
			return true;
		}
		
		// If no space for helmet
		if(emptySlot == -1) {
			LogHelper.showWarning("removehatNoSpace", sender);
			return true;
		}
		
		// Remove hat
		inv.setHelmet(null);
		inv.setItem(emptySlot, head);
		
		// Alert sender
		LogHelper.showInfo("removehatSucess", sender);
		
		return true;
	}
}
