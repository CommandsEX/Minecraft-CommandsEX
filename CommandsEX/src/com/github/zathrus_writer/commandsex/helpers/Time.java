package com.github.zathrus_writer.commandsex.helpers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;

public class Time {

	/***
	 * TIME SET/ADD - sets a time in the world
	 * @param sender
	 * @param args
	 * @return
	 */
	public static void setTime(CommandSender sender, String[] args) {
		// we only support SET and ADD
		if (!args[0].equalsIgnoreCase("set") && !args[0].equalsIgnoreCase("add")) {
			LogHelper.showWarning("timeInvalidFirstArgument", sender);
			return;
		}
		
		// for ADD, the value must be numeric
		if (args[0].equalsIgnoreCase("add") && ((args.length < 2) || !args[1].matches(CommandsEX.intRegex))) {
			LogHelper.showWarning("timeAddOnlyWorksWithNumbers", sender);
			return;
		}
		
		// convert non-numeric second parameter to a numeric one
		Long moveBy = 0L;
		if (args[1].matches(CommandsEX.intRegex)) {
			moveBy = Long.parseLong(args[1]);

			// check if we are within allowed constrains
			if (moveBy < 0) {
				LogHelper.showWarning("timeCannotBeNegative", sender);
				return;
			}
			
			if (args[0].equalsIgnoreCase("set") && (moveBy > 24000)) {
				LogHelper.showWarning("timeOutOfBounds", sender);
				return;
			}
		} else {
			if (args[1].equalsIgnoreCase("dawn") || args[1].equalsIgnoreCase("day")){
				moveBy = 0L;
			} else if (args[1].equalsIgnoreCase("morning")) {
				moveBy = 2500L;
			} else if (args[1].equalsIgnoreCase("noon")) {
				moveBy = 6000L;
			} else if (args[1].equalsIgnoreCase("evening")) {
				moveBy = 11000L;
			} else if (args[1].equalsIgnoreCase("night")) {
				moveBy = 15000L;
			} else {
				LogHelper.showWarning("timeInvalidStringValue", sender);
				return;
			}
		}
		
		// now move the time :)
		Player p = (Player) sender;
		String pName = p.getName();
		String worldName = p.getWorld().getName();
		
		if (args[0].equalsIgnoreCase("set")) {
			p.getWorld().setTime(moveBy);
		} else {
			p.getWorld().setTime(p.getWorld().getTime() + moveBy);
		}
		
		// tell the player and anyone with permission to see what happened
		LogHelper.showInfo("timeChanged#####[" + Utils.parseTime(p.getWorld().getTime()) + " #####timeOr#####[" + p.getWorld().getTime() + " #####timeTicks", sender);
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!player.equals(p) && Permissions.checkPermEx(player, "cex.time.notify")) {
				LogHelper.showInfo("timeChangedBy1#####[" + worldName + " #####timeChangedBy2#####[" + Nicknames.getNick(pName), player);
			}
		}
	}

	/***
	 * Function to set a players time
	 * @author iKeirNez
	 * @param sender
	 * @param args
	 */
	
	public static void setPlayerTime(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		// Reset the player time to server time
		if (args[0].equalsIgnoreCase("reset")){
			p.resetPlayerTime();
			LogHelper.showInfo("playerTimeReset", sender, ChatColor.AQUA);
			return;
		} 
		
		// we only support SET and ADD
		if (!args[0].equalsIgnoreCase("set") && !args[0].equalsIgnoreCase("add")) {
			LogHelper.showWarning("timeInvalidFirstArgument", sender);
			return;
		}
		
		// for ADD, the value must be numeric
		if (args[0].equalsIgnoreCase("add") && ((args.length < 2) || !args[1].matches(CommandsEX.intRegex))) {
			LogHelper.showWarning("timeAddOnlyWorksWithNumbers", sender);
			return;
		}
		
		// convert non-numeric second parameter to a numeric one
		Long moveBy = 0L;
		if (args[1].matches(CommandsEX.intRegex)) {
			moveBy = Long.parseLong(args[1]);

			// check if we are within allowed constrains
			if (moveBy < 0) {
				LogHelper.showWarning("timeCannotBeNegative", sender);
				return;
			}
			
			if (args[0].equalsIgnoreCase("set") && (moveBy > 24000)) {
				LogHelper.showWarning("timeOutOfBounds", sender);
				return;
			}
		} else {
			if (args[1].equalsIgnoreCase("dawn") || args[1].equalsIgnoreCase("day")){
				moveBy = 0L;
			} else if (args[1].equalsIgnoreCase("morning")) {
				moveBy = 2500L;
			} else if (args[1].equalsIgnoreCase("noon")) {
				moveBy = 6000L;
			} else if (args[1].equalsIgnoreCase("evening")) {
				moveBy = 11000L;
			} else if (args[1].equalsIgnoreCase("night")) {
				moveBy = 15000L;
			} else {
				LogHelper.showWarning("timeInvalidStringValue", sender);
				return;
			}
		}
		
		if (args[0].equalsIgnoreCase("set")) {
			p.setPlayerTime(moveBy, false);
		} else {
			p.setPlayerTime(p.getPlayerTime() + moveBy, false);
		}
		
		// tell the player and anyone with permission to see what happened
		LogHelper.showInfo("playerTimeChanged#####[" + Utils.parseTime(p.getPlayerTime()) + " #####timeOr#####[" + p.getPlayerTime() + " #####timeTicks", sender);
	}
}
