package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.Common;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Teleportation;

public class Command_cex_tpaccept {
	
	/***
	 * TPACCEPT - Accepts /tpa or /tpahere request for a specified player.
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player = (Player)sender;

			if (args.length > 0) {
				if (Permissions.checkPerms(player, "cex.tpaccept")) {
					// check if the requested player is online
					Player tpaPlayer = Bukkit.getServer().getPlayer(args[0]);

					// if player is offline...
					if (tpaPlayer == null) {
						LogHelper.showWarning("invalidPlayer", sender);
						return true;
					}
					
					// check if there is a TPA or TPAHERE request for the given players combination
					String id = tpaPlayer.getName() + "#####" + player.getName();
					if (Teleportation.tpaallRequests.contains(id)){
						// remove pending request
						Teleportation.tpaallRequests.remove(id);
						// teleport the player to us
						Teleportation.delayedTeleport(player, tpaPlayer.getLocation());
						LogHelper.showInfo("[" + Nicknames.getNick(tpaPlayer.getName()) + "#####tpAcceptNotify", sender);
						// catch if Common does not exist
						try {
							if (!Common.invisiblePlayers.contains(sender.getName())){
								LogHelper.showInfo("tpAccept#####[" + Nicknames.getNick(sender.getName()), tpaPlayer);
							}
						} catch (Exception e){
							LogHelper.showInfo("tpAccept#####[" + Nicknames.getNick(sender.getName()), tpaPlayer);
						}
					} else if (Teleportation.tpaRequests.contains(id)) {
						// remove pending request
						Teleportation.tpaRequests.remove(id);
						// teleport us to the given player
						Teleportation.delayedTeleport(tpaPlayer, player.getLocation());
						LogHelper.showInfo("[" + Nicknames.getNick(tpaPlayer.getName()) + "#####tpAcceptNotify", sender);
						// catch if Common does not exist
						try {
							if (!Common.invisiblePlayers.contains(sender.getName())){
								LogHelper.showInfo("tpAccept#####[" + Nicknames.getNick(sender.getName()), tpaPlayer);
							}
						} catch (Exception e){
							LogHelper.showInfo("tpAccept#####[" + Nicknames.getNick(sender.getName()), tpaPlayer);
						}
					} else if (Teleportation.tpahereRequests.contains(id)) {
						// remove pending request
						Teleportation.tpahereRequests.remove(id);
						// teleport the player to us
						Teleportation.delayedTeleport(player, tpaPlayer.getLocation());
						LogHelper.showInfo("[" + Nicknames.getNick(tpaPlayer.getName()) + "#####tpAcceptNotify", sender);
						// catch if Common does not exist
						try {
							if (!Common.invisiblePlayers.contains(sender.getName())){
								LogHelper.showInfo("tpAccept#####[" + Nicknames.getNick(sender.getName()), tpaPlayer);
							}
						} catch (Exception e){
							LogHelper.showInfo("tpAccept#####[" + Nicknames.getNick(sender.getName()), tpaPlayer);
						}
					} else {
						// no matching request found
						LogHelper.showWarning("tpRequestNotFound", sender);
						return true;
					}
				}
			} else {
				// we need a player name as first argument or we cannot continue
				Commands.showCommandHelpAndUsage(sender, "cex_tpaccept", alias);
			}
		}
        return true;
	}
}
