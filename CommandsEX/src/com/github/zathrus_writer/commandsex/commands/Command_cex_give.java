package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.ClosestMatches;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.ItemSpawning;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_give extends ItemSpawning {

	/***
	 * Give - Gives a player an item
	 * Could be improved in the future by adding enchantment support
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static boolean run(CommandSender sender, String alias, String[] args){
		
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (Utils.checkCommandSpam(player, "cex_give")){
				return true;
			}
		}
		
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (Utils.checkCommandSpam(player, "cex_give")){
				return true;
			}
		}
		
		if (args.length < 2 || args.length > 3){
			System.out.println("Incorrect args");
			Commands.showCommandHelpAndUsage(sender, "cex_give", alias);
		} else {
			Material item;
			short damage = 0;
			int amount = -1;
		
			Player target = Bukkit.getPlayer(args[0]);
			if (target == null){
				LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
				return true;
			}
			
			if (args[1].contains(":")){
				String[] data = args[1].split(":");
				if (ClosestMatches.material(data[0]).size() > 0){
					item = ClosestMatches.material(data[0]).get(0);
				} else {
					LogHelper.showInfo("itemNotFound", sender, ChatColor.RED);
					return true;
				}
				
				try {
					damage = Short.valueOf(args[1].split(":")[1]);
				} catch (Exception e) {
					if (item == Material.WOOL && ClosestMatches.dyeColor(data[1]).size() > 0){
						damage = ClosestMatches.dyeColor(data[1]).get(0).getData();
					} else {
						LogHelper.showInfo("itemIncorrectDamage", sender, ChatColor.RED);
						Commands.showCommandHelpAndUsage(sender, "cex_give", alias);
						return true;
					}
					
				}
			} else {
				if (ClosestMatches.material(args[1]).size() > 0){
					item = ClosestMatches.material(args[1]).get(0);
				} else {
					LogHelper.showInfo("itemNotFound", sender, ChatColor.RED);
					return true;
				}
			}
			
			if (args.length == 3){
				try {
					amount = Integer.valueOf(args[2]);
				} catch (Exception e){
					LogHelper.showInfo("itemIncorrectDamageValue", sender, ChatColor.RED);
					Commands.showCommandHelpAndUsage(sender, "cex_give", alias);
					return true;
				}
			}
			
			giveItem(sender, target, item, amount, damage);
		}
		
		return true;
		
	}
	
}
