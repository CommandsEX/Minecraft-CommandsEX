package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;

public class Command_cex_repair {
	
	/***
	 * REPAIR - repairs any tool/armour in hand
	 * @author Kezz101
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		
		// Check they are not console
		if (PlayerHelper.checkIsPlayer(sender)) {

			// Get some variables
			Player player = (Player)sender;
			ItemStack itemInHand = player.getItemInHand();
			int itemdura = itemInHand.getDurability();
			Material material = Material.getMaterial(itemInHand.getTypeId());
			
			// Check if it can be repaired
			if (material.isBlock() || material.getMaxDurability() < 1) {
				LogHelper.showWarning("repairCannotRepair", sender);
				return true;
			}
			
			// Check if it can be repaired
			if(itemdura == 0) {
				LogHelper.showWarning("repairAlreadyFixed", sender);
				return true;
			}
			
			// Repair item
			itemInHand.setDurability((short)0);
			
			// Send a message
			LogHelper.showInfo("repairDone#####" + "[" + material, sender);

		}
		
		return true;
	}
}
