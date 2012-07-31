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

public class Command_cex_tpahere {
	
	public static Integer tTimeout = 0;
	
	/***
	 * TPAHERE - Requests another player to teleport to you.
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player = (Player)sender;

			if (!Utils.checkCommandSpam(player, "tp-tpahere")) {
				if (args.length > 0) {
					if (Permissions.checkPerms(player, "cex.tpahere")) {
						// check if the requested player is online
						Player tpaPlayer = Bukkit.getServer().getPlayer(args[0]);
	
						// if player is offline...
						if (tpaPlayer == null) {
							LogHelper.showWarning("invalidPlayer", sender);
							return true;
						}
						
						// disallow TPAHERE to ourselves
						if (player.getName().equals(tpaPlayer.getName())) {
							LogHelper.showWarning("tpCannotTeleportSelf", sender);
							return true;
						}
						
						// if another TPA or TPAHERE request is pending...
						String id = player.getName() + "#####" + tpaPlayer.getName();
						if (Teleportation.tpahereRequests.contains(id) || Teleportation.tpaRequests.contains(id) || Teleportation.tpaallRequests.contains(id)) {
							LogHelper.showWarning("tpRequestPending", sender);
							return true;
						}
						
						// load TPAHERE timeout from config if not present
						if (tTimeout == 0) {
							Integer t = CommandsEX.getConf().getInt("tpahereTimeout");
							if (t > 0) {
								tTimeout = t;
							} else {
								// fallback to default if we didn't find a valid config value
								tTimeout = 50;
							}
						}
						
						// add names of TPAHERE players and send message
						Teleportation.tpahereRequests.add(id);
						tpaPlayer.sendMessage(ChatColor.GREEN + player.getName() + " " + _("tpRequest1a", sender.getName()));
						tpaPlayer.sendMessage(ChatColor.GREEN + _("tpRequest2", sender.getName()));
						tpaPlayer.sendMessage(ChatColor.GREEN + _("tpRequest3", sender.getName()));
						
						// set timeout function that will cancel TPAHERE request if timeout is reached
						CommandsEX.plugin.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new TpRequestCanceller("tpahere", id), (20 * tTimeout));
						
						// send confimation message to the original player
						player.sendMessage(ChatColor.GREEN + _("tpRequestSent", sender.getName()));
					}
				} else {
					// we need a player name as first argument or we cannot continue
					Commands.showCommandHelpAndUsage(sender, "cex_tpahere", alias);
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
		if (!Teleportation.tpahereRequests.contains(id)) {
			return;
		}

		// remove the request from list
		Teleportation.tpahereRequests.remove(id);
		
		// send message to the original requestor, if he's still online
		String[] s = id.split("#####");
		Player tpaPlayer = Bukkit.getServer().getPlayer(s[0]);
		if (tpaPlayer != null) {
			tpaPlayer.sendMessage(ChatColor.RED + _("tpRequestCancelled", tpaPlayer.getName()) + s[1] + ".");
		}
	}
}
