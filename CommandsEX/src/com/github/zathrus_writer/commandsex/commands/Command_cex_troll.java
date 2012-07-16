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

public class Command_cex_troll {

	public static Boolean run(CommandSender sender, String alias, String[] args){
		// <Type of Troll> [Optional Parameter] [Player]
		
		if (args.length > 3 || args.length == 0){
			Commands.showCommandHelpAndUsage(sender, "cex_troll", alias);
			return true;
		}
		
		Player target = null;
		boolean everyone = false;
		String troll = null;
		String trollType = null;
		int setting = -1;
		
		if (args.length <= 3){
			troll = args[0];
			
			if (args.length > 1){
				if (args[1].matches(CommandsEX.intRegex)){
					setting = Integer.valueOf(args[1]);
				} else {
					LogHelper.showInfo("trollInteger", sender, ChatColor.RED);
					return true;
				}
			}
			
			if (args.length == 3){
				if (args[3].equals("*")){
					everyone = true;
				} else {
					target = Bukkit.getPlayer(args[2]);
					if (target == null){
						LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
						return true;
					}
				}
			} else {
				if (sender instanceof Player){
					Player player = (Player) sender;
					target = player;
				} else {
					Commands.showCommandHelpAndUsage(sender, "cex_troll", alias);
					return true;
				}
			}
		}
		
		if (troll.equalsIgnoreCase("blind")){
			target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (setting != -1 ? setting * 20 : 500), 0));
			LogHelper.showInfo("trollConfirm#####" + (sender != target ? "[" + target.getName() : "") + "trollWith#####[" + trollType, sender);
			return true;
		}
		
		return true;
	}
}
