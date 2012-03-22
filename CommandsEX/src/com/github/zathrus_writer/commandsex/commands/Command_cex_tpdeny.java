package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;

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
					if (Command_cex_tpa.requests.contains(id)) {
						// remove pending request
						Command_cex_tpa.requests.remove(id);
					} else if (Command_cex_tpahere.requests.contains(id)) {
						// remove pending request
						Command_cex_tpahere.requests.remove(id);
					} else {
						// no matching request found
						LogHelper.showWarning("tpRequestNotFound", sender);
						return true;
					}
					
					// send message to the requesting player (if online)
					if (tpaPlayer != null) {
						LogHelper.showWarning("tpRequestDenied1#####["+player.getName()+" #####tpRequestDenied2", tpaPlayer);
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
