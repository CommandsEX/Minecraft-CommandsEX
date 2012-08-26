package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_smite {
	
	/***
	 * SMITE - kills a player with LIGHTNING
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (!Utils.checkCommandSpam((Player) sender, "smite") && ((!(sender instanceof Player)) || ((sender instanceof Player) && Permissions.checkPerms((Player) sender, "cex.smite")))) {
			if(args.length > 0) {
				// get variables about the player 
				Player smited = Bukkit.getServer().getPlayer(args[0]);
				if (smited == null) {
					LogHelper.showWarning("invalidPlayer", sender);
					return true;
				}
				
				Location loc = smited.getLocation();
				// smite the player
				smited.getWorld().strikeLightningEffect(loc);
				// set last damage cause for custom death messages
				smited.setLastDamageCause(new EntityDamageEvent(smited, DamageCause.LIGHTNING, 20));
				smited.setHealth(0);
			
				// show the sender a message
				LogHelper.showInfo("smitePlayer#####[" + Nicknames.getNick(smited.getName()), sender);
				
				// config variable
				Boolean showMessageOnSmite = CommandsEX.getConf().getBoolean("showMessageOnSmite");
				
				// show who smited the smitee (is that a word)
				if(showMessageOnSmite == true) {
					LogHelper.showWarning("smiteRecieveSmite#####[" + Nicknames.getNick(sender.getName()), smited);
				}
				
			} else {
				Commands.showCommandHelpAndUsage(sender, "cex_smite", alias);
			}
		}
		return true;
	}
}
