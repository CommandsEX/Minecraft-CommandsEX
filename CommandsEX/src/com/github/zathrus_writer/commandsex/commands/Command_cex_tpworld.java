package com.github.zathrus_writer.commandsex.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.ClosestMatches;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;
import com.github.zathrus_writer.commandsex.helpers.Spawning;
import com.github.zathrus_writer.commandsex.helpers.Teleportation;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_tpworld {

	/**
	 * TPWORLD - Teleports a player to a worlds spawn point
	 * @author iKeirNez
	 * @param sender
	 * @param alias
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args){
		if (sender instanceof Player && Utils.checkCommandSpam((Player) sender, "cex_tpworld")){
			return true;
		}
		
		if (args.length < 1 || args.length > 2){
			Commands.showCommandHelpAndUsage(sender, "cex_tpworld", alias);
			return true;
		}
		
		Player target;
		
		if (args.length == 2){
			target = Bukkit.getPlayer(args[1]);
			
			if (target != sender && !sender.hasPermission("cex.tpworld.others")){
				LogHelper.showWarning("tpWorldOthersNoPerm", sender);
				return true;
			}
			
			if (target == null){
				LogHelper.showWarning("invalidPlayer", sender);
			}
		} else {
			if (sender instanceof Player){
				target = (Player) sender;
			} else {
				Commands.showCommandHelpAndUsage(sender, "cex_tpworld", alias);
				return true;
			}
		}
		
		List<World> matches = ClosestMatches.intellWorld(args[0], target.getWorld());
		
		if (matches.size() < 1){
			LogHelper.showWarning("invalidWorld", sender);
			return true;
		}
		
		World world = matches.get(0);
		
		// teleport
		Teleportation.delayedTeleport(target, Spawning.getWorldSpawn(world));
		
		if (sender == target){
			LogHelper.showInfo("tpWorldSelf#####[" + world.getName(), sender);
		} else {
			LogHelper.showInfo("tpLocSuccess#####[" + Nicknames.getNick(target.getName()) + " #####to#####[" + world.getName(), sender);
			LogHelper.showInfo("[" + Nicknames.getNick(sender.getName()) + " #####tpWorldOtherNotify#####[" + world.getName(), target);
		}
		
		return true;
	}
	
}
