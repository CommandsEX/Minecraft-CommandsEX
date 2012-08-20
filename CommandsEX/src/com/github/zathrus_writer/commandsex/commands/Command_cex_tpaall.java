package com.github.zathrus_writer.commandsex.commands;

import static com.github.zathrus_writer.commandsex.Language._;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;
import com.github.zathrus_writer.commandsex.helpers.Teleportation;
import com.github.zathrus_writer.commandsex.helpers.TpRequestCanceller;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_tpaall {

	public static Integer tTimeout = 0;
	
	/***
	 * TpaAll, sends a request to all online players to teleport to the target/sender
	 * @author iKeirNez
	 * @param sender
	 * @param alias
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args){
		
		if (sender instanceof Player && Utils.checkCommandSpam((Player) sender, "cex_tpaall")){
			return true;
		}
		
		if (args.length > 1){
			Commands.showCommandHelpAndUsage(sender, "cex_tpaall", alias);
			return true;
		}
		
		Player to = null;
		int sent = 0;
		
		if (args.length == 0){
			if (!(sender instanceof Player)){
				Commands.showCommandHelpAndUsage(sender, "cex_tpaall", alias);
				return true;
			} else {
				to = (Player) sender;
			}
		} else {
			to = Bukkit.getPlayer(args[0]);
			if (to == null){
				LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
				return true;
			}
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

		for (Player player : Bukkit.getOnlinePlayers()){
			if (player != sender){
				String id = to.getName() + "#####" + player.getName();
				Teleportation.tpaallRequests.add(id);
				// set timeout function that will cancel TPA request if timeout is reached
				player.sendMessage(ChatColor.GREEN + Nicknames.getNick(to.getName()) + " " + _("tpRequest1", sender.getName()));
				player.sendMessage(ChatColor.GREEN + _("tpRequest2", sender.getName()));
				player.sendMessage(ChatColor.GREEN + _("tpRequest3", sender.getName()));
				sent++;
				CommandsEX.plugin.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new TpRequestCanceller("tpaall", id), (20 * tTimeout));
			}
		}
		
		LogHelper.showInfo("[" + sent + " #####tpaAllRequestsSent", sender, ChatColor.AQUA);
		
		return true;
	}
	
	/***
	 * Cancel's a request after timeout has been reached and sends message to the appropriate user.
	 * @param id
	 */
	public static void cancelRequest(String id) {
		// check if the request still exists
		if (!Teleportation.tpaallRequests.contains(id)) {
			return;
		}
		
		// remove the request from list
		Teleportation.tpaallRequests.remove(id);
	}
}
