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

public class Command_cex_tpdeny {
	
	/***
	 * TPDENY - Rejects /tpa or /tpahere request for a specified player.
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player = (Player)sender;

			if (args.length > 0) {
				if (Permissions.checkPerms(player, "cex.tpdeny")) {
					// get player object (if online)
					Player tpaPlayer = Bukkit.getServer().getPlayer(args[0]);
					
					// check if there is a TPA or TPAHERE request for the given players combination
					String id = args[0] + "#####" + player.getName();
					if (Teleportation.tpaallRequests.contains(id)){
						Teleportation.tpaallRequests.remove(id);
					} else if (Teleportation.tpaRequests.contains(id)) {
						// remove pending request
						Teleportation.tpaRequests.remove(id);
					} else if (Teleportation.tpahereRequests.contains(id)) {
						// remove pending request
						Teleportation.tpahereRequests.remove(id);
					} else {
						// no matching request found
						LogHelper.showWarning("tpRequestNotFound", sender);
						return true;
					}
					
					// send message to the requesting player (if online) and is not invisible
					if (tpaPlayer != null && !Common.invisiblePlayers.contains(sender.getName())) {
						LogHelper.showWarning("tpRequestDenied1#####["+ Nicknames.getNick(player.getName()) +" #####tpRequestDenied2", tpaPlayer);
					}
					
					// send confirmation message to the denier
					LogHelper.showInfo("tpRequestDeniedConfirm", player);
				}
			} else {
				// we need a player name as first argument or we cannot continue
				Commands.showCommandHelpAndUsage(sender, "cex_tpdeny", alias);
			}
		}
        return true;
	}
}
