package com.github.zathrus_writer.commandsex.handlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;

public class Handler_tntkamikaze implements Listener {

	public static List<String> explodingPlayers = new ArrayList<String>();
	
	/***
	 * Activate event listeners.
	 */
	public Handler_tntkamikaze() {
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}

	// single-purpose class to remove player from internal list of exploding players
	// ... this is to prevent listening to this player's damage events, since they'd create a chain reaction of explosion upon explosion
	public static class DelayedRemoval implements Runnable {
    	private String p;
    	
    	public DelayedRemoval(String p) {
    		this.p = p;
    	}
    	
    	public void run() {
    		// remove player from the list
    		Handler_tntkamikaze.explodingPlayers.remove(p);
    	}
    }
	
	// single-purpose class to do a delayed explosion on player
	public static class DelayedExplosion implements Runnable {
    	private Player p;
    	
    	public DelayedExplosion(Player p) {
    		this.p = p;
    	}
    	
    	public void run() {
    		// remove TNT from player's inventory
    		p.getInventory().remove(Material.TNT);

    		// create explosion in the world where the player is
    		Location l = this.p.getLocation();
    		this.p.playEffect(l, Effect.EXTINGUISH, 0);
    		p.getWorld().createExplosion(l, 4F);

    		// check if we should insta-kill the player
    		if (CommandsEX.getConf().getBoolean("kamikazeInstaKill")) {
    			p.setHealth(0);
    		}
    		
    		// remove player from the list
    		CommandsEX.plugin.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new DelayedRemoval(p.getName()), (20 * 5));
    	}
    }
	
	/***
	 * Check if a player that catches fire doesn't have TNT in inventory.
	 * If they do, the TNT will explode and be removed from their inventory, possibly insta-killing the player (if set in config).
	 * @param e
	 * @return
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void kamikaze(EntityDamageEvent e) {
		Player p;
		if (e.getEntityType() != EntityType.PLAYER) {
			return;
		} else {
			p = (Player) e.getEntity();
		}
		
		DamageCause dc = e.getCause();
		if (
				p.getInventory().contains(Material.TNT)
				&&
				!explodingPlayers.contains(p.getName())
				&&
				(
					((dc == DamageCause.FIRE) && Permissions.checkPermEx(p, "cex.tnt.kamikaze"))
					||
					((dc == DamageCause.BLOCK_EXPLOSION) && Permissions.checkPermEx(p, "cex.tnt.kamikaze.from.damage"))
				)
		) {
			// Only blow up the player if they didn't already die from the explosion
			if (!(e.getDamage() >= p.getHealth())){
				// tell the player he's about to explode :P
				Integer timeToExplode = CommandsEX.getConf().getInt("kamikazeTimeout", 3);
				LogHelper.showWarning("kamikazeTNTYouWillExplode#####[" + timeToExplode + " #####seconds", p);
				explodingPlayers.add(p.getName());
				CommandsEX.plugin.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new DelayedExplosion(p), (20 * CommandsEX.getConf().getInt("kamikazeTimeout", 3)));
			}
		}
	}
	
}
