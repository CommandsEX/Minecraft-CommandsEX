package com.github.zathrus_writer.commandsex.helpers;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Painting;

import com.github.zathrus_writer.commandsex.SQLManager;

public class BlockLogging {
	//ID, PlayerName, Action, Time, Block, World, Location
	public static boolean enabled = false;
	public static String table = "";
	
	public static boolean logEvent(String player, BlockLoggingAction action, Block block, World world, Location loc) {
		String c = ", ", q = "\'";
		String value = q+player+q+ c +q+action.getName()+q+ c +q+Utils.getTime()+q+ c +q+block.getTypeId()+q+
				c +q+world.getName()+q+ c + q+loc.getBlockX() + ":" + loc.getY() + ":" + loc.getBlockZ()+q;
		return SQLManager.query("INSERT INTO " + table + "(PlayerName, Action, Time, Block, World, Location) VALUES (" + value + ")");
	}
	
	public static boolean logEvent(String player, BlockLoggingAction action, Painting block, World world, Location loc) {
		String c = ", ", q = "\'";
		String value = q+player+q+ c +q+action.getName()+q+ c +q+Utils.getTime()+q+ c +q+"PAINTING"+q+
				c +q+world.getName()+q+ c + q+loc.getBlockX() + ":" + loc.getY() + ":" + loc.getBlockZ()+q;
		return SQLManager.query("INSERT INTO " + table + "(PlayerName, Action, Time, Block, World, Location) VALUES (" + value + ")");
	}
	
	public enum BlockLoggingAction {
		BLOCK_BREAK("BLOCK_BREAK"),
		BLOCK_PLACE("BLOCK_PLACE"),
		SIGN_CHANGE("SIGN_CHANGE"),
		BLOCK_FORM("BLOCK_FORM"),
		BLOCK_FADE("BLOCK_FADE"),
		BLOCK_BURN("BLOCK_BURN"),
		LEAF_DECAY("LEAF_DECAY"),
		ENTITY_EXPLOSION("ENTITY_EXPLOSION"),
		PAINTING_BREAK("PAINTING_BREAK"),
		PAINTING_PLACE("PAINTING_PLACE"),
		ENDERMAN_PICKUP("ENDERMAN_PICKUP"),
		ENDERMAN_PLACE("ENDERMAN_PLACE");
		
		private final String name;
		
		BlockLoggingAction(String name) {
			this.name = name;
		}
		
		String getName() {
			return this.name;
		}
	}

}
