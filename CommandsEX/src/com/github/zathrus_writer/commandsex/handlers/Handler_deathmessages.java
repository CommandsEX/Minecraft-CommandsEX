package com.github.zathrus_writer.commandsex.handlers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Handler_deathmessages implements Listener {

	public Handler_deathmessages(){
		// register events
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent e){
		FileConfiguration config = CommandsEX.getConf();
		Player victum = e.getEntity();
		EntityDamageEvent damev = victum.getLastDamageCause();
		DamageCause cause;
		if (damev != null){
			cause = damev.getCause();
		} else {
			return;
		}
		Entity killer = null;
		// put this in a try catch incase we get an NPE here
		try {
			killer = (((EntityDamageByEntityEvent) damev).getDamager());
		} catch (Exception ex){
			// no killer
		}
		String message = null;
		
		// check if player died via pvp
		if (killer instanceof Player){
			message = replacements(config.getString("deathPvP"), victum).replaceAll("%killer%", ((Player) killer).getName());
		}
		
		// check if drowned
		if (cause == DamageCause.DROWNING){
			message = replacements(config.getString("deathDrown"), victum);
		}
		
		// check if they fell
		if (cause == DamageCause.FALL){
			message = replacements(config.getString("deathFall"), victum);
		}
		
		// check if fire
		if (cause == DamageCause.FIRE || cause == DamageCause.FIRE_TICK){
			message = replacements(config.getString("deathFire"), victum);
		}
		
		// check if lava
		if (cause == DamageCause.LAVA){
			message = replacements(config.getString("deathLava"), victum);
		}
		
		// check if magic
		if (cause == DamageCause.MAGIC){
			message = replacements(config.getString("deathMagic"), victum);
		}
		
		// check if starved
		if (cause == DamageCause.STARVATION){
			message = replacements(config.getString("deathStarvation"), victum);
		}
		
		// check if struck by lightning
		if (cause == DamageCause.LIGHTNING){
			message = replacements(config.getString("deathLightning"), victum);
		}
		
		// check if poisoned
		if (cause == DamageCause.POISON){
			message = replacements(config.getString("deathPoison"), victum);
		}
		
		// check if suffocated
		if (cause == DamageCause.SUFFOCATION){
			message = replacements(config.getString("deathSuffocate"), victum);
		}
		
		// check if suicide
		if (cause == DamageCause.SUICIDE){
			message = replacements(config.getString("deathSuicide"), victum);
		}
		
		// check if void
		if (cause == DamageCause.VOID){
			message = replacements(config.getString("deathVoid"), victum);
		}
		
		if (cause == DamageCause.CONTACT || cause == DamageCause.ENTITY_ATTACK){
			message = replacements(config.getString("death" + killer.getType().getName()), victum);
		}
		
		// check if explosion
		if (cause == DamageCause.BLOCK_EXPLOSION || cause == DamageCause.ENTITY_EXPLOSION){
			// check if tnt
			if (killer.getType() == EntityType.PRIMED_TNT){
				message = replacements(config.getString("deathTNT"), victum);
			}
			
			// check if creeper
			if (killer.getType() == EntityType.CREEPER){
				message = replacements(config.getString("deathCreeper"), victum);
			}
		}

		// check if projectile
		if (cause == DamageCause.PROJECTILE){
			// check if arrow
			if (killer instanceof Arrow){
				Arrow arrow = (Arrow) killer;
				if (arrow.getShooter() instanceof Player){
					// player shot the arrow
					message = replacements(config.getString("deathShotByPlayer"), victum).replaceAll("%killer%", ((Player) arrow.getShooter()).getName());
				} else if (arrow.getShooter() instanceof Skeleton) {
					// skeleton shot the arrow
					message = replacements(config.getString("deathShotByMob"), victum).replaceAll("%killer%", Utils.userFriendlyNames(arrow.getShooter().getType().getName()));
				} else {
					// something else shot the arrow, e.g. dispenser
					message = replacements(config.getString("deathShotOther"), victum);
				}
			}
		}
		
		// small fireball
		if (killer instanceof SmallFireball){
			SmallFireball fireball = (SmallFireball) killer;
			// check if ghast
			if (fireball.getShooter() instanceof Ghast){
				message = replacements(config.getString("deathGhast"), victum);
			}
			
			// check if blaze
			if (fireball.getShooter() instanceof Blaze){
				message = replacements(config.getString("deathBlaze"), victum);
			}
		}
		
		// normal fireball
		if (killer instanceof Fireball){
			Fireball fireball = (Fireball) killer;
			// check if ghast
			if (fireball.getShooter() instanceof Ghast){
				message = replacements(config.getString("deathGhast"), victum);
			}
			
			// check if blaze
			if (fireball.getShooter() instanceof Blaze){
				message = replacements(config.getString("deathBlaze"), victum);
			}
		}
		
		// send custom death message
		e.setDeathMessage(message);
	}
	
	public String replacements(String string, Player victum){
		return Utils.replaceChatColors(string).replaceAll("%victum%", victum.getName()).replaceAll("%world%", victum.getWorld().getName());
	}
}
