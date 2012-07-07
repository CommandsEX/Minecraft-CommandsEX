package com.github.zathrus_writer.commandsex.commands;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
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
				if (args.length > 0) {
					EntityType entity = EntityType.fromName(args[0]);
					if (args[0].equalsIgnoreCase("chargedcreeper")){ entity = EntityType.CREEPER; }
					Location loc = player.getTargetBlock(null, 100).getLocation().add(0, 1, 0);
					
					// Amount of mobs to spawn
					Integer mobCount = 1;
					if ((args.length > 1) && args[1].matches(CommandsEX.intRegex)) {
						mobCount = Integer.parseInt(args[1]);
					}

					int limit = CommandsEX.getConf().getInt("spawnMobLimit");
					// Check for spawn mob limit
					if (limit != 0 && mobCount > limit && !player.hasPermission("cex.mob.spawn.bypasslimit")){
						// Set the mob count to maximum limit
						mobCount = limit;
						LogHelper.showInfo("mobsLimit", sender, ChatColor.RED);
					}
					
					// Check the mob type is valid
					if (entity != null){
						for (Integer i = 0; i < mobCount; i++) {
							Entity entity1 = player.getWorld().spawnCreature(loc, entity);
							if (args[0].equalsIgnoreCase("chargedcreeper")){
								Creeper creep = ((Creeper) entity1);
								creep.setPowered(true);
							}
						}
						
						LogHelper.showInfo("mobsSuccess#####[" + mobCount + " " + Utils.userFriendlyNames((args[0].equalsIgnoreCase("chargedcreeper") ? "Charged_Creeper" : entity.getName())), sender, ChatColor.AQUA);
					} else {
						LogHelper.showInfo("mobsInvalid", sender, ChatColor.RED);
					}
				} else {
					// list all available entities
					List<String> s = new ArrayList<String>();
					s.add("ChargedCreeper");
					for (EntityType et : EntityType.values()) {
						if (et.isAlive() && et.isSpawnable()) {
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
