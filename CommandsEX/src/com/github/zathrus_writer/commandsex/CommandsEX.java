package com.github.zathrus_writer.commandsex;


import static com.github.zathrus_writer.commandsex.Language._;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.zathrus_writer.commandsex.helpers.AutoUpdate;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.Jails;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Metrics;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class CommandsEX extends JavaPlugin implements Listener {
	// our plugin :-)
	public static CommandsEX plugin;
	public static String[] authors = { "iKeirNez", "Callum_White1997", "melonman1999" };
	// regex to check if String is a number
	public final static String intRegex = "(-)?(\\d){1,10}(\\.(\\d){1,10})?";
	// plugin description file, used at least on 2 places, so it's here :-P
	public transient static PluginDescriptionFile pdfFile;
	// if true, the _() function won't try to load existing perUserLocales from DB - used in exceptions handling
	public static Boolean avoidDB = false;
	// our command listener class
	private Commands cListener;
	// if SQL is enabled for our plugin, this will be true,
	// otherwise it'll remain false... this variable removes
	// the need for unneccessary SQLManager class if we don't need it
	public static Boolean sqlEnabled = false;
	// number of seconds each player on the server played so far
	public static Map<String, Integer> playTimes = new HashMap<String, Integer>();
	// timestamp of user's server join time, used to update playtime periodically
	public static Map<String, Integer> joinTimes = new HashMap<String, Integer>();
	// IPs of all users currently on the server (or that was on the server configurable time ago)
	public static Map<String, String> playerIPs = new HashMap<String, String>();
	// this map will hold IDs of tasks patched to scheduler for delayed execution when a user joins the server
	// ... this delay is used so when a player spams login/logout, the DB is not queried all the time for his playtime
	protected Map<String, Integer> playTimeLoadTasks = new HashMap<String, Integer>();
	// minimum number of seconds from player's last logout to pass before his playtime is loaded from DB
	public static Integer minTimeFromLogout = 30;
	// number of seconds after which we flush playTimes into database
	public static Integer playTimesFlushTime = 180;
	// number of seconds a player must stay on the server before his playTime is storable
	protected Integer minTimeToSavePlayTime = 45;
	// functions contained in this list will get executed on plugin disable,
	// so we can handle things like DB or XMPP disconnects correctly
	public static List<String> onDisableFunctions = new ArrayList<String>();
	// true if Vault plugin was found in the server installation
	public static Boolean vaultPresent = false;
	// reference our plugin timer
	private long startTime, stopTime, finalTime;
	// reference the auto updater
	AutoUpdate autoUpdate;

	/***
	 * Class constructor.
	 * We need the static plugin declaration for getting config from other modules.
	 */
	public CommandsEX() {
		plugin = this;
	}
	
	/***
	 * OnEnable
	 */
	@Override
	public void onEnable() {
		startTimer();
		// save default config if not saved yet
		getConfig().options().copyDefaults(true);
		if (getConf().getBoolean("autoUpdate")){
			try {
				autoUpdate = new AutoUpdate(this);
			} catch (Exception e) {

			}
		}
		saveConfig();
		
		// check for Vault plugin presence
		try {
			new Vault();
			vaultPresent = true;
		} catch (Throwable e) {}
		
		// set up commands listener
		cListener = new Commands(this);

		// initialize translations
		Language.init(this);

		// get description file and display initial startup OK info
		pdfFile = this.getDescription();
		LogHelper.logInfo("[" + pdfFile.getName() + "] " + _("startupMessage", "") + " " + Language.defaultLocale);
		LogHelper.logInfo("[" + pdfFile.getName() + "] " + _("version", "") + " " + pdfFile.getVersion() + " " + _("enableMsg", ""));

		// initialize database, if we have it included in our build
		Class<?>[] proto = new Class[] {this.getClass()};
		Object[] params = new Object[] {this};
		if (getConf().getBoolean("enableDatabase")) {
			try {
				Class<?> c = Class.forName("com.github.zathrus_writer.commandsex.SQLManager");
				Method method = c.getDeclaredMethod("init", proto);
				method.invoke(null, params);
			} catch (ClassNotFoundException e) {
				// this is OK, since we won't neccessarily have this class in each build
			} catch (Throwable e) {
				LogHelper.logSevere(_("dbError", ""));
				LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
			}
		}
		
		// enable existing classes that are listening to events - determine names from permissions
		// ... also call init() function for each helper class that requires initialization (has Init prefix in permissions)
		List<Permission> perms = CommandsEX.pdfFile.getPermissions();
		for(int i = 0; i <= perms.size() - 1; i++) {
			// call initialization function for each of the event handling functions
			String pName = perms.get(i).getName();
			if (pName.startsWith("Listener")) {
				String[] s = pName.split("\\.");
				if (s.length == 0) continue;
				try {
					Class.forName("com.github.zathrus_writer.commandsex.handlers.Handler_" + s[1]).newInstance();
				} catch (ClassNotFoundException e) {
					// this is OK, since we won't neccessarily have this class in each build
				} catch (Throwable e) {
					LogHelper.logSevere(_("loadTimeError", ""));
					LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
				}
			} else if (pName.startsWith("Init")) {
				String[] s = pName.split("\\.");
				if (s.length == 0) continue;
				try {
					Class<?> c = Class.forName("com.github.zathrus_writer.commandsex.helpers." + s[1]);
					Method method = c.getDeclaredMethod("init", proto);
					method.invoke(null, params);
				} catch (ClassNotFoundException e) {
					// this is OK, since we won't neccessarily have this class in each build
				} catch (Throwable e) {
					LogHelper.logSevere(_("loadTimeError", ""));
					LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
				}
			}
		}
		
		// setup a recurring task that will periodically save players' play times into DB
		if (sqlEnabled) {
			getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
				@Override
			    public void run() {
			        // flush play times only for players that have their playtime loaded initially
					Integer stamp = Utils.getUnixTimestamp(0L);
					Iterator<Entry<String, Integer>> it = CommandsEX.playTimes.entrySet().iterator();
					List<Object> insertParts = new ArrayList<Object>();
					List<Object> insertValues = new ArrayList<Object>();
					while (it.hasNext()) {
						Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>)it.next();
						
						// only update data for players that don't have -1 set as their playTime
						if (pairs.getValue() <= -1) continue;
						
						String pName = pairs.getKey();
						// update play time and join time
						Integer played = (pairs.getValue() + (stamp - CommandsEX.joinTimes.get(pName)));
						CommandsEX.playTimes.put(pName, played);
						CommandsEX.joinTimes.put(pName, Utils.getUnixTimestamp(0L));
						
						// prepare DB query parts
						insertParts.add("SELECT ? AS 'player_name', ? AS 'seconds_played'");
						insertValues.add(pName);
						insertValues.add(played);
						//it.remove(); // avoids a ConcurrentModificationException - not needed in our case and will clear out HashMap!
					}
					
					if (insertParts.size() > 0) {
						// update the database
						SQLManager.query("INSERT "+ (SQLManager.sqlType.equals("mysql") ? "" : "OR REPLACE ") +"INTO " + SQLManager.prefix + "playtime "+ Utils.implode(insertParts, " UNION ") + (SQLManager.sqlType.equals("mysql") ? " ON DUPLICATE KEY UPDATE seconds_played = VALUES(seconds_played)" : ""), insertValues);
					}
			    }
			}, (20 * playTimesFlushTime), (20 * playTimesFlushTime));
			
			// tell Bukkit we have some event handling to do in this class :-)
			this.getServer().getPluginManager().registerEvents(this, this);
		}

		// don't start metrics if the user has disabled it
		if (getConf().getBoolean("pluginMetrics")){
			try {
			    Metrics metrics = new Metrics(plugin);
			    metrics.start();
			} catch (IOException e) {

			}
		}
		stopTimer();
	}
	
	// Timer Methods
	public void startTimer(){
		startTime = System.currentTimeMillis();
	}
	
	public void stopTimer(){
		stopTime = System.currentTimeMillis();
		finalTime = stopTime - startTime;
		if (getConf().getBoolean("startupTimer")){
			LogHelper.logInfo("[CommandsEx] " + _("startupTime", "") + finalTime + "ms");
		}
	}

	/***
	 * Bukkit API requires us to bind each existing command individually
	 * to a separate class if we want to utilize such a class.
	 * Here, we'll just pass each command to the class' onCommand function,
	 * saving us the trouble of writing a list of all possible commands.
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		return cListener.onCommand(sender, cmd, commandLabel, args);
	}
	
	/***
	 * OnDisable
	 */
	@Override
	public void onDisable() {
		
		// kicks all players when the plugin is disabled (server is shutdown)
		for (Player player : Bukkit.getOnlinePlayers()){
			player.kickPlayer(ChatColor.RED + _("shutdownKickMessage", player.getName()));
		}
		// if we don't have per-player language loaded from DB, do not try to load it now :-)
		avoidDB = true;
		
		// execute everything that should be executed on disable
		if (onDisableFunctions.size() > 0) {
			Class<?>[] proto = new Class[] {this.getClass()};
			Object[] params = new Object[] {this};
			
			for (String s : onDisableFunctions) {
				try {
					String[] ss = s.split("#####");
					Class<?> c = Class.forName(ss[0]);
					Method method = c.getDeclaredMethod(ss[1], proto);
					method.invoke(null, params);
				} catch (Throwable e) {
					LogHelper.logSevere("[CommandsEX] " + _("errorFunctionOnDisableExecute", "") + s);
					LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
				}
			}
		}
		
		// close all database connections
		LogHelper.logInfo("[" + this.getDescription().getName() + "] " + _("disableMsg", ""));
	}

	/***
	 * Returns current config.
	 * @return
	 */
	public static FileConfiguration getConf() {
		return plugin.getConfig();
	}
		
	/***
	 * When a player joins the server, their join timestamp is saved, helping us to determine
	 * how long he's been online. Also, a new delayed task will be created that will load up player's
	 * full playtime from database, should the player stay on the server for more than minTimeFromLogout seconds.
	 * 
	 * Additionally, when a player joins the server, their IP is stored internally to allow for IP-banning when
	 * the player leaves as soon as they burst-grief.
	 * @param e
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void pJoin(PlayerJoinEvent e) {
		String pName = e.getPlayer().getName();
		playerIPs.put(pName.toLowerCase(), e.getPlayer().getAddress().getAddress().getHostAddress());

		// check if player is not jailed
		try {
			if (Jails.jailedPlayers.containsKey(pName)) {
				LogHelper.showInfo("jailsStillJailed", e.getPlayer());
			}
		} catch (Throwable ex) {}
		
		if (sqlEnabled) {
			// add -1 to playtimes, so our Runnable function will know to look the player up after the delay has passed
			playTimes.put(pName, -1);
			joinTimes.put(pName, Utils.getUnixTimestamp(0L));

			// cancel out any previously delayed task created by this user
			if (this.playTimeLoadTasks.containsKey(pName)) {
				this.getServer().getScheduler().cancelTask(this.playTimeLoadTasks.get(pName));
			}
			
			// add new delayed task to load user's playtime from DB
			this.playTimeLoadTasks.put(pName, this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				@Override
				public void run() {
					// load playtimes for all players with time set to -1 and valid time on the server (i.e. +(minTimeFromLogout - 5) seconds)
					// ... and -5 seconds to allow for server lag and similar things to happen :-)
					Iterator<Entry<String, Integer>> it = CommandsEX.playTimes.entrySet().iterator();
					List<String> playerNames = new ArrayList<String>();
					Integer stamp = Utils.getUnixTimestamp(0L);
					while (it.hasNext()) {
						Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>)it.next();

						// only check players with -1 as playtime
						if (pairs.getValue() > -1) continue;

						// check if the player's last logout time was not within last minTimeFromLogout seconds, in which case we don't bother
						// checking up on him and his playtime will be loaded as needed on Quit event
						String pName = (String)pairs.getKey();
						OfflinePlayer o = CommandsEX.plugin.getServer().getOfflinePlayer(pName);
						
						if ((o != null) && (o.getLastPlayed() > 0)) {
							// convert miliseconds of player last visit time to seconds
							Integer lastPlay = Utils.getUnixTimestamp(o.getLastPlayed());
							// check if we should be adding the player, based on last quitting time
							if ((stamp - lastPlay) >= (CommandsEX.minTimeFromLogout - 5)) {
								playerNames.add(pName);
							}
						} else {
							// this is a new player, set his playtime to 0
							CommandsEX.playTimes.put(pName, 0);
						}
				        //it.remove(); // avoids a ConcurrentModificationException - not needed in our case and will clear out HashMap!
				    }
					
					// load playtimes from DB
					Integer pSize = playerNames.size();
					if (pSize > 0) {
						try {
							String[] qMarks = new String[pSize];
							Arrays.fill(qMarks, "?");
							ResultSet res = SQLManager.query_res("SELECT * FROM " + SQLManager.prefix + "playtime WHERE player_name IN ("+ Utils.implode(qMarks, ", ") +")", playerNames);
							while (res.next()) {
								CommandsEX.playTimes.put(res.getString("player_name"), res.getInt("seconds_played"));
							}
							res.close();
						} catch (Throwable e) {
							// unable to load players' playtimes, show up on console
							LogHelper.logSevere("[CommandsEX] " + _("dbTotalPlayTimeGetError", ""));
							LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
							return;
						}
					}
				}
			}, (20 * minTimeFromLogout)));
		}
	}
	
	/***
	 * Single-purpose class that handles removal of old Players' IPs after a certain amount of time.
	 */
	public static class DelayedIpRemoval implements Runnable {
    	private String pName;
    	
    	public DelayedIpRemoval(String pName) {
    		this.pName = pName;
    	}
    	
    	public void run() {
    		// only remove offline player - in case of the player re-joining server
    		if (Bukkit.getServer().getPlayer(this.pName) == null) {
    			CommandsEX.playerIPs.remove(this.pName.toLowerCase());
    		}
    	}
    }
	
	/***
	 * When a player leaves the server, their join timestamp and playtime is removed to free up memory.
	 * Playtime will get stored into database if the time since his joining is more than 45 seconds
	 * to prevent database overloading when player tries to spam logins/logouts.
	 * @param e
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void pQuit(PlayerQuitEvent e) {
		String pName = e.getPlayer().getName();
		Integer stamp = Utils.getUnixTimestamp(0L);

		// schedule player's IP removal
		CommandsEX.plugin.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new DelayedIpRemoval(pName), (20 * getConf().getInt("maxIPholdTime")));
		
		// save player's playtime
		if (sqlEnabled && joinTimes.containsKey(pName)) {
			Integer played = (stamp - joinTimes.get(pName));
			if (played >= minTimeToSavePlayTime) {
				// player was online for more than minTimeToSavePlayTime seconds, count this visit
				if (playTimes.containsKey(pName) && (playTimes.get(pName) > -1)) {
					// update playtime directly if we have previous time loaded
					playTimes.put(pName, (playTimes.get(pName) + played));
				} else {
					// get total playtime from database, since we don't have it loaded yet
					try {
						// first, reset the time, so we don't add to -1 later
						Integer pTime = 0;
						ResultSet res = SQLManager.query_res("SELECT seconds_played FROM " + SQLManager.prefix + "playtime WHERE player_name = ?", pName);
						while (res.next()) {
							pTime = res.getInt("seconds_played");
						}
						res.close();
						playTimes.put(pName, (pTime + played));
					} catch (Throwable ex) {
						// something went wrong...
						LogHelper.logSevere("[CommandsEX] " + _("dbTotalPlayTimeGetError", ""));
						LogHelper.logDebug("Message: " + ex.getMessage() + ", cause: " + ex.getCause());
					}
				}
				// update DB with new value
				played = playTimes.get(pName);
				SQLManager.query("INSERT "+ (SQLManager.sqlType.equals("mysql") ? "" : "OR REPLACE ") +"INTO " + SQLManager.prefix + "playtime VALUES (?, ?)"+ (SQLManager.sqlType.equals("mysql") ? " ON DUPLICATE KEY UPDATE seconds_played = VALUES(seconds_played)" : ""), pName, played);
			}
			joinTimes.remove(pName);
			playTimes.remove(pName);
		}
	}
}