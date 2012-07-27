package com.github.zathrus_writer.commandsex.handlers;

import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Item;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;

public class Handler_explosions implements Listener {
	
	/***
	 * Explosion Handler - Blocks certain types of explosions, options in the config
	 * @author iKeirNez
	 */
	
	public Handler_explosions() {
		// Register our events
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent e){
		Entity entity = e.getEntity();
		Location loc = e.getLocation();
		
		// Check what entity caused the explosion, and is it set to block that kind of explosion in the config?
		if ((entity instanceof Creeper && CommandsEX.getConf().getBoolean("blockCreeperExplosions")) || (entity instanceof TNTPrimed && CommandsEX.getConf().getBoolean("blockTNTExplosions")) || ((entity instanceof Fireball || entity instanceof SmallFireball) && CommandsEX.getConf().getBoolean("blockFireballExplosions"))){
			// Clear the blocks the explosion will break
			e.blockList().clear();
			// Create the fancy explosion particles
			loc.getWorld().createExplosion(loc, 0);
		}
		
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e){
		Entity damager = e.getDamager();
		if ((damager instanceof TNTPrimed || damager instanceof Creeper || damager instanceof Fireball || damager instanceof SmallFireball)){
			// If the entity is an item, or if the config has blockMobDamage set to true then cancel the damage
			// This stops items from despawning from damage caused by explosions
			if (e.getEntity() instanceof Item || CommandsEX.getConf().getBoolean("blockMobExplosionDamage")){
				e.setCancelled(true);
			}
		}
	}
}
