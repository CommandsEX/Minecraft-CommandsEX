package com.github.zathrus_writer.commandsex.helpers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Effect;
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
	
	// stores all requests for all tpa types
	public static List<String> tpaallRequests = new ArrayList<String>();
	public static List<String> tpaRequests = new ArrayList<String>();
	public static List<String> tpahereRequests = new ArrayList<String>();
	
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
				LogHelper.showWarning("invalidPlayer", sender);
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
				LogHelper.showInfo("tpTeleport#####[ " + Nicknames.getNick(player2.getName()), sender);
			} else if ((command.equals("tphere")) || (command.equals("tp") && (aLength > 1))) {
				// teleporting another player to our position OR first player to second player (via arguments)
				delayedTeleport(player1, player2.getLocation());
				LogHelper.showInfo("[" + Nicknames.getNick(player2.getName()) + "tpHereTeleport", sender);
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
    public static void delayedTeleport(Player p, Location l, Runnable... r) {
    	refreshMapChunk(l);
    	
    	CommandsEX.plugin.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new DelayedTeleport(p, l, r), 2);
    }

    public static class DelayedTeleport implements Runnable {
    	private Player p;
    	private Location l;
    	private Runnable r;
    	
    	public DelayedTeleport(Player p, Location l, Runnable... r) {
    		this.p = p;
    		this.l = l;
    		this.r = ((r.length > 0) ? r[0] : null);
    	}
    	
    	public void run() {
    		p.getWorld().playEffect(p.getLocation(), Effect.SMOKE, 20);
    		p.teleport(l);
    		p.getWorld().playEffect(p.getLocation(), Effect.SMOKE, 20);
    		
    		// check if we should not call a post-teleport function and call it with a delay, since teleporting takes time
    		// and the result would not be pretty (like being frozen/jailed in the air and kicked for flying)
    		if (this.r != null) {
    			CommandsEX.plugin.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, r, (20 * 2));
    		}
    	}
    }
}