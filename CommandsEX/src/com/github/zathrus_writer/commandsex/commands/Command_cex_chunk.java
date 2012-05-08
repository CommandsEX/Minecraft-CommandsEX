package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_chunk {
	/***
	 * CHUNK - refreshes 5 chunks around player to close rendering gaps
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player = (Player)sender;
			
			if (!Utils.checkCommandSpam(player, "chunk") && Permissions.checkPerms(player, "cex.chunk.refresh")) {
				Location loc = player.getLocation();
				World w = player.getWorld();
				Chunk lastChunk = w.getChunkAt(loc);
				Chunk currentChunk = w.getChunkAt(loc);
				
				// first regenerate player's chunk
				w.refreshChunk((int) loc.getX(), (int) loc.getZ());
				
				// regenerate left
				while (lastChunk.equals(currentChunk)) {
					loc.setX(loc.getX() + 10);
					currentChunk = w.getChunkAt(loc);
				}
				lastChunk = currentChunk;
				w.refreshChunk((int) loc.getX(), (int) loc.getZ());
				loc = player.getLocation();
				
				// regenerate right
				while (lastChunk.equals(currentChunk)) {
					loc.setX(loc.getX() - 10);
					currentChunk = w.getChunkAt(loc);
				}
				lastChunk = currentChunk;
				w.refreshChunk((int) loc.getX(), (int) loc.getZ());
				loc = player.getLocation();
				
				// regenerate in front of player
				while (lastChunk.equals(currentChunk)) {
					loc.setZ(loc.getZ() + 10);
					currentChunk = w.getChunkAt(loc);
				}
				lastChunk = currentChunk;
				w.refreshChunk((int) loc.getX(), (int) loc.getZ());
				loc = player.getLocation();
				
				// regenerate behind the player
				while (lastChunk.equals(currentChunk)) {
					loc.setZ(loc.getZ() - 10);
					currentChunk = w.getChunkAt(loc);
				}
				lastChunk = currentChunk;
				w.refreshChunk((int) loc.getX(), (int) loc.getZ());
				loc = player.getLocation();
				
				// all done :)
				LogHelper.showInfo("chunksRegenerated", sender);
			}
		}
        return true;
	}
}
