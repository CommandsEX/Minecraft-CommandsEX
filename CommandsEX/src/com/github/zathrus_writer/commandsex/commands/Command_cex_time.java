package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.ClosestMatches;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Time;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_time extends Time {
	/***
	 * TIME - changes time in the current world
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		World world;

		// do we have any parameters?
		if (args.length == 0 || args.length == 1 || args[0].equalsIgnoreCase("view")){
			if (args.length == 0){
				if (sender instanceof Player){
					world = ((Player) sender).getWorld();
				} else {
					world = Bukkit.getWorlds().get(0);
				}
			} else {
				world = ClosestMatches.intellWorld(args[0], (sender instanceof Player ? ((Player) sender).getWorld() : Bukkit.getWorlds().get(0))).get(0);
			}
			
			LogHelper.showInfo("timeCurrentTime1#####[" + Utils.parseTime(world.getTime()) + " #####timeOr#####[" + world.getTime() + " #####timeTicks", sender, ChatColor.AQUA);
		} else if (args.length > 1) {
			if (sender.hasPermission("cex.time.set")) {
				setTime(sender, args);
			} else {
				LogHelper.showInfo("timeSetNoPerm", sender, ChatColor.RED);
			}
		} else {
			// show usage
			Commands.showCommandHelpAndUsage(sender, "cex_time", alias);
		}

		return true;
	}
}
