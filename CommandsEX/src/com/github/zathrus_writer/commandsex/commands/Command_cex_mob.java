package com.github.zathrus_writer.commandsex.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Ocelot.Type;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_mob {
	/***
	 * MOB - spawns a given mob at player's location
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		
		if (!(sender instanceof Player)){
			LogHelper.showInfo("inWorldCommandOnly", sender, ChatColor.RED);
			return true;
		}
		
		Player player = (Player) sender;
		
		if (Utils.checkCommandSpam(player, "cex_mob")){
			return true;
		}
		
		if (args.length == 0){
			ArrayList<String> available = new ArrayList<String>();
			for (EntityType en : EntityType.values()){
				if (en.isAlive() && en.isSpawnable()){
					available.add(Utils.userFriendlyNames(en.name()));
				}
			}
			LogHelper.showInfo("mobsList#####[" + Utils.implode(available, ", "), sender, ChatColor.AQUA);
			return true;
		}
		
		if (args.length > 2){
			Commands.showCommandHelpAndUsage(sender, "cex_mob", alias);
			return true;
		}
		
		EntityType toSpawn = null;
		String type = null;
		int amount = 1;
		int limit = CommandsEX.getConf().getInt("spawnMobLimit");
		
		if (args[0].contains(":")){
			String[] data = args[0].split(":");
			
			if (data.length == 0){
				LogHelper.showInfo("mobsInvalid", sender, ChatColor.RED);
				return true;
			}
			
			if (data.length == 1){
				LogHelper.showInfo("mobsInvalidType", sender, ChatColor.RED);
				return true;
			}
			
			if (Utils.livingEntityClosestMatches(data[0]).size() > 0){
				toSpawn = Utils.livingEntityClosestMatches(data[0]).get(0);
			} else {
				LogHelper.showInfo("mobsInvalid", sender, ChatColor.RED);
				return true;
			}
			
			type = data[1];
			if ((type.equalsIgnoreCase("baby") && isAgeable(toSpawn)) || (type.equalsIgnoreCase("charged") &&
					toSpawn == EntityType.CREEPER) || ((type.equalsIgnoreCase("angry") || type.equalsIgnoreCase("tamed")) &&
					toSpawn == EntityType.WOLF) || (type.equalsIgnoreCase("tamed") && toSpawn == EntityType.OCELOT)){
				// Nothing to do here
			} else if (toSpawn == EntityType.OCELOT && catTypeClosestMatches(type).size() > 0){
				type = "ocelottype:" + catTypeClosestMatches(type).get(0).name().replaceAll("_", "").toLowerCase().replaceAll("cat", "").replaceAll("ocelot", "");
			} else if (toSpawn == EntityType.SHEEP && Utils.dyeColorClosestMatches(type).size() > 0){
				type = "sheepcolor:" + Utils.dyeColorClosestMatches(type).get(0).name().replaceAll("_", "").toLowerCase();
			} else {
				LogHelper.showInfo("mobsInvalidType", sender, ChatColor.RED);
				return true;
			}
		} else {
			if (Utils.livingEntityClosestMatches(args[0]).size() > 0){
				toSpawn = Utils.livingEntityClosestMatches(args[0]).get(0);
			} else {
				LogHelper.showInfo("mobsInvalid", sender, ChatColor.RED);
				return true;
			}
		}
		
		if (args.length == 2){
			try {
				amount = Integer.valueOf(args[1]);
			} catch (Exception e){
				LogHelper.showInfo("mobsInt", sender, ChatColor.RED);
				return true;
			}
		}
		
		if (amount > limit && !player.hasPermission("cex.mob.spawn.bypasslimit")){
			LogHelper.showInfo("mobsLimit", sender, ChatColor.RED);
			amount = limit;
		}
		
		for (int i = 0; i < amount; i++){
			Location location = player.getTargetBlock(null, 50).getLocation();
			location.setY(location.getY() + 1);
			Entity entity = player.getWorld().spawnEntity(location, toSpawn);
			
			if (type != null){
				if (type.equalsIgnoreCase("baby")){
					Ageable ageable = (Ageable) entity;
					ageable.setBaby();
				}
				
				if (type.equalsIgnoreCase("tamed")){
					Tameable tame = (Tameable) entity;
					tame.setTamed(true);
					tame.setOwner((AnimalTamer) player);
				}
				
				if (type.equalsIgnoreCase("angry")){
					Wolf wolf = (Wolf) entity;
					wolf.setAngry(true);
				}
				
				if (type.equalsIgnoreCase("charged")){
					Creeper creep = (Creeper) entity;
					creep.setPowered(true);
				}
				
				if (type.startsWith("ocelottype:")){
					type = type.replaceFirst("ocelottype:", "");
					Ocelot oce = (Ocelot) entity;
					if (!type.startsWith("wild")){
						oce.setTamed(true);
						oce.setOwner((AnimalTamer) player);
					}
					
					oce.setCatType(catTypeClosestMatches(type).get(0));
				}
				
				if (type.startsWith("sheepcolor:")){
					type = type.split(":")[1];
					Sheep sheep = (Sheep) entity;
					sheep.setColor(Utils.dyeColorClosestMatches(type).get(0));
				}
			}
		}
		
		LogHelper.showInfo("mobsSuccess#####[" + amount + " " + Utils.userFriendlyNames((type != null ? type + " " : "") + toSpawn.name()), sender, ChatColor.AQUA);
		
		return true;
	}
	
	public static Boolean isAgeable(EntityType et){
		if (et == EntityType.COW || et == EntityType.CHICKEN || et == EntityType.SHEEP
				|| et == EntityType.PIG || et == EntityType.WOLF || et == EntityType.OCELOT
				|| et == EntityType.VILLAGER){
			return true;
		} else {
			return false;
		}
	}
	
	public static List<Type> catTypeClosestMatches(String input){
		ArrayList<Type> matches = new ArrayList<Type>();
        
        for (Type type : Type.values()){
        	if ((type.name().replace("_", "").toLowerCase().equals(input.toLowerCase()) || String.valueOf(type.getId()).equals(input))){
        		return Arrays.asList(type);
            } else if (type.name().replace("_", "").toLowerCase().contains(input.toLowerCase())){
            	matches.add(type);
            }
        }
        
        return matches;
	}
}
