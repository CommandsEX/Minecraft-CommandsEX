package com.github.zathrus_writer.commandsex.helpers;

import static com.github.zathrus_writer.commandsex.Language._;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;

public class Weather implements Listener {

	private static Weather plugin = null;
	
	public Weather() {
		plugin = this;
	}
	
	/***
	 * INIT - initialization function, called when plugin is enabled to make periodic checks of weather conditions and warn players before it's about to rain
	 * @return
	 */
	public static void init(CommandsEX p) {
		if (!CommandsEX.getConf().getBoolean("weatherNotifyEnabled", false)) {
			return;
		} else {
			if (plugin == null) {
				new Weather();
			}
		}

		List<World> worlds = Bukkit.getWorlds();
		Integer notifyTimer = (CommandsEX.getConf().getInt("weatherNotifyTime", 15) * 20);
		
		// run through all worlds and set up a warning broadcast for each of them some time before the weather changes
		for (World w : worlds) {
			Integer duration = w.getWeatherDuration();
			if (duration > 0) {
				Integer timeout = ((duration < notifyTimer) ? 0 : (duration - notifyTimer));
				
				// set up a delayed task
				CommandsEX.plugin.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new WeatherWarning(w.getName()), timeout);
			}
		}
		
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(Weather.plugin, CommandsEX.plugin);
	}
	
	public static class WeatherWarning implements Runnable {
		private String worldName;
		
    	public WeatherWarning(String wName) {
    		this.worldName = wName;
    	}
    	
    	public void run() {
    		// broadcast weather change message to all players in the world
    		for (Player p : Bukkit.getOnlinePlayers()) {
    			if (p.getWorld().getName() == worldName) {
    				LogHelper.showInfo("weatherChangePlayerNotify", p);
    			}
    		}
    	}
    }
	
	/***
	 * Resets weather change broadcast on each weather change.
	 * @param e
	 * @return
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void setNewForecast(WeatherChangeEvent e) {
		// allow for 30 seconds timeout before we set up a new forecast delayed task for this world, since the weather duration in this event would be 0
		CommandsEX.plugin.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new DelayedForecast(e.getWorld()), (20 * 30));
	}
	
	public static class DelayedForecast implements Runnable {
    	private World w;
    	
    	public DelayedForecast(World w) {
    		this.w = w;
    	}
    	
    	public void run() {
    		Integer notifyTimer = (CommandsEX.getConf().getInt("weatherNotifyTime", 15) * 20);
    		Integer duration = w.getWeatherDuration();
    		
    		if (duration > 0) {
	    		Integer timeout = ((duration < notifyTimer) ? 0 : (duration - notifyTimer));
	    		
	    		// set up a delayed task
	    		CommandsEX.plugin.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new WeatherWarning(w.getName()), timeout);
    		}
    	}
    }
	
	public static class DelayedStorm implements Runnable {
    	private Player p;
    	
    	public DelayedStorm(Player p) {
    		this.p = p;
    	}
    	
    	public void run() {
    		String pName = p.getName();
			p.getWorld().setThundering(!p.getWorld().isThundering());
			
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!player.equals(p) && Permissions.checkPermEx(player, "cex.weather.notify")) {
					LogHelper.showInfo("weatherChangedBy#####[" + pName, player);
				}
			}
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
    		String pName = p.getName();
    		p.getWorld().setStorm((forceSun ? false : !p.getWorld().hasStorm()));
			
    		for (Player player : Bukkit.getOnlinePlayers()) {
				if (!player.equals(p) && Permissions.checkPermEx(player, "cex.weather.notify")) {
					LogHelper.showInfo("weatherChangedBy#####[" + pName, player);
				}
			}
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
