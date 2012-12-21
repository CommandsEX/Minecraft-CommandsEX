package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.handlers.Handler_nanosuit;
import com.github.zathrus_writer.commandsex.helpers.Common;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_speed extends Common {
	/***
	 * SPEED - multiplies player's speed when NanoSuit is activated
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender) && Permissions.checkPerms((Player) sender, "cex.nanosuit") && !Utils.checkCommandSpam((Player) sender, "speed-armor")) {
			// check if the player is nano-suited
			Player player = (Player) sender;
			String pName = sender.getName();
			if (Handler_nanosuit.suitedPlayers.containsKey(pName)) {
				if (Handler_nanosuit.speed.contains(pName)) {
					player.removePotionEffect(PotionEffectType.SPEED);
					Handler_nanosuit.speed.remove(pName);
				} else {
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (int) (20 * CommandsEX.getConf().getDouble("nanoSuitTime")), CommandsEX.getConf().getInt("nanoSuitSpeed")));
					Handler_nanosuit.speed.add(pName);
				}
				LogHelper.showInfo("nanoSuitSpeedMode", sender);
			}
		}
        return true;
	}
}
