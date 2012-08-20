package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Teleportation;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_tpall extends Teleportation {

	/***
	 * TPALL - Teleports all players on the server to the given player.
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args){

		if (PlayerHelper.checkIsPlayer(sender)){
			Player player = (Player) sender;

			if (Utils.checkCommandSpam(player, "cex_tpall")){
				return true;
			}

			Player tpTo = null;

			if (args.length == 0){
				tpTo = player;
			} else if (args.length == 1){
				tpTo = Bukkit.getPlayer(args[0]);
			}

			if (args.length <= 1){
				if (tpTo != null){
					for (Player player2 : Bukkit.getOnlinePlayers()){
						if (player2 != tpTo){
							String[] newargs = {tpTo.getName(), player2.getName()};
							tp_common(sender, newargs, "tpto", alias);
							LogHelper.showInfo("tpTeleport#####[ " + Nicknames.getNick(player.getName()), player2, ChatColor.GREEN);
						}
					}
					LogHelper.showInfo("tpAllSuccess", player, ChatColor.GREEN);
				} else {
					LogHelper.showInfo("invalidPlayer", player, ChatColor.RED);
				}
			}
		}
		
		return true;
	}
	
}
