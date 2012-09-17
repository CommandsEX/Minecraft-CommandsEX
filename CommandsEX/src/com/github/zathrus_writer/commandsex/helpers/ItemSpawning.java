package com.github.zathrus_writer.commandsex.helpers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemSpawning {

	/***
	 * Used by /item and /give command to handle the spawning of items
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
		
		String itemArg = (function.equals("give") ? args[1] : args[0]);
		String amountArg = (function.equals("give") ? (args.length > 2 ? args[2] : null) : (args.length > 1 ? args[1] : null));
		String damageArg = (function.equals("give") ? (args.length > 3 ? args[3] : null) : (args.length > 2 ? args[2] : null));
		
		if (itemArg.contains(":") && damageArg != null){
			Commands.showCommandHelpAndUsage(sender, "cex_" + function, alias);
			return;
		}
		
		List<String> listArgs = new ArrayList<String>();
		if (itemArg != null){ listArgs.add(itemArg); }
		if (amountArg != null){ listArgs.add(amountArg); }
		if (damageArg != null){ listArgs.add(damageArg); }
		
		ItemStack stack = ItemStackParser.parse(listArgs.toArray(new String[]{}), sender);
		if (stack == null){ return; }
		target.getInventory().addItem(stack);

		Material item = stack.getType();
		int amount = stack.getAmount();
		short damage = stack.getDurability();
		
		// Messages will be different if the player spawned the item for himself, or someone else
		if (sender != target){
			LogHelper.showInfo("itemYouGave#####[" + Nicknames.getNick(target.getName()) + " " + amount + " " + Utils.userFriendlyNames(item.name()) + (damage != 0 ? " (" + damage + ")": ""), sender, ChatColor.AQUA);
			LogHelper.showInfo("itemGiveSuccess#####[" + amount + " " + Utils.userFriendlyNames(item.name()) + (damage != 0 ? " (" + damage + ")": "") + " #####itemFrom#####[" + Nicknames.getNick(sender.getName()), target, ChatColor.AQUA);
		} else {
			LogHelper.showInfo("itemGiveSuccess#####[" + amount + " " + Utils.userFriendlyNames(item.name()) + (damage != 0 ? " (" + damage + ")": ""), sender, ChatColor.AQUA);
		}
	}
}
