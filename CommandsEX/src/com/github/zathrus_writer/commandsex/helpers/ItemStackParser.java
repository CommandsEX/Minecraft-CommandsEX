package com.github.zathrus_writer.commandsex.helpers;

import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import com.github.zathrus_writer.commandsex.CommandsEX;

public class ItemStackParser {

	/**
	 * Helper class to parse arguments into an item stack
	 * Arguments must be formatted as below
	 * 
	 * <material>[:damage] [amount] [damage]
	 * <> - required [] - optional
	 * 
	 * Senders argument is optional, used if a CommandSender should be used
	 * @param args
	 * @param senders
	 * @return
	 */
	
	// TODO expand this to enchanting
	public static ItemStack parse(String[] args, CommandSender...senders){
		CommandSender sender = (senders.length > 0 ? senders[0] : null);
		
		Material material;
		int amount = -1;
		short damage = 0;
		
		if (args.length == 0){
			return null;
		}
		
		if (args[0].contains(":")){
			String[] data = args[0].split(":");

			List<Material> materialMatches = ClosestMatches.material(data[0]);
			if (materialMatches.size() > 0){
				material = materialMatches.get(0);
			} else {
				if (sender != null){ LogHelper.showWarning("itemNotFound", sender); }
				return null;
			}
			
			if (data[1].matches(CommandsEX.intRegex)){
				damage = Short.valueOf(data[1]);
			} else {
				List<DyeColor> dyeMatches = ClosestMatches.dyeColor(data[1]);
				if ((material == Material.WOOL || material == Material.INK_SACK) && dyeMatches.size() > 0){
					damage = dyeMatches.get(0).getData();
				} else {
					if (sender != null){ LogHelper.showWarning("itemIncorrectDamage", sender); }
					return null;
				}
			}
		} else {
			List<Material> materialMatches = ClosestMatches.material(args[0]);
			if (materialMatches.size() > 0){
				material = materialMatches.get(0);
			} else {
				if (sender != null){ LogHelper.showWarning("itemNotFound", sender); }
				return null;
			}
		}
		
		if (args.length > 1){
			if (args[1].matches(CommandsEX.intRegex)){
				amount = Integer.valueOf(args[1]);
			} else {
				if (sender != null){ LogHelper.showWarning("itemIncorrectAmount", sender); }
				return null;
			}
		}
		
		if (args.length > 2){
			if (args[2].matches(CommandsEX.intRegex)){
				damage = Short.valueOf(args[2]);
			} else {
				List<DyeColor> dyeMatches = ClosestMatches.dyeColor(args[2]);
				if (dyeMatches.size() > 0){
					damage = dyeMatches.get(0).getData();
				} else {
					if (sender != null){ LogHelper.showWarning("itemIncorrectAmount", sender); }
					return null;
				}
			}
		}
		
		if (amount == -1){
			amount = material.getMaxStackSize();
		}
		
		return new ItemStack(material, amount, damage);
	}
	
}
