package com.github.zathrus_writer.commandsex.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.ClosestMatches;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_butcher {

	public static Boolean run(CommandSender sender, String alias, String[] args){
		
		if (!(sender instanceof Player)){
			LogHelper.showInfo("inWorldCommandOnly", sender, ChatColor.RED);
			return true;
		}

		if (args.length > 3){
			Commands.showCommandHelpAndUsage(sender, "cex_butcher", alias);
			return true;
		}
		
		Player player = (Player) sender;
		int radius = CommandsEX.getConf().getInt("butcherDefaultRadius");
		String typeToKill = "all";
		boolean lightning = false;
		
		if (args.length > 0){
			if (args[0].matches(CommandsEX.intRegex)){
				radius = Integer.valueOf(args[0]);
			} else if (args.length == 3){
				Commands.showCommandHelpAndUsage(sender, "cex_butcher", alias);
				return true;
			} else if (args[0].equalsIgnoreCase("lightning") || args[0].equalsIgnoreCase("-l")){
				lightning = true;
			}
			
			if (args.length > 1){
				if (!args[1].equalsIgnoreCase("lightning") && !args[1].equalsIgnoreCase("-l")){
					typeToKill = args[1];
					
					if (args.length > 2){
						if (args[2].equalsIgnoreCase("lightning") || args[2].equalsIgnoreCase("-l")){
							lightning = true;
						} else {
							Commands.showCommandHelpAndUsage(sender, "cex_butcher", alias);
							return true;
						}
					}
				} else {
					lightning = true;
				}
			} else {
				if (args[0].equalsIgnoreCase("lightning") || args[0].equalsIgnoreCase("-l")){
					lightning = true;
				} 
			}
		}
		
		List<Entity> entities = player.getNearbyEntities(radius, radius, radius);
		int killCount = 0;
		
		for (Entity entity : entities){
			EntityType eType = entity.getType();
			if (eType.isAlive() && eType.isSpawnable()){
				LivingEntity lEntity = (LivingEntity) entity;
				String typeOfEntity = Utils.typeOfEntity(entity.getType());
				if (typeOfEntity.equalsIgnoreCase(typeToKill) || typeToKill.equalsIgnoreCase("all")
						|| eType == ClosestMatches.livingEntity(typeToKill).get(0)){
					
					lEntity.remove();
					killCount++;
					
					if (lightning){
						entity.getWorld().strikeLightningEffect(entity.getLocation());
					}
				}
			}
		}
		
		LogHelper.showInfo("butcherSuccess#####[" + killCount + " #####butcherMobs", sender, ChatColor.AQUA);
		return true;
	}
}
