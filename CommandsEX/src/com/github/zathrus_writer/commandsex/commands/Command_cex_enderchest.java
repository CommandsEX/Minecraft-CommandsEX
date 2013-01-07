package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_enderchest {

	/***
	 * Ender Chest - Opens a players Ender Chest
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
		
		if (Utils.checkCommandSpam(player, "cex_enderchest")){
			return true;
		}
		
		if (args.length > 1){
			Commands.showCommandHelpAndUsage(sender, "cex_enderchest", alias);
			return true;
		}
		
		Player target;
		if (args.length == 0){
			target = player;
		} else {
			target = Bukkit.getPlayer(args[0]);
			
			if (target == null){
				target = Bukkit.getOfflinePlayer(args[0]).getPlayer();
				if (target == null){
					LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
					return true;
				}
			}
		}
		
		if (player.equals(target)){
			LogHelper.showInfo("enderChestOpenSelf", sender);
		} else {
			if (player.hasPermission("cex.enderchest.others")){
				LogHelper.showInfo("enderChestOpen#####[" + Nicknames.getNick(target.getName()), sender);
			} else {
				LogHelper.showInfo("enderChestOtherNoPerm", sender, ChatColor.RED);
				return true;
			}
		}
		
		player.openInventory(target.getEnderChest());
		
		return true;
	}
	
}
