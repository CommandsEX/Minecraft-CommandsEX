package com.github.zathrus_writer.commandsex.helpers;

import static com.github.zathrus_writer.commandsex.Language._;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;

public class Weather {

	public static class DelayedStorm implements Runnable {
    	private Player p;
    	
    	public DelayedStorm(Player p) {
    		this.p = p;
    	}
    	
    	public void run() {
			p.getWorld().setThundering(!p.getWorld().isThundering());
    	}
    }
	
	public static class DelayedRain implements Runnable {
    	private Player p;
    	private Boolean forceSun;
    	
    	public DelayedRain(Player p, Boolean... forceSun) {
    		this.p = p;
    		this.forceSun = (forceSun.length > 0);
    	}
    	
    	public void run() {
			p.getWorld().setStorm((forceSun ? false : !p.getWorld().hasStorm()));
    	}
    }
	
	/***
	 * STORM - toggles storm in the current world
	 * @param sender
	 * @param args
	 * @return
	 */
	public static void storm(CommandSender sender, String[] args, Boolean... forceOn) {
		Player player = (Player)sender;
		if ((forceOn.length > 0) && player.getWorld().isThundering()) {
			LogHelper.showInfo("weatherNothingToDo", sender);
			return;
		}
		
		LogHelper.showInfo((player.getWorld().isThundering() ? "thunderOff" : "thunderOn"), sender);
		// check when should the switch occur
		Integer timeToSwitch = 0;
		if (args.length > 0) {
			try {
				timeToSwitch = Integer.parseInt(args[0]);
				Bukkit.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + _((player.getWorld().isThundering() ? "thunderOffDelayed" : "thunderOnDelayed"), ""));
			} catch (Throwable e) {}
		}
		
		CommandsEX.plugin.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new DelayedStorm(player), (timeToSwitch * 20));
	}
	
	/***
	 * RAIN - toggles rain/snow in the current world
	 * @param sender
	 * @param args
	 * @return
	 */
	public static void rain(CommandSender sender, String[] args, Boolean... forceOn) {
		Player player = (Player) sender;
		if ((forceOn.length > 0) && player.getWorld().hasStorm()) {
			LogHelper.showInfo("weatherNothingToDo", sender);
			return;
		}
		
		LogHelper.showInfo((player.getWorld().isThundering() ? "rainOff" : "rainOn"), sender);
		// check when should the switch occur
		Integer timeToSwitch = 0;
		if (args.length > 0) {
			try {
				timeToSwitch = Integer.parseInt(args[0]);
				Bukkit.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + _((player.getWorld().hasStorm() ? "rainOffDelayed" : "rainOnDelayed"), ""));
			} catch (Throwable e) {}
		}
		
		CommandsEX.plugin.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new DelayedRain(player), (timeToSwitch * 20));
	}
	
	
	/***
	 * SUN - turns rain/snow off in the current world
	 * @param sender
	 * @param args
	 * @return
	 */
	public static void sun(CommandSender sender, String[] args, Boolean... forceOn) {
		Player player = (Player) sender;
		if ((forceOn.length > 0) && !player.getWorld().hasStorm()) {
			LogHelper.showInfo("weatherNothingToDo", sender);
			return;
		}
		
		LogHelper.showInfo("rainOff", sender);
		// check when should the switch occur
		Integer timeToSwitch = 0;
		if (args.length > 0) {
			try {
				timeToSwitch = Integer.parseInt(args[0]);
				Bukkit.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + _("rainOffDelayed", ""));
			} catch (Throwable e) {}
		}
		
		CommandsEX.plugin.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new DelayedRain(player, true), (timeToSwitch * 20));
	}
	
}
