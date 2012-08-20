package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.Common;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_fly extends Common {

	/***
	 * Fly - Toggles fly mode for a player, without the need for creative mode.
	 * @author iKeirNez
	 * @param sender
	 * @param alias
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args){
		if (sender instanceof Player){
			if (Utils.checkCommandSpam(((Player) sender), "cex_fly")){
				return true;
			}
		}
		
		if (args.length == 0){
			if (!(sender instanceof Player)){
				Commands.showCommandHelpAndUsage(sender, "cex_fly", alias);
				return true;
			}
			
			Player player = (Player) sender;
			setFlyMode(player);
			LogHelper.showInfo("flyOwnSuccess", sender, ChatColor.AQUA);
		} else if (args.length == 1){
			Player target = Bukkit.getPlayer(args[0]);
			if (target != sender){
				if ( ((!(sender instanceof Player)) || ((Player) sender).hasPermission("cex.fly.others"))){
					if (target == null){
						LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
						return true;
					}
					
					setFlyMode(target);
					LogHelper.showInfo("flyMsgToTarget#####[" + Nicknames.getNick(sender.getName()), target, ChatColor.AQUA);
					LogHelper.showInfo("flyOtherSuccess#####[" + Nicknames.getNick(target.getName()), sender, ChatColor.AQUA);
				} else {
					LogHelper.showInfo("flyOtherNoPerm", sender, ChatColor.RED);
				}
			} else {
				setFlyMode(target);
				LogHelper.showInfo("flyOwnSuccess", sender, ChatColor.AQUA);
			}
		} else {
			Commands.showCommandHelpAndUsage(sender, "cex_fly", alias);
		}
		return true;
	}
	
}
