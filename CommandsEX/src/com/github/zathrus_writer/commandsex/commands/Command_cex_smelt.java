package com.github.zathrus_writer.commandsex.commands;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_smelt {

	public static Boolean run(CommandSender sender, String alias, String[] args){

		if (PlayerHelper.checkIsPlayer(sender)){
			Player player = (Player) sender;

			if (Utils.checkCommandSpam(player, "cex_smelt")){
				return true;
			}

			if (args.length == 0){
				smeltItems(player, player.getItemInHand().getAmount());
			} else if (args.length == 1){
				try {
					int amount = Integer.parseInt(args[0].toString());
					smeltItems(player, amount);
				} catch (Exception e) {
					Commands.showCommandHelpAndUsage(player, "cex_smelt", alias);
				}
			} else {
				Commands.showCommandHelpAndUsage(player, "cex_smelt", alias);
			}
		}

		return true;
	}
	
	private static void smeltItems(Player player, int amount){
		ItemStack itemHand = player.getItemInHand();
		PlayerInventory inv = player.getInventory();
		
		// Get the output if that item was smelted
		ItemStack result = null;
		Iterator<Recipe> iter = Bukkit.recipeIterator();
		while (iter.hasNext()){
			Recipe recipe = iter.next();
			if (recipe instanceof FurnaceRecipe){
				if (((FurnaceRecipe) recipe).getInput().getType() == player.getItemInHand().getType()){
					result = recipe.getResult();
				}
			}
		}
		
		if (result != null){
			// Check that the player has the correct amount of items
			if (inv.contains(itemHand.getTypeId())){
				// Remove the item using the type id, amount and durability (damage value)
				inv.removeItem(new ItemStack(itemHand.getTypeId(), amount, itemHand.getDurability()));
				inv.addItem(new ItemStack(result.getTypeId(), amount));
				LogHelper.showInfo("smeltSuccess#####[ " + Utils.userFriendlyNames(itemHand.getType().name()), player, ChatColor.GREEN);
			} else {
				LogHelper.showInfo("smeltNotEnough#####[ " + Utils.userFriendlyNames(itemHand.getType().name()), player, ChatColor.RED);
			}
		} else {
			LogHelper.showInfo("smeltNotSmeltable", player, ChatColor.RED);
		}
	}
	
}
