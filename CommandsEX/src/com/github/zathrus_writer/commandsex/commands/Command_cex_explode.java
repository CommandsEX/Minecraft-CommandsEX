package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_explode {
	
	/***
	 * EXPLODE - kills a player with EXPLOSIONS
	 * @author iKeirNez
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
	
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (Utils.checkCommandSpam(player, "cex_explode")){
				return true;
			}
		}
		
		Player target = null;
		int explosionStrength = CommandsEX.getConf().getInt("explodeStrength");
		
		if (args.length > 2){
			Commands.showCommandHelpAndUsage(sender, "cex_explode", alias);
			return true;
		}
		
		if (args.length == 0){
			if (!(sender instanceof Player)){
				Commands.showCommandHelpAndUsage(sender, "cex_explode", alias);
				return true;
			}
			target = (Player) sender;
		}

		if (args.length >= 1){
			if (args[0].matches(CommandsEX.intRegex) && Bukkit.getPlayerExact(args[0]) == null){
				explosionStrength = Integer.valueOf(args[0]);
				target = (Player) sender;
			} else {
				target = Bukkit.getPlayer(args[0]);
				if (target == null){
					LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
					return true;
				}
			}
		}
		
		if (args.length == 2){
			if (args[1].matches(CommandsEX.intRegex)){
				explosionStrength = Integer.valueOf(args[1]);
			} else {
				LogHelper.showInfo("explodeInteger", sender, ChatColor.RED);
				return true;
			}
		}
		
		target.setHealth(0);
		target.getWorld().createExplosion(target.getLocation(), explosionStrength);
		target.setLastDamageCause(new EntityDamageEvent(target, DamageCause.ENTITY_EXPLOSION, 20));
		LogHelper.showInfo("explodePlayer#####[" + Nicknames.getNick(target.getName()), sender, ChatColor.AQUA);
		if (CommandsEX.getConf().getBoolean("showMessageOnExplode") && sender != target){
			LogHelper.showInfo("explodeRecieveExplode#####[" + Nicknames.getNick(sender.getName()), sender, ChatColor.RED);
		}
		
		return true;
	}
}