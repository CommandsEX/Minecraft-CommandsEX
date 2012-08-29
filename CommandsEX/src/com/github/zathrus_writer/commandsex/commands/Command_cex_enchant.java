package com.github.zathrus_writer.commandsex.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.ClosestMatches;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_enchant {

	/***
	 * Enchant - A full enchanting suite to cover all enchanting needs
	 * @param sender
	 * @param alias
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args){
		if (!PlayerHelper.checkIsPlayer(sender)){
			return true;
		}
		
		Player player = (Player) sender;
		if (Utils.checkCommandSpam(player, "cex_enchant")){
			return true;
		}
		
		if (args.length == 0 || args[0].equalsIgnoreCase("list")){
			List<String> enchantments = new ArrayList<String>();
			
			for (Enchantment en : Enchantment.values()){
				enchantments.add(Utils.userFriendlyNames(en.getName()));
			}
			
			LogHelper.showInfo("enchantList#####[" + ChatColor.GOLD + Utils.implode(enchantments, ChatColor.AQUA + ", " + ChatColor.GOLD), sender);
			return true;
		}
		
		ItemStack inHand = player.getInventory().getItemInHand();
		int level = -1;
		boolean unSafe = false;
		
		if (args[0].equalsIgnoreCase("clear")){
			if (Permissions.checkPerms(sender, "cex.enchant.clear")){
				int count = 0;
				for (Enchantment ench : inHand.getEnchantments().keySet()){
					inHand.removeEnchantment(ench);
				}
				
				LogHelper.showInfo("enchantClear#####[" + ChatColor.GOLD + count + ChatColor.AQUA + " #####enchantEnchantments", sender);
			}
			
			return true;
		}
		
		if (args[0].equalsIgnoreCase("remove")){
			if (Permissions.checkPerms(sender, "cex.enchant.remove")){
				if (args.length > 2){
					Commands.showCommandHelpAndUsage(sender, "cex_enchant", alias);
					return true;
				}
				
				List<Enchantment> matches = ClosestMatches.enchantments(args[1]);
				Enchantment ench;
				
				if (matches.size() > 0){
					ench = matches.get(0);
				} else {
					LogHelper.showInfo("enchantNotFound", sender, ChatColor.RED);
					return true;
				}
				
				if (!inHand.containsEnchantment(ench)){
					LogHelper.showInfo("enchantRemoveNotFound#####[" + ChatColor.GOLD + Utils.userFriendlyNames(inHand.getType().name()), sender);
					return true;
				}
				
				inHand.removeEnchantment(ench);
				LogHelper.showInfo("enchantRemoveSuccess#####[ " + ChatColor.GOLD + Utils.userFriendlyNames(ench.getName()) + ChatColor.AQUA + "#####from#####[ " + Utils.userFriendlyNames(inHand.getType().name()), sender);
			}
			
			return true;
		}
		
		List<Enchantment> matches = ClosestMatches.enchantments(args[0]);
		Enchantment ench = null;
		
		if (args[0].equalsIgnoreCase("all")){
			if (Permissions.checkPerms(sender, "cex.enchant.all")){
				if (matches.size() > 0){
					ench = matches.get(0);
				} else {
					LogHelper.showInfo("enchantNotFound", sender, ChatColor.RED);
					return true;
				}
				
				if (args.length > 3){
					Commands.showCommandHelpAndUsage(sender, "cex_enchant", alias);
					return true;
				}

				if (args.length == 3){
					if (args[1].matches(CommandsEX.intRegex)){
						Integer.valueOf(args[1]);
					} else {
						LogHelper.showInfo("valueNotNumeric", sender, ChatColor.RED);
						return true;
					}
					
					if (args[2].equalsIgnoreCase("-u") || args[2].equalsIgnoreCase("unsafe")){
						unSafe = true;
					}
				} else if (args.length == 2){
					if (args[1].equalsIgnoreCase("-u") || args[1].equalsIgnoreCase("unsafe")){
						unSafe = true;
					}
				}

				int count = 0;
				
				if (inHand.getType().equals(Material.AIR)){
					LogHelper.showInfo("enchantFist", sender, ChatColor.RED);
					return true;
				}
				
				for (Enchantment en : Enchantment.values()){
					if (player.hasPermission("cex.enchant." + en.getName().toLowerCase().replaceAll("_", ""))){
						int enchantLevel = level;
						
						if (level == -1 || enchantLevel > en.getMaxLevel()){
							enchantLevel = en.getMaxLevel();
						}
						
						if (en.canEnchantItem(inHand)){
							inHand.addEnchantment(en, enchantLevel);
							count++;
						} else {
							if (unSafe){
								inHand.addUnsafeEnchantment(en, enchantLevel);
								count++;
							}
						}
					}
				}
				
				LogHelper.showInfo("enchantAddedAll#####[" + ChatColor.GOLD + count + ChatColor.AQUA + " #####enchantEnchantments", sender);
			}
			return true;
		}
		
		if (matches.size() > 0){
			ench = matches.get(0);
		} else {
			LogHelper.showInfo("enchantNotFound", sender, ChatColor.RED);
			return true;
		}
		
		if (!player.hasPermission("cex.enchant." + ench.getName().toLowerCase().replaceAll("_", ""))){
			LogHelper.showInfo("enchantEnchantmentNoPerm", sender, ChatColor.RED);
			return true;
		}
		
		boolean nonNumeric = false;
		
		if (args.length == 2){
			if (args[1].equalsIgnoreCase("-u") || args[1].equalsIgnoreCase("unsafe")){
				unSafe = true;
			} else if (args[1].matches(CommandsEX.intRegex)){
				level = Integer.valueOf(args[1]);
			} else {
				nonNumeric = true;
			}
		}
		
		if (args.length == 3){
			if (args[2].equalsIgnoreCase("-u") || args[2].equalsIgnoreCase("unsafe")){
				unSafe = true;
			} else if (args[1].matches(CommandsEX.intRegex)){
				level = Integer.valueOf(args[1]);
			} else {
				nonNumeric = true;
			}
		}
		
		if (inHand.getType().equals(Material.AIR)){
			LogHelper.showInfo("enchantFist", sender, ChatColor.RED);
			return true;
		}
		
		if (!ench.canEnchantItem(inHand) && !unSafe){
			LogHelper.showInfo("enchantNotSafe", sender, ChatColor.RED);
			return true;
		}
		
		if (nonNumeric){
			LogHelper.showInfo("valueNotNumeric", sender, ChatColor.RED);
			return true;
		}
		
		if (level == -1 || level > ench.getMaxLevel()){
			level = ench.getMaxLevel();
		}
		
		if (unSafe){
			inHand.addUnsafeEnchantment(ench, level);
		} else {
			inHand.addEnchantment(ench, level);
		}
		
		LogHelper.showInfo("enchantAdded#####[" + ChatColor.GOLD + Utils.userFriendlyNames(ench.getName())
				+ ChatColor.AQUA + " (" + ChatColor.GOLD + ench.getId() + ChatColor.AQUA + ") #####enchantAddedLevel#####["
				+ ChatColor.GOLD + level, sender);
		return true;
	}
	
}