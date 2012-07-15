package com.github.zathrus_writer.commandsex.commands;


import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Common;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_online {
	
	/***
	 * ONLINE - displays list of all players that are currently online
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		Boolean hasPerms = true;
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (Utils.checkCommandSpam(player, "cex_online")){
				return true;
			}
			hasPerms = Permissions.checkPerms(player, "cex.online");
		}

		// permissions ok, list players
		if (hasPerms) {
			if (args.length == 0) {
				
				ArrayList<String> players = new ArrayList<String>();
				for (Player player : Bukkit.getOnlinePlayers()){
					// Determine whether or not to show the player
					boolean addPlayer;
					if (Common.invisiblePlayers.contains(player.getName())){
						if (sender.hasPermission("cex.online.hidden")){
							addPlayer = true;
						} else if (sender.getName().equalsIgnoreCase(player.getName())){
							addPlayer = true;
						} else {
							addPlayer = false;
						}
					} else {
						addPlayer = true;
					}
					
					// Using the method above, choose whether or not to add the player
					if (addPlayer){
						players.add((Common.invisiblePlayers.contains(player.getName()) ? "[" + ChatColor.AQUA + "#####onlinePlayerHidden#####[" + ChatColor.YELLOW : "#####[") + player.getName());
					}
				}
				
				LogHelper.showInfo("onlinePlayers#####[(" + players.size() + "/"  + Bukkit.getServer().getMaxPlayers() + "): " + ChatColor.YELLOW + "#####" + Utils.implode(players, ChatColor.AQUA + ", " + ChatColor.YELLOW), sender);
			} else {
				// check if a given player is online
				Player p = Bukkit.getServer().getPlayer(args[0]);
				if (p == null) {
					LogHelper.showInfo("[" + args[0] + " #####isOffline", sender);
				} else {
					LogHelper.showInfo("[" + p.getName() + " #####isOnline", sender);
				}
			}
		}

        return true;
	}
}
