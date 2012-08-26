package com.github.zathrus_writer.commandsex.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.ClosestMatches;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_seed {

	/***
	 * Seed - shows the seed of any world, even from the console
	 * @param sender
	 * @param alias
	 * @param args
	 * @return
	 */

	public static Boolean run(CommandSender sender, String alias, String[] args){
		if (sender instanceof Player && Utils.checkCommandSpam((Player) sender, "cex_seed")){
			return true;
		}
		
		World world = null;

		if (args.length > 1){
			Commands.showCommandHelpAndUsage(sender, "cex_seed", alias);
			return true;
		}

		if (args.length == 0){
			if (sender instanceof Player){
				Player player = (Player) sender;
				world = player.getWorld();
			} else {
				Commands.showCommandHelpAndUsage(sender, "cex_seed", alias);
				return true;
			}
		} else {
			if (sender instanceof Player){
				List<World> matches = ClosestMatches.intellWorld(args[0], ((Player) sender).getWorld());
				if (matches.size() > 0){
					world = matches.get(0);
				}
			} else {
				Commands.showCommandHelpAndUsage(sender, "cex_seed", alias);
				return true;
			}
		}

		if (world == null){
			List<String> worldNames = new ArrayList<String>();
			for (World w : Bukkit.getWorlds()){
				worldNames.add(w.getName());
			}
			LogHelper.showInfo("seedInvalidWorld", sender, ChatColor.RED);
			LogHelper.showInfo("seedInvalidWorldList#####[" + ChatColor.GOLD
					+ Utils.implode(worldNames, ChatColor.AQUA + ", " + ChatColor.GOLD), sender);
			return true;
		}

		LogHelper.showInfo("seedShow#####[" + ChatColor.GOLD + world.getName() + ChatColor.AQUA + " #####is#####["
				+ ChatColor.GOLD + world.getSeed(), sender);
		return true;
	}

}
