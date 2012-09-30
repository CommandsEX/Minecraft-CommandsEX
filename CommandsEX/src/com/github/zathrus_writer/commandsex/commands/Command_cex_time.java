package com.github.zathrus_writer.commandsex.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.ClosestMatches;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_time {
	/***
	 * TIME - changes time in a world
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (sender instanceof Player && Utils.checkCommandSpam((Player) sender, "cex_time")){
			return true;
		}
		
		String function = "";
		World cWorld = (sender instanceof Player ? ((Player) sender).getWorld() : Bukkit.getWorlds().get(0));
		World w = null;
		long move = 0L;
		
		if (args.length == 0){
			Commands.showCommandHelpAndUsage(sender, "cex_time", alias);
			return true;
		}
		
		if (args.length > 0){
			function = args[0];
			
			if (function.equalsIgnoreCase("view")){
				if (args.length == 2){
					List<World> matches = ClosestMatches.intellWorld(args[1], cWorld);
					if (matches.size() == 0){
						LogHelper.showWarning("invalidWorld", sender);
						return true;
					}
					
					w = matches.get(0);
				} else if (args.length == 1){
					w = cWorld;
				} else {
					Commands.showCommandHelpAndUsage(sender, "cex_time", alias);
					return true;
				}
			} else {
				if (args.length == 3){
					List<World> matches = ClosestMatches.intellWorld(args[2], cWorld);
					if (matches.size() == 0){
						LogHelper.showWarning("invalidWorld", sender);
						return true;
					}
					
					w = matches.get(0);
				} else if (args.length == 2){
					w = cWorld;
				} else {
					Commands.showCommandHelpAndUsage(sender, "cex_time", alias);
					return true;
				}
			}
		}
		
		if (w == null){
			LogHelper.showWarning("invalidWorld", sender);
			return true;
		}
		
		if (function.equalsIgnoreCase("view")){
			LogHelper.showInfo("timeCurrentTime1#####[" + Utils.parseTime(w.getTime()) + " #####timeCurrentTime2#####[" + w.getName(), sender);
			return true;
		} else if (function.equalsIgnoreCase("add")){
			if (args.length == 2 || args.length == 3){
				if (!args[1].matches(CommandsEX.intRegex)){
					LogHelper.showWarning("timeAddOnlyWorksWithNumbers", sender);
					return true;
				}
				
				move = Long.parseLong(args[1]);
			}
			
			w.setTime(w.getTime() + move);
		} else if (function.equalsIgnoreCase("set")){
			if (args[1].equalsIgnoreCase("dawn") || args[1].equalsIgnoreCase("day")){
				move = 0L;
			} else if (args[1].equalsIgnoreCase("morning")){
				move = 2500L;
			} else if (args[1].equalsIgnoreCase("noon")){
				move = 6000L;
			} else if (args[1].equalsIgnoreCase("evening")){
				move = 11000L;
			} else if (args[1].equalsIgnoreCase("night")){
				move = 15000L;
			} else if (args[1].matches(CommandsEX.intRegex)){
				move = Long.parseLong(args[1]);
			} else {
				LogHelper.showWarning("timeInvalidStringValue", sender);
				return true;
			}

			w.setTime(move);
		} else {
			Commands.showCommandHelpAndUsage(sender, "cex_time", alias);
			return true;
		}

		// tell the player and anyone with permission to see what happened
		LogHelper.showInfo("timeChanged#####[" + Utils.parseTime(w.getTime()) + " #####timeOr#####[" + w.getTime() + " #####timeTicks" + (!cWorld.equals(w) ? "#####in#####[ " + w.getName() : ""), sender);
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!player.equals(sender) && Permissions.checkPermEx(player, "cex.time.notify")) {
				LogHelper.showInfo("timeChangedBy1#####[" + w.getName() + " #####timeChangedBy2#####[" + Nicknames.getNick(sender.getName()), player);
			}
		}
		return true;
	}
}
