package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_heal {

	/***
	 * HEAL - fills a players health level
	 * @author iKeirNez
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args){
	
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (Utils.checkCommandSpam(player, "cex_heal")){
				return true;
			}
		}
		
		if (args.length == 0){
			if (!(sender instanceof Player)){
				Commands.showCommandHelpAndUsage(sender, "cex_heal", alias);
				return true;
			}
			
			Player player = (Player) sender;
			player.setHealth(20);
			LogHelper.showInfo("healHealed", sender, ChatColor.AQUA);
		} else if (args.length == 1){
			Player beingHealed = Bukkit.getPlayer(args[0]);
			
			if (beingHealed == null){
				LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
				return true;
			}
			
			if (sender.getName().equalsIgnoreCase(beingHealed.getName())){
				beingHealed.setHealth(20);
				LogHelper.showInfo("healHealed", sender, ChatColor.AQUA);
			} else if ((!(sender instanceof Player)) || (((Player) sender).hasPermission("cex.heal.others"))){
				beingHealed.setHealth(20);
				LogHelper.showInfo("healHealedBySomeoneElse#####[" + Nicknames.getNick(sender.getName()), beingHealed, ChatColor.AQUA);
				LogHelper.showInfo("healHealedSomeoneElse#####[" + Nicknames.getNick(beingHealed.getName()), sender, ChatColor.AQUA);
			} else {
				LogHelper.showInfo("healOthersNoPerm", sender, ChatColor.RED);
				return true;
			}
		} else {
			Commands.showCommandHelpAndUsage(sender, "cex_heal", alias);
			return true;
		}
		return true;
	}
}
