package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_blind {


	public static Boolean run(CommandSender sender, String alias, String[] args){
		
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (Utils.checkCommandSpam(player, "cex_blind")){
				return true;
			}
		}
		
		if (args.length == 0){
			if (!(sender instanceof Player)){
				Commands.showCommandHelpAndUsage(sender, "cex_blind", alias);
				return true;
			}
			
			Player player = (Player) sender;
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 500, 0));
			player.sendMessage(ChatColor.GREEN + "You are blind");
		} else if (args.length == 1){
			Player blind = Bukkit.getPlayer(args[0]);
			
			if (blind == null){
				LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
				return true;
			}
			
			if (sender.getName().equalsIgnoreCase(blind.getName())){
				blind.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 500, 0));
				blind.sendMessage(ChatColor.GREEN + "You are blind");
			} else if ((!(sender instanceof Player)) || (((Player) sender).hasPermission("cex.blind.others"))){
				blind.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 500, 0));
				LogHelper.showInfo("You are blind!" + sender.getName(), blind, ChatColor.GREEN);
			} else {
				LogHelper.showInfo("You do not have permission to blind others!", sender, ChatColor.RED);
				return true;
			}
		} else {
			Commands.showCommandHelpAndUsage(sender, "cex_blind", alias);
			return true;
		}
		return true;
	}
}
