package com.github.zathrus_writer.commandsex.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;

public class Utils {

	// used for simple checking of commands spamming
	public transient static Map<String, Integer> lastCommandUsage = new HashMap<String, Integer>();
	protected static Boolean spamCleanupStarted = false;
	private static Integer spamCleanupPeriod = 5; // number of minutes to repeat the spam cleanup task after
	
	/***
	 * Converts given timestamp in milliseconds to Unix Timestamp in seconds.
	 * @param i
	 * @return
	 */
	public static Integer getUnixTimestamp(Long i) {
		if (i == 0) {
			i = System.currentTimeMillis();
		}
		
		return (int) (i / 1000L);
	}
	
	/**
	* Method to join array elements of type string
	* @author Hendrik Will, imwill.com
	* @param inputArray Array which contains strings
	* @param glueString String between each array element
	* @return String containing all array elements seperated by glue string
	*/
	public static String implode(Object[] inputArray, String glueString) {
		/** Output variable */
		String output = "";

		if (inputArray.length > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(inputArray[0]);

			for (int i=1; i<inputArray.length; i++) {
				sb.append(glueString);
				sb.append(inputArray[i]);
			}
			output = sb.toString();
		}
		return output;
	}
	
	/**
	* Method to join list elements of type string
	* @author Hendrik Will, imwill.com, updated by Zathrus_Writer
	* @param inputArray List<String> which contains strings
	* @param glueString String between each array element
	* @return String containing all array elements seperated by glue string
	*/
	public static String implode(List<Object> listInputArray, String glueString) {
		/** Output variable */
		Object[] inputArray = listInputArray.toArray();
		return implode(inputArray, glueString);
	}
	
	/***
	 * Checks if the given commandName was not executed within last minTimeout[0] seconds by given playerName.
	 * Returns true if the player tried to spam command too frequently, false otherwise.
	 * @param playerName
	 * @param commandName
	 * @param minTimeout
	 * @return
	 */
	public static Boolean checkCommandSpam(Player player, String commandName, Integer... minTimeout) {
		// bypass checking if a player has the right permissions
		if (player.hasPermission("cex.allowspamcommands")) return false;

		// first of all, create a periodic cleanup task that will remove all players
		// that are not logged-in anymore from the spam checking map to free up memory
		if (!spamCleanupStarted) {
			CommandsEX.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(CommandsEX.plugin, new Runnable() {
				@Override
			    public void run() {
			        // iterate through the HashMap and save all offline players that should be removed
					Iterator<Entry<String, Integer>> it = Utils.lastCommandUsage.entrySet().iterator();
					List<String> toRem = new ArrayList<String>();
					while (it.hasNext()) {
						Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>)it.next();
						Player p = Bukkit.getServer().getPlayer(pairs.getKey());
						if (p == null) {
							toRem.add(pairs.getKey());
						}
						//it.remove(); // avoids a ConcurrentModificationException - not needed in our case and will clear out HashMap!
					}
					
					// remove those offline players
					if (toRem.size() > 0) {
						for (String s : toRem) {
							Utils.lastCommandUsage.remove(s);
						}
					}
			    }
			}, (20 * spamCleanupPeriod * 60), (20 * spamCleanupPeriod * 60));
			spamCleanupStarted = true;
		}
		
		// load minimum cooldown value from config if not set
		Integer t = 0;
		if (minTimeout.length == 0) {
			t = CommandsEX.getConf().getInt("commandCooldownTime");
		} else {
			t = minTimeout[0];
		}

		String playerName = player.getName();
		Boolean ret = false;
		Integer stamp = getUnixTimestamp(0L);
		if (lastCommandUsage.containsKey(playerName + "-" + commandName)) {
			ret = ((stamp - lastCommandUsage.get(playerName + "-" + commandName)) < t);
		}

		// if the player is spamming the command, tell him
		LogHelper.showInfo("dontSpamCommand", player);
		
		// update last command time, then return result
		lastCommandUsage.put(playerName + "-" + commandName, stamp);
		return ret;
	}
}
