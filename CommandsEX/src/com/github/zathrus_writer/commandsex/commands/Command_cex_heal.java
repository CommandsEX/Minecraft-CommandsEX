package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;

public class Command_cex_heal {

	/***
	 * HEAL - fills a players health level
	 * @author iKeirNez
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args){
		
		if (!(sender instanceof Player) && args.length == 0){
			LogHelper.showWarning("playerNameMissing", sender);
			return true;
		}
		
		Player beingHealed;
		
		if ((args.length > 0) && (!(sender instanceof Player) || (Permissions.checkPerms((Player) sender, "cex.heal.others")))){
			beingHealed = Bukkit.getPlayer(args[0]);
			
			if (beingHealed == null){
				LogHelper.showWarning("invalidPlayer", sender);
				return true;
			}
		} else {
			beingHealed = (Player) sender;
		}
		
		beingHealed.setHealth(20);
		
		if (sender.getName().equalsIgnoreCase(beingHealed.getName())){
			LogHelper.showInfo("healHealed", beingHealed);
		} else {
			LogHelper.showInfo("healHealedBySomeoneElse", beingHealed);
			LogHelper.showInfo("healHealedSomeoneElse######" + "[" + beingHealed.getName(), sender);
		}
		
		return true;
	}
	
}
