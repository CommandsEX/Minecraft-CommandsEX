package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.ChatColor;
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

public class Command_cex_jump extends Common {
	/***
	 * JUMP - multiplies player's jump height when NanoSuit is activated
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender) && Permissions.checkPerms((Player) sender, "cex.nanosuit") && !Utils.checkCommandSpam((Player) sender, "jump-armor")) {
			// check if the player is nano-suited
			Player player = (Player) sender;
			String pName = sender.getName();
			if (Handler_nanosuit.suitedPlayers.containsKey(pName)) {
				if (Handler_nanosuit.jumps.contains(pName)) {
					player.removePotionEffect(PotionEffectType.JUMP);
					Handler_nanosuit.jumps.remove(pName);
				} else {
					player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, (int) (20 * CommandsEX.getConf().getDouble("nanoSuitTime")), CommandsEX.getConf().getInt("nanoSuitJump")));
					Handler_nanosuit.jumps.add(pName);
				}
				LogHelper.showInfo("nanoSuitJumpMode", sender);
			} else {
				LogHelper.showInfo("nanoSuitNotActivated", sender, ChatColor.RED);
			}
		}
        return true;
	}
}
