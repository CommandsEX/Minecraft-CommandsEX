package com.github.zathrus_writer.commandsex.commands;

import static com.github.zathrus_writer.commandsex.Language._;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Teleportation;
import com.github.zathrus_writer.commandsex.helpers.TpRequestCanceller;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_tpa {
	
	public static Integer tTimeout = 0;
	
	/***
	 * TPA - Asks another player for permission to teleport yourself to this player.
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player = (Player)sender;

			if (!Utils.checkCommandSpam(player, "tp-tpa")) {
				if (args.length > 0) {
					if (Permissions.checkPerms(player, "cex.tpa")) {
						// check if the requested player is online
						Player tpaPlayer = Bukkit.getServer().getPlayer(args[0]);
	
						// if player is offline...
						if (tpaPlayer == null) {
							LogHelper.showWarning("invalidPlayer", sender);
							return true;
						}
						
						// disallow TPA to ourselves
						if (player.getName().equals(tpaPlayer.getName())) {
							LogHelper.showWarning("tpCannotTeleportSelf", sender);
							return true;
						}
						
						// if another TPA or TPAHERE request is pending...
						String id = player.getName() + "#####" + tpaPlayer.getName();
						if (Teleportation.tpaRequests.contains(id) || Teleportation.tpahereRequests.contains(id) || Teleportation.tpaallRequests.contains(id)) {
							LogHelper.showWarning("tpRequestPending", sender);
							return true;
						}
						
						// load TPA timeout from config if not present
						if (tTimeout == 0) {
							Integer t = CommandsEX.getConf().getInt("tpaTimeout");
							if (t > 0) {
								tTimeout = t;
							} else {
								// fallback to default if we didn't find a valid config value
								tTimeout = 50;
							}
						}
						
						// add names of TPA players and send message
						Teleportation.tpaRequests.add(id);
						tpaPlayer.sendMessage(ChatColor.GREEN + player.getName() + " " + _("tpRequest1", sender.getName()));
						tpaPlayer.sendMessage(ChatColor.GREEN + _("tpRequest2", sender.getName()));
						tpaPlayer.sendMessage(ChatColor.GREEN + _("tpRequest3", sender.getName()));
						
						// set timeout function that will cancel TPA request if timeout is reached
						CommandsEX.plugin.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new TpRequestCanceller("tpa", id), (20 * tTimeout));
						
						// send confimation message to the original player
						player.sendMessage(ChatColor.GREEN + _("tpRequestSent", sender.getName()));
					}
				} else {
					// we need a player name as first argument or we cannot continue
					Commands.showCommandHelpAndUsage(sender, "cex_tpa", alias);
				}
			}
		}
        return true;
	}
	
	/***
	 * Cancel's a request after timeout has been reached and sends message to the appropriate user.
	 * @param id
	 */
	public static void cancelRequest(String id) {
		// check if the request still exists
		if (!Teleportation.tpaRequests.contains(id)) {
			return;
		}
		
		// remove the request from list
		Teleportation.tpaRequests.remove(id);
		
		// send message to the original requestor, if he's still online
		String[] s = id.split("#####");
		Player tpaPlayer = Bukkit.getServer().getPlayer(s[0]);
		if (tpaPlayer != null) {
			tpaPlayer.sendMessage(ChatColor.RED + _("tpRequestCancelled", tpaPlayer.getName()) + s[1] + ".");
		}
	}
}
