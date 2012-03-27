package com.github.zathrus_writer.commandsex.helpers;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;

/***
 * Contains set of commands to be executed for teleportation purposes.
 * @author zathrus-writer
 *
 */
public class Teleportation {
	/***
	 * TPCOMMON - used as base method for teleporting players one to another
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean tp_common(CommandSender sender, String[] args, String command, String alias) {
		Player player = (Player)sender;
		if (!Utils.checkCommandSpam(player, "tp-"+command)) {
			// check the number of arguments
			int aLength = args.length;
			if (aLength > 2) {
				LogHelper.showWarning("pTooManyArguments", sender);
				return true;
			} else if (aLength == 0) {
				Commands.showCommandHelpAndUsage(sender, "cex_" + command, alias);
				return true;
			}
	
			// check if given players are online
			Player player1 = Bukkit.getServer().getPlayer(args[0]);
			Player player2;
			if (aLength > 1) {
				player2 = Bukkit.getServer().getPlayer(args[1]);
			} else {
				// set the command sender as second player in case we need them later (for /tpto)
				player2 = player;
			}
	
			if ((player1 == null) || (player2 == null)) {
				LogHelper.showWarning("tpInvalidPlayer", sender);
				return true;
			}
			
			// also, we cannot teleport player to himself
			if (player1.getName().equals(player2.getName())) {
				LogHelper.showWarning("tpCannotTeleportSelf", sender);
				return true;
			}
			
			/***
			 * Teleportation fun starts here :-P
			 */
			if ((command.equals("tp") && (aLength == 1)) || command.equals("tpto")) {
				// simple tp to another person
				delayedTeleport(player2, player1.getLocation());
			} else if ((command.equals("tphere")) || (command.equals("tp") && (aLength > 1))) {
				// teleporting another player to our position OR first player to second player (via arguments)
				delayedTeleport(player1, player2.getLocation());
			}
		}
        return true;
	}
	
	/***
	 * Code taken from HomeSpawnPlus, which uses "codename_B's excellent BananaChunk plugin's code,
	 * which this forces Bukkit to refresh the chunk the player is teleporting into"
	 * @param l
	 */
	public static void refreshMapChunk(Location l) {
		World world = l.getWorld();
    	Chunk chunk = world.getChunkAt(l);
    	int chunkx = chunk.getX();
    	int chunkz = chunk.getZ();
    	world.refreshChunk(chunkx, chunkz);
	}
	
	/** Can be used to teleport the player on a slight delay, which gets around a nasty issue that can crash
     * the server if you teleport them during certain events (such as onPlayerJoin).
     * 
     * Credits: andune (http://dev.bukkit.org/profiles/andune), HomeSpawnPlus creator (http://dev.bukkit.org/server-mods/homespawnplus)
     * 
     * @param p
     * @param l
     */
    public static void delayedTeleport(Player p, Location l) {
    	refreshMapChunk(l);
    	CommandsEX.plugin.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new DelayedTeleport(p, l), 2);
    }

    public static class DelayedTeleport implements Runnable {
    	private Player p;
    	private Location l;
    	
    	public DelayedTeleport(Player p, Location l) {
    		this.p = p;
    		this.l = l;
    	}
    	
    	public void run() {
    		p.teleport(l);
    	}
    }
}