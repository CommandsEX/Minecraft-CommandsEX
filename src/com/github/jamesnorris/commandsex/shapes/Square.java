package com.github.jamesnorris.commandsex.shapes;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Square {
	private int x, y, z, radius, i, j, k;
	private World world;
	private Location loc, center;
	private List<Location> locs;

	/**
	 * Creates a new square, with the center being the given location,
	 * and the radius being the given radius.
	 * 
	 * @param centerLocation The center of the square
	 * @param radius The radius of the square
	 */
	public Square(Location centerLocation, int radius) {
		this.center = centerLocation;
		this.x = centerLocation.getBlockX();
		this.y = centerLocation.getBlockY();
		this.z = centerLocation.getBlockZ();
		this.radius = radius;
		this.world = centerLocation.getWorld();
		for (i = -radius; i <= radius; i++) {
			for (j = -radius; j <= radius; j++) {
				for (k = -radius; k <= radius; k++) {
					loc = world.getBlockAt(x + i, y + j, z + k).getLocation();
					locs.add(loc);
				}
			}
		}
	}

	/**
	 * Changes the type of all blocks in the square to the specified material.
	 * 
	 * @param material The type to change the blocks to
	 */
	public void changeType(Material material) {
		for (Location l : locs) {
			l.getBlock().setType(material);
		}
	}

	/**
	 * Changes the type of all blocks in a square from the specified fromMaterial,
	 * to the specified toMaterial.
	 * 
	 * @param fromMaterial The type to change from
	 * @param toMaterial The type to change to
	 */
	public void changeFromToType(Material fromMaterial, Material toMaterial) {
		for (Location l : locs) {
			Block b = l.getBlock();
			if (b.getType() == fromMaterial) {
				b.setType(toMaterial);
			}
		}
	}

	/**
	 * Checks if the square contains the defined material.
	 * 
	 * @param material The type to check for
	 * @return Whether or not the material is in the square
	 */
	public boolean contains(Material material) {
		for (Location l : locs) {
			if (l.getBlock().getType() == material) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the center location of the square.
	 * 
	 * @return The center of the sqaure
	 */
	public Location getCenter() {
		return center;
	}

	/**
	 * Gets all locations within the square.
	 * 
	 * @return A list of locations within the square
	 */
	public List<Location> getLocations() {
		return locs;
	}

	/**
	 * Gets the radius of the square.
	 * 
	 * @return The radius of the square
	 */
	public int getRadius() {
		return radius;
	}
}
