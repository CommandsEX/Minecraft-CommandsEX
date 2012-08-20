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
			if (sender instanceof Player && (args[0].equalsIgnoreCase("nether") || args[0].equalsIgnoreCase("end")
					|| args[0].equalsIgnoreCase("theend") || args[0].equalsIgnoreCase("the_end")
					|| args[0].equalsIgnoreCase("overworld"))){
				World cWorld = ((Player) sender).getWorld();
				String worldName = cWorld.getName();
				if (worldName.contains("_")){
					worldName = worldName.split("_")[0];
				}
				
				if (args[0].equalsIgnoreCase("overworld")){
					World nWorld = Bukkit.getWorld(worldName);
					if (nWorld != null){
						world = nWorld;
					} else {
						LogHelper.showInfo("seedOverworldNotExist", sender, ChatColor.RED);
						return true;
					}
				}
				
				if (args[0].equalsIgnoreCase("nether")){
					World nWorld = Bukkit.getWorld(worldName + "_nether");
					if (nWorld != null){
						world = nWorld;
					} else {
						LogHelper.showInfo("seedNetherNotExist", sender, ChatColor.RED);
						return true;
					}
				}
				
				if (args[0].contains("end")){
					World nWorld = Bukkit.getWorld(worldName + "_the_end");
					if (nWorld != null){
						world = nWorld;
					} else {
						LogHelper.showInfo("seedEndNotExist", sender, ChatColor.RED);
						return true;
					}
				}
			} else {
				List<World> matches = ClosestMatches.world(args[0]);

				if (matches.size() == 0){
					List<String> worldNames = new ArrayList<String>();
					for (World w : Bukkit.getWorlds()){
						worldNames.add(w.getName());
					}
					LogHelper.showInfo("seedInvalidWorld", sender, ChatColor.RED);
					LogHelper.showInfo("seedInvalidWorldList#####[" + ChatColor.GOLD
							+ Utils.implode(worldNames, ChatColor.AQUA + ", " + ChatColor.GOLD), sender);
					return true;
				}

				world = ClosestMatches.world(args[0]).get(0);
			}
		}

		LogHelper.showInfo("seedShow#####[" + ChatColor.GOLD + world.getName() + ChatColor.AQUA + " #####is#####["
				+ ChatColor.GOLD + world.getSeed(), sender);
		return true;
	}

}
