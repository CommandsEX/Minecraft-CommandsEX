package com.github.ikeirnez.commandsex.handlers;

/* Breakable Imports */
import org.bukkit.craftbukkit.v1_4_6.entity.CraftEntity;
/* End Breakable Imports */

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;

import com.github.ikeirnez.commandsex.CommandsEX;

public class Meteor {
	private Location spawn, target;
	private World world;
	private Entity entity;
	private Fireball fireball;
	private Random random = new Random();
	private float yield = 2, maxyield = 18, minyield = 6, speed = 0.15F;
	private int id = -1;

	public Meteor(Location spawn, Location target) {
		this.spawn = spawn;
		this.world = spawn.getWorld();
		this.entity = world.spawnEntity(spawn, EntityType.FIREBALL);
		this.fireball = (Fireball) entity;
		this.yield = random.nextInt((int) maxyield) + minyield;
		setPower(yield);
		setTarget(target);
	}

	public float getPower() {
		return yield;
	}

	public void setPower(float yield) {
		this.yield = yield;
		fireball.setYield(yield);
	}

	public void setTarget(final Location target) {
		if (id != -1)
			cancel();
		this.target = target;
		System.out.println(target.getBlockX() + ", " + target.getBlockY() + ", " + target.getBlockZ());//TODO remove
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(CommandsEX.plugin, new Runnable() {
			public void run() {
				if (!entity.isDead())
					move(target);
				else
					cancel();
			}
		}, 1, 1);
	}

	public Location getTarget() {
		return target;
	}

	public Location getSpawn() {
		return spawn;
	}

	public World getWorld() {
		return world;
	}

	public Fireball getFireball() {
		return fireball;
	}

	private void cancel() {
		id = -1;
		Bukkit.getScheduler().cancelTask(id);
	}

	private void move(Location target) {
		Location l = entity.getLocation();
		int X = l.getBlockX();
		int Z = l.getBlockZ();
		double modX = 0;
		double modZ = 0;
		if (X < target.getBlockX())
			modX = speed;
		else if (X > target.getBlockX())
			modX = -speed;
		else
			modX = 0;
		if (Z < target.getBlockZ())
			modZ = speed;
		else if (Z > target.getBlockZ())
			modZ = -speed;
		else
			modZ = 0;
		((CraftEntity) entity).getHandle().setLocation(l.getX() + modX, l.getY() - speed, l.getZ() + modZ, 0, 0);
		//((CraftEntity) entity).getHandle().move(l.getBlockX() + modX, l.getBlockY() - speed, l.getBlockZ() + modZ);//TODO why isn't this going the right direction? (setPosition())?
	}
}
