package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.ClosestMatches;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.ItemSpawning;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_item extends ItemSpawning {
	/***
	 * ITEM - Allows a player to spawn an item by using /i <item-name>:[damage-value] [amount]
	 * Could be improved in the future by adding support for enchantments
	 * @author iKeirNez
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player = (Player) sender;

			if (Utils.checkCommandSpam(player, "cex_item")){
				return true;
			}

			if (args.length == 0 || args.length > 2){
				Commands.showCommandHelpAndUsage(player, "cex_item", alias);
			} else {
				Material item;
				short damage = 0;
				int amount = -1;

				if (args[0].contains(":")){
					String[] data = args[0].split(":");
					
					if (ClosestMatches.material(data[0]).size() > 0){
						item = ClosestMatches.material(data[0]).get(0);
					} else {
						LogHelper.showInfo("itemNotFound", player, ChatColor.RED);
						return true;
					}

					try {
						damage = Short.valueOf(data[1]);
					} catch (Exception e) {
						if (item == Material.WOOL && ClosestMatches.dyeColor(data[1]).size() > 0){
							damage = ClosestMatches.dyeColor(data[1]).get(0).getData();
						} else {
							LogHelper.showInfo("itemIncorrectDamage", player, ChatColor.RED);
							Commands.showCommandHelpAndUsage(player, "cex_item", alias);
							return true;
						}
					}
				} else {
					if (ClosestMatches.material(args[0]).size() > 0){
						item = ClosestMatches.material(args[0]).get(0);
					} else {
						LogHelper.showInfo("itemNotFound", player, ChatColor.RED);
						return true;
					}
				}

				if (args.length == 2){
					try {
						amount = Integer.valueOf(args[1]);
					} catch (Exception e){
						LogHelper.showInfo("itemIncorrectDamageValue", player, ChatColor.RED);
						Commands.showCommandHelpAndUsage(player, "cex_item", alias);
						return true;
					}
				}

				giveItem(sender, player, item, amount, damage);
			}
		}
		return true;
	}
}
