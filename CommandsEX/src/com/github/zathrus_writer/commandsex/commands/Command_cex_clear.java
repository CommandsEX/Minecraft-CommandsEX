package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_clear {
	/***
	 * CLEAR - clears player's inventory
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		String pName;
		String clearArgument = null;
		Player p;
		
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (Utils.checkCommandSpam(player, "cex_clear")){
				return true;
			}
		}
		
		// get name of the player to clear inventory for
		if ((args.length > 0) && !args[0].equalsIgnoreCase("all") && !args[0].equalsIgnoreCase("backpack") && !args[0].equalsIgnoreCase("quick") && !args[0].equalsIgnoreCase("armor")) {
			// check permissions first
			
			if ((sender instanceof Player) && !Permissions.checkPerms((Player) sender, "cex.clear.all")) {
				return true;
			}

			p = Bukkit.getPlayer(args[0]);
			if (p == null) {
				LogHelper.showWarning("invalidPlayer", sender);
				return true;
			} else {
				pName = p.getName();
			}
		} else {
			// clearing our own inventory
			if (PlayerHelper.checkIsPlayer(sender)) {
				pName = sender.getName();
			} else {
				// console does not have an inventory :P
				return true;
			}
		}

		// check if we have second argument, i.e. what to clear
		if (args.length > 0) {
			for (String s : args) {
				if (s.equalsIgnoreCase("all") || s.equalsIgnoreCase("backpack") || s.equalsIgnoreCase("quick") || s.equalsIgnoreCase("armor")) {
					clearArgument = s.toLowerCase();
					break;
				}
			}
		}
		
		// clear up :)
		if (
				((sender instanceof Player) && !Utils.checkCommandSpam((Player) sender, "clear-inventory") && Permissions.checkPerms((Player) sender, "cex.clear"))
				||
				!(sender instanceof Player)
		) {
			p = Bukkit.getPlayer(pName);
			// clearing armor?
			if ((clearArgument != null) && (clearArgument.equals("all") || clearArgument.equals("armor"))) {
				p.getInventory().setArmorContents(null);
			}
			
			// clearing backpack?
			if (
					(clearArgument == null)
					||
					((clearArgument != null) && (clearArgument.equals("all") || clearArgument.equals("backpack")))
			) {
				ItemStack[] inventory = p.getInventory().getContents();
				Integer iLength = inventory.length;
				for (Integer i = 0; i < iLength; i++) {
					if ((i > 8) && (inventory[i] != null)) {
						inventory[i].setType(Material.AIR);
					}
				}
				
				p.getInventory().setContents(inventory);
			}
			
			// clearing quick bar?
			if (
					(clearArgument == null)
					||
					((clearArgument != null) && (clearArgument.equals("all") || clearArgument.equals("quick")))
			) {
				ItemStack[] inventory = p.getInventory().getContents();
				Integer iLength = inventory.length;
				for (Integer i = 0; i < iLength; i++) {
					if ((i < 9) && (inventory[i] != null)) {
						inventory[i].setType(Material.AIR);
					}
				}
				
				p.getInventory().setContents(inventory);
			}
			
			// tell the cleaner and the player whose inventory we cleared
			LogHelper.showInfo("inventoryCleared", sender);
			if (!p.getName().equals(sender.getName())) {
				if (sender.getName().toLowerCase() == "console") {
					LogHelper.showInfo("yourInventoryCleared", p);
				} else {
					LogHelper.showInfo("inventoryClearedBy#####[" + sender.getName(), p);
				}
				
			}
		}
		
        return true;
	}
}
