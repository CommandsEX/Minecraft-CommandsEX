package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_blind {


	public static Boolean run(CommandSender sender, String alias, String[] args){
		
		if (sender instanceof Player && Utils.checkCommandSpam((Player) sender, "cex_blind")){
			return true;
		}
		
		if (args.length > 2){
			Commands.showCommandHelpAndUsage(sender, "cex_blind", alias);
		}
		
		Player target = null;
		int time = 500;
		
		if (args.length == 0){
			if (sender instanceof Player){
				target = (Player) sender;
			} else {
				Commands.showCommandHelpAndUsage(sender, "cex_blind", alias);
				return true;
			}
		}
		
		if (args.length == 1){
			if (args[0].matches(CommandsEX.intRegex)){
				if (Bukkit.getPlayerExact(args[0]) != null){
					target = Bukkit.getPlayerExact(args[0]);
				} else {
					time = Integer.valueOf(args[0]) * 20;
				}
			} else {
				target = Bukkit.getPlayer(args[0]);
				if (target == null){
					LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
					return true;
				}
			}
		}
		
		if (args.length == 2){
			target = Bukkit.getPlayer(args[0]);
			if (target == null){
				LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
				return true;
			}
		}
		
		target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, time, 0));
		LogHelper.showInfo("blindConfirm#####[" + Nicknames.getNick(target.getName()), sender, ChatColor.AQUA);
		if (sender != target){
			LogHelper.showInfo("blindNotify#####[" + Nicknames.getNick(sender.getName()), target, ChatColor.AQUA);
		}
		
		return true;
	}
}
