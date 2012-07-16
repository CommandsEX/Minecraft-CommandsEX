package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_blindall {

	public static Boolean run(CommandSender sender, String alias, String[] args){
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (Utils.checkCommandSpam(player, "cex_blindall")){
				return true;
			}
		if (args.length == 0){
			if (!(sender instanceof Player)){
				Commands.showCommandHelpAndUsage(sender, "cex_blindall", alias);
				return true;
			}
			for (Player p : Bukkit.getOnlinePlayers()){
				Player blind = Bukkit.getPlayer(args[0]);
				// Don't launch the sender
				if (!sender.getName().equalsIgnoreCase(p.getName())){
					sender.sendMessage(ChatColor.AQUA + "You made the whole server blind");
					blind.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 500, 0));
			
				}
				
				
			}	
			
		}
		}
		return false;
	}
	
}