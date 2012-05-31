package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;

public class Command_cex_smite {
	
	/***
	 * SMITE - kills a player with LIGHTNING
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if(args.length > 0) {
			// get variables about the player 
			Player smited = Bukkit.getServer().getPlayer(args[0]);
			Location loc = smited.getLocation();
		
			// smite the player
			smited.getWorld().strikeLightningEffect(loc);
			smited.setHealth(0);
		
			// show the sender a message
			LogHelper.showInfo("smitePlayer#####[" + smited.getName(), sender);
			
			// config variable
			Boolean showMessageOnSmite = CommandsEX.getConf().getBoolean("showMessageOnSmite");
			
			// show who smited the smitee (is that a word)
			if(showMessageOnSmite == true) {
				LogHelper.showWarning("smiteRecieveSmite#####[" + sender.getName(), smited);
			}
			
		} else {
			Commands.showCommandHelpAndUsage(sender, "cex_smite", alias);
		}
		return true;
	}
}
