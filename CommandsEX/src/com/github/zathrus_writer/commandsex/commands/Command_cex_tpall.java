package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Teleportation;

public class Command_cex_tpall extends Teleportation {

	/***
	 * TPA - Asks another player for permission to teleport yourself to this player.
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args){
		
		if (PlayerHelper.checkIsPlayer(sender)){
			Player player = (Player) sender;
			if (args.length == 0){
				for (Player player2 : Bukkit.getOnlinePlayers()){
					if (player != player2){
						String[] newargs = {player.getName(), player2.getName()};
						tp_common(sender, newargs, "tpto", alias);
						//LogHelper.showInfo("tpAll" + "#####[" + player.getName(), player2, ChatColor.GREEN);
						LogHelper.showInfo("[" + player.getName() + " " + "#####tpAll", player2, ChatColor.GREEN);
					}
				}
				
				LogHelper.showInfo("tpAllSuccess", player, ChatColor.GREEN);
			}
		}
		
		return true;
	}
	
}
