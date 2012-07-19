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
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_blindall {

	public static Boolean run(CommandSender sender, String alias, String[] args){

		if (sender instanceof Player && Utils.checkCommandSpam((Player) sender, "cex_blindall")){
			return true;
		}

		if (args.length > 1){
			Commands.showCommandHelpAndUsage(sender, "cex_blindall", alias);
			return true;
		}

		int time = 500;

		if (args.length == 1){
			if (args[0].matches(CommandsEX.intRegex)){
				time = Integer.valueOf(args[0]) * 20;
			} else {
				LogHelper.showInfo("blindInteger", sender, ChatColor.RED);
				return true;
			}
		}

		for (Player player : Bukkit.getOnlinePlayers()){
			if (sender != player){
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, time, 0));
				LogHelper.showInfo("blindNotify#####[" + sender.getName(), player, ChatColor.AQUA);
			}
		}
		LogHelper.showInfo("blindAllConfirm", sender, ChatColor.AQUA);
		return true;
	}
}