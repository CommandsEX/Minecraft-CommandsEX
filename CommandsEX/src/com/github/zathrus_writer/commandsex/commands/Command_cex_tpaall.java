package com.github.zathrus_writer.commandsex.commands;

import static com.github.zathrus_writer.commandsex.Language._;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.TpRequestCanceller;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_tpaall {

	public static List<String> requests = new ArrayList<String>();
	public static Integer tTimeout = 0;
	
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
		
		for (Player player : Bukkit.getOnlinePlayers()){
			String id = player.getName() + "#####" + player.getName();
			requests.add(id);
			// set timeout function that will cancel TPA request if timeout is reached
			player.sendMessage(ChatColor.GREEN + player.getName() + " " + _("tpRequest1", sender.getName()));
			player.sendMessage(ChatColor.GREEN + _("tpRequest2", sender.getName()));
			player.sendMessage(ChatColor.GREEN + _("tpRequest3", sender.getName()));
			sent++;
		}
		
		if (!requests.isEmpty()){
			CommandsEX.plugin.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new TpRequestCanceller("tpaall", requests), (20 * tTimeout));
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
		
		LogHelper.showInfo("[" + sent + "#####tpaAllRequestsSent", sender, ChatColor.AQUA);
		
		return true;
	}
	
	/***
	 * Cancel's a request after timeout has been reached and sends message to the appropriate user.
	 * @param id
	 */
	public static void cancelRequests(List<String> toCancel) {
		
		for (String st : toCancel){
			if (requests.contains(st)){
				requests.remove(st);
				System.out.println("Removed " + st);
			}
		}
	}
}
