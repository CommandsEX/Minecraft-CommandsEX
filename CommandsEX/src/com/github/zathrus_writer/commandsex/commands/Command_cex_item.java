package com.github.zathrus_writer.commandsex.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
			
				if (args.length == 0 || args.length > 2){
					Commands.showCommandHelpAndUsage(player, "cex_item", alias);
				} else {
					String item;
					short damage = 0;
					int amount = 64;
					
					if (args[0].contains(":")){
						String[] data = args[0].split(":");
						item = data[0];

						try {
							damage = Short.valueOf(args[0].split(":")[1]);
						} catch (Exception e) {
							LogHelper.showInfo("itemIncorrectDamageValue", player, ChatColor.RED);
							Commands.showCommandHelpAndUsage(player, "cex_item", alias);
							return true;
						}
					} else {
						item = args[0];
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
				
					if (Utils.closestMatches(item).size() > 0){
						List<Material> matches = Utils.closestMatches(item);
						giveItem(sender, player, matches.get(0), amount, damage);
					} else {
						LogHelper.showInfo("itemNotFound", player, ChatColor.RED);
					}
				}
				//player.performCommand("give " + player.getName() + " " + Utils.implode(args, " "));
			}
        return true;
	}
}
