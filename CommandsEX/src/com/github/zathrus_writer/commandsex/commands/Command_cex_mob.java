package com.github.zathrus_writer.commandsex.commands;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_mob {
	/***
	 * MOB - spawns a given mob at player's location
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player = (Player)sender;
			
			if (!Utils.checkCommandSpam(player, "mob-spawn") && Permissions.checkPerms(player, "cex.mob.spawn")) {
				if ((args.length > 0) && (EntityType.fromName(args[0]) != null)) {
					Location loc = player.getTargetBlock(null, 100).getLocation().add(0, 1, 0);
					
					Integer mobCount = 1;
					if ((args.length > 1) && args[1].matches(CommandsEX.intRegex)) {
						mobCount = Integer.parseInt(args[1]);
						
					}
					
					for (Integer i = 0; i < mobCount; i++) {
						player.getWorld().spawnCreature(loc, EntityType.fromName(args[0]));
					}
				} else {
					// list all available entities
					List<String> s = new ArrayList<String>();
					for (EntityType et : EntityType.values()) {
						if (et.isAlive()) {
							s.add(et.getName());
						}
					}
					LogHelper.showInfo("mobsList#####[" + ChatColor.WHITE + Utils.implode(s, ", "), sender);
				}
			}
		}
        return true;
	}
}
