package com.github.zathrus_writer.commandsex.helpers;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemSpawning {

	/***
	 * Used by /item and /give command to handle the spawning of item
	 * @param sender
	 * @param args
	 * @param function
	 * @param alias
	 */
	
	public static void giveItem(CommandSender sender, String[] args, String function, String alias){

		if (sender instanceof Player){
			Player player = (Player) sender;
			if (Utils.checkCommandSpam(player, "cex_" + function)){
				return;
			}
		}

		if ((function.equals("give") ? args.length < 2 || args.length > 4 : args.length < 1 || args.length > 3)){
			Commands.showCommandHelpAndUsage(sender, "cex_" + function, alias);
			return;
		}

		String itemArg = (function.equals("give") ? args[1] : args[0]);
		String amountArg = (function.equals("give") ? (args.length > 2 ? args[2] : null) : (args.length > 1 ? args[1] : null));
		String damageArg = (function.equals("give") ? (args.length > 3 ? args[3] : null) : (args.length > 2 ? args[2] : null));
		
		Material item;
		short damage = 0;
		int amount = -1;

		Player target;

		if (function.equals("give")){
			target = Bukkit.getPlayer(args[0]);

			if (target == null){
				LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
				return;
			}
		} else {
			if (!(sender instanceof Player)){
				Commands.showCommandHelpAndUsage(sender, "cex_" + function, alias);
				return;
			}

			target = (Player) sender;
		}
		
		if (itemArg.contains(":") && damageArg != null){
			Commands.showCommandHelpAndUsage(sender, "cex_" + function, alias);
			return;
		}
		
		if (!itemArg.contains(":")){
			List<Material> matches = ClosestMatches.material(itemArg);

			if (matches.size() > 0){
				item = matches.get(0);
			} else {
				LogHelper.showInfo("itemNotFound", sender, ChatColor.RED);
				return;
			}
		} else {
			String[] data = itemArg.split(":");
			if (ClosestMatches.material(data[0]).size() > 0){
				item = ClosestMatches.material(data[0]).get(0);
			} else {
				LogHelper.showInfo("itemNotFound", sender, ChatColor.RED);
				return;
			}

			try {
				damage = Short.valueOf(data[1]);
			} catch (Exception e) {
				if (item == Material.WOOL && ClosestMatches.dyeColor(data[1]).size() > 0){
					damage = ClosestMatches.dyeColor(data[1]).get(0).getData();
				} else {
					LogHelper.showInfo("itemIncorrectDamage", sender, ChatColor.RED);
					Commands.showCommandHelpAndUsage(sender, "cex_" + function, alias);
					return;
				}
			}
		}

		if (damageArg != null){
			try {
				damage = Short.valueOf(damageArg);
			} catch (Exception e) {
				List<DyeColor> matches = ClosestMatches.dyeColor(damageArg);
				
				if (item == Material.WOOL && matches.size() > 0){
					damage = matches.get(0).getData();
				} else {
					LogHelper.showInfo("itemIncorrectDamage", sender, ChatColor.RED);
					Commands.showCommandHelpAndUsage(sender, "cex_" + function, alias);
					return;
				}
			}
		}
		
		if (amountArg != null){
			try {
				amount = Integer.valueOf(amountArg);
			} catch (Exception e){
				LogHelper.showInfo("itemIncorrectDamageValue", sender, ChatColor.RED);
				Commands.showCommandHelpAndUsage(sender, "cex_" + function, alias);
				return;
			}
		}



		ItemStack stack = new ItemStack(item);

		if (amount == -1){
			amount = stack.getMaxStackSize();
		}

		stack.setAmount(amount);
		// Set the stacks durability (damage value)
		stack.setDurability(damage);

		target.getInventory().addItem(stack);

		// Messages will be different if the player spawned the item for himself, or someone else
		if (sender != target){
			LogHelper.showInfo("itemYouGave#####[" + target.getName() + " " + amount + " " + Utils.userFriendlyNames(item.name()) + (damage != 0 ? " (" + damage + ")": ""), sender, ChatColor.AQUA);
			LogHelper.showInfo("itemGiveSuccess#####[" + amount + " " + Utils.userFriendlyNames(item.name()) + (damage != 0 ? " (" + damage + ")": "") + " #####itemFrom#####[" + sender.getName(), target, ChatColor.AQUA);
		} else {
			LogHelper.showInfo("itemGiveSuccess#####[" + amount + " " + Utils.userFriendlyNames(item.name()) + (damage != 0 ? " (" + damage + ")": ""), sender, ChatColor.AQUA);
		}
	}
}
