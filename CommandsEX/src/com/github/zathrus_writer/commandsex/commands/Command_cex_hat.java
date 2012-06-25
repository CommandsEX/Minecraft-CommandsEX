package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;

public class Command_cex_hat {
	
	/***
	 * HAT - puts a block on yo head!
	 * HAPPY 1000th (BUKKIT DEV) DOWNLOAD
	 * @author Kezz101
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		
		// Check if player
		if(PlayerHelper.checkIsPlayer(sender)) {
		
			// Variables
			Player player = (Player)sender;
			ItemStack hand = player.getItemInHand();
			PlayerInventory inv = player.getInventory();
			ItemStack head = inv.getHelmet();
			int handAmount = hand.getAmount();
			Material handID = Material.getMaterial(hand.getTypeId());
			ItemStack forHead = new ItemStack(handID, 1);
			
			// Check that head is empty
			if(head != null) {
				LogHelper.showWarning("hatHeadFull", sender);
				return true;
			}

			// Make sure the block going onto the players head is a block
			if(!forHead.getType().isBlock()){
				LogHelper.showInfo("hatNotBlock", sender, ChatColor.RED);
				return true;
			}

			// Check for item in hand
			if(hand.getTypeId() == 0) {
				LogHelper.showWarning("hatHandEmpty", sender);
				return true;
			}
			
			// Get new hand amount
			if(handAmount > 1) {
				int handAmountNEW = handAmount - 1;
				hand.setAmount(handAmountNEW);
			} else {
				inv.setItemInHand(null);
			}
			
			// Check for anything that has a damage value (durability)
			if (hand.getDurability() != 0) {
	            short handDurability = hand.getDurability();
	            forHead = new ItemStack(handID, 1, handDurability);
	        }
			
			// HELMET THEM UP
			inv.setHelmet(forHead);
			
			// Send message
			LogHelper.showInfo("hatNew", sender);
			LogHelper.showInfo("hatHowToRem", sender);
			
			// done.
		}
		
		return true;
	}
}
