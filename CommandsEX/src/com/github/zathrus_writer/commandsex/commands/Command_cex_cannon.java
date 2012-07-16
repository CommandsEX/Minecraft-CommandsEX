package com.github.zathrus_writer.commandsex.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_cannon {
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		
		int explosionStrengthLimit = 25;
		
		ArrayList<String> entities = new ArrayList<String>();
		for (EntityType en : EntityType.values()){
			if (en.isAlive() && en.isSpawnable()){
				entities.add(Utils.userFriendlyNames(en.name()).replace(" ", ""));
			}
		}
		
		if (args.length == 0){
			LogHelper.showInfo("cannonList#####[" + Utils.implode(entities, ", "), sender, ChatColor.AQUA);
			return true;
		}
		
		if (args.length > 3){
			Commands.showCommandHelpAndUsage(sender, "cex_cannon", alias);
			return true;
		}
		
		EntityType toSpawn = null;
		int explosionStrength = CommandsEX.getConf().getInt("CannonExplosionStrength");
		Player target = null;
		String type = null;
		String entity = null;
		
		if (args.length == 1 || args.length == 2){
			if (sender instanceof Player){
				target = (Player) sender;
				if (args[0].contains(":")){
					String[] data = args[0].split(":");
					entity = data[0];
					if (data.length == 2){ type = data[1]; }
				} else {
					entity = args[0];
				}
				
				if (Utils.entityClosestMatches(entity).size() > 0){
					List<EntityType> matches = Utils.entityClosestMatches(entity);
					toSpawn = matches.get(0);
				} else {
					LogHelper.showInfo("cannonInvalid", sender, ChatColor.RED);
					return true;
				}
			} else {
				Commands.showCommandHelpAndUsage(sender, "cex_cannon", alias);
				return true;
			}
		}
		
		if (args.length == 2){
			if (args[1].matches(CommandsEX.intRegex)){
				explosionStrength = Integer.valueOf(args[1]);
				if (explosionStrength > explosionStrengthLimit){
					LogHelper.showInfo("cannonLimit", sender, ChatColor.RED);
					explosionStrength = explosionStrengthLimit;
				}
			} else {
				LogHelper.showInfo("cannonInteger", sender, ChatColor.RED);
				return true;
			}
		}
		
		if (args.length == 3){
			target = Bukkit.getPlayer(args[0]);
			if (target == null){
				LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
				return true;
			}
			
			if (args[1].contains(":")){
				String[] data = args[1].split(":");
				entity = data[0];
				if (data.length == 2){ type = data[1]; }
			} else {
				entity = args[1];
			}
			
			if (Utils.entityClosestMatches(entity).size() > 0){
				List<EntityType> matches = Utils.entityClosestMatches(entity);
				toSpawn = matches.get(0);
			} else {
				LogHelper.showInfo("cannonInvalid", sender, ChatColor.RED);
				return true;
			}
			
			if (args[2].matches(CommandsEX.intRegex)){
				explosionStrength = Integer.valueOf(args[2]);
				if (explosionStrength > explosionStrengthLimit){
					LogHelper.showInfo("cannonLimit", sender, ChatColor.RED);
					explosionStrength = explosionStrengthLimit;
				}
			} else {
				LogHelper.showInfo("cannonInteger", sender, ChatColor.RED);
				return true;
			}
		}
		
		DyeColor dye = null;

		try {
			for (DyeColor d : DyeColor.values()){
				String lower = d.name().toLowerCase();
				if (lower.equalsIgnoreCase(type)){
					dye = d;
				}
			}
		} catch (Exception e){

		}

		if (type != null){
			if (type.equalsIgnoreCase("charged") || type.equalsIgnoreCase("powered") && toSpawn == EntityType.CREEPER){
				type = "charged";
			} else if (type.equalsIgnoreCase("baby") || type.equalsIgnoreCase("child") && (toSpawn == EntityType.PIG || toSpawn == EntityType.COW
					|| toSpawn == EntityType.CHICKEN || toSpawn == EntityType.SHEEP || toSpawn == EntityType.OCELOT || toSpawn == EntityType.WOLF)){
				type = "baby";
			} else if (type.equalsIgnoreCase("tamed") && (toSpawn == EntityType.WOLF || toSpawn == EntityType.OCELOT)){
				
			} else if (type.equalsIgnoreCase("angry") && toSpawn == EntityType.WOLF){
				
			}
			else if (toSpawn == EntityType.SHEEP && dye != null){
				// Correct, nothing to do here
			} else {
				LogHelper.showInfo("cannonInvalidType", sender, ChatColor.RED);
				return true;
			}
		}
		

		final LivingEntity mob = target.getWorld().spawnCreature(target.getEyeLocation(), toSpawn);
		
		if (toSpawn == EntityType.OCELOT){
			final Ocelot ocelot = (Ocelot) mob;
			Random random = new Random();
			int i = random.nextInt(Ocelot.Type.values().length);
			ocelot.setCatType(Ocelot.Type.values()[i]);
		}
		
		if (type != null){
			if (type.equalsIgnoreCase("baby")){
				Ageable age = (Ageable) mob;
				age.setBaby();
			} else if (type.equalsIgnoreCase("tamed")){
				Tameable tame = (Tameable) mob;
				tame.setTamed(true);
			} else if (type.equalsIgnoreCase("angry")){
				Wolf wolf = (Wolf) mob;
				wolf.setAngry(true);
			} else if (type.equalsIgnoreCase("charged")){
				Creeper creep = (Creeper) mob;
				creep.setPowered(true);
			} else if (dye != null){
				Sheep sheep = (Sheep) mob;
				sheep.setColor(dye);
			}
		}
		
		mob.setVelocity(target.getEyeLocation().getDirection().multiply(2));
		mob.setFallDistance(-999999999999F);
		LogHelper.showInfo("cannonCreated", sender);
		// required for task, has to be final
		final int newExplosionStrength = explosionStrength;
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new Runnable() {
			public void run() {
				final Location loc = mob.getLocation();
				mob.remove();
				loc.getWorld().createExplosion(loc, newExplosionStrength);
			}
		}, 20L);
		return true;
	}
}