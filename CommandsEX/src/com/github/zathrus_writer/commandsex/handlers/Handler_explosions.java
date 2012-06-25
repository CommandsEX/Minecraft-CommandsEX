package com.github.zathrus_writer.commandsex.handlers;

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
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent e){
		Entity entity = e.getEntity();
		
		if ((entity instanceof Creeper && CommandsEX.getConf().getBoolean("explosions.creeper")) || (entity instanceof TNTPrimed && CommandsEX.getConf().getBoolean("explosions.tnt")) || ((entity instanceof Fireball || entity instanceof SmallFireball) && CommandsEX.getConf().getBoolean("explosions.fireball"))){
			e.setYield(0);
			entity.remove();
			e.setCancelled(true);
			entity.getWorld().createExplosion(entity.getLocation(), 0);
		}
		
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e){
		Entity damager = e.getDamager();
		
		if ((damager instanceof TNTPrimed || damager instanceof Creeper || damager instanceof Fireball || damager instanceof SmallFireball)){
			if (e.getEntity() instanceof Item || CommandsEX.getConf().getBoolean("explosions.blockMobDamage")){
				e.setCancelled(true);
			}
		}
	}
}
