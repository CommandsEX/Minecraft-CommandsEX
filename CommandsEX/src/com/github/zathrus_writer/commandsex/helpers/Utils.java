package com.github.zathrus_writer.commandsex.helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftServer;
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
	public static String implode(List<?> listInputArray, String glueString) {
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
		if (Permissions.checkPermEx(player, "cex.allowspamcommands")) return false;

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

		if (ret) {
			// if the player is spamming the command, tell him
			LogHelper.showInfo("dontSpamCommand", player);
		}
		
		// update last command time, then return result
		lastCommandUsage.put(playerName + "-" + commandName, stamp);
		return ret;
	}

	/***
	 * Returns parsed time value from a command in this format: (<days, 10> <hours, 3> <minutes, 24> <seconds, 11>)
	 * @param args
	 * @return
	 * @throws PatternSyntaxException
	 */
	public static Map<String, Integer> parseTime(String[] args) throws PatternSyntaxException, Throwable {
		Boolean dateNotFound = true;
		Map<String, Integer> ret = new HashMap<String, Integer>();
		// check for all arguments that start with t: and save them into return variable
		for (String s : args) {
			if (s.startsWith("t:")) {
				dateNotFound = false;
				Pattern Regex = Pattern.compile("(\\d+)(weeks?|w|days?|d|hours?|hrs?|h|minutes?|mins?|m|seconds?|secs?|s)",
						Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
					Matcher RegexMatcher = Regex.matcher(s.substring(2, s.length()));
					while (RegexMatcher.find()) {
						Integer v;
						if (RegexMatcher.group(1).matches(CommandsEX.intRegex)) {
							v = Integer.parseInt(RegexMatcher.group(1));
						} else {
							v = 0;
						}
						
						if (RegexMatcher.group(2).startsWith("w")) {
							ret.put("weeks", v);
						} else if (RegexMatcher.group(2).startsWith("d")) {
							ret.put("days", v);
						} else if (RegexMatcher.group(2).startsWith("h")) {
							ret.put("hours", v);
						} else if (RegexMatcher.group(2).startsWith("m")) {
							ret.put("minutes", v);
						} else if (RegexMatcher.group(2).startsWith("s")) {
							ret.put("seconds", v);
						} else {
							throw new Throwable("Invalid time parameter: " + RegexMatcher.group(2));
						}

						//for (int i = 1; i < RegexMatcher.groupCount(); i++) {
							// RegexMatcher.group(i); RegexMatcher.start(i); RegexMatcher.end(i);
						//}
					}
			}
		}

		// add not found value into the output, so we can check agains it
		if (dateNotFound) {
			ret.put("not_found", 1);
		}
		
		// fill NULL values with zeroes
		if (ret.get("weeks") == null) {
			ret.put("weeks", 0);
		}
		
		if (ret.get("days") == null) {
			ret.put("days", 0);
		}
		
		if (ret.get("hours") == null) {
			ret.put("hours", 0);
		}
		
		if (ret.get("minutes") == null) {
			ret.put("minutes", 0);
		}
		
		if (ret.get("seconds") == null) {
			ret.put("seconds", 0);
		}
		
		return ret;
	}
	
	/***
	 * Parses given Unix Timestamp and returns hashmap with days, hours, minutes and seconds.
	 * @param stamp
	 * @return
	 */
	public static Map<String, Integer> parseTimeStamp(Long stamp) {
		Map<String, Integer> m = new HashMap<String, Integer>();
		Integer weeks = (int) Math.floor(stamp / 604800);
		Integer days = (int) Math.floor(stamp / 86400);
		Integer hours = (int) Math.floor((stamp - (days * 86400)) / 3600);
		Integer minutes = (int) Math.floor((stamp - (days * 86400) - (hours * 3600)) / 60);
		Integer seconds = (int) (stamp - (days * 86400) - (hours * 3600) - (minutes * 60));
		
		m.put("weeks", weeks);
		m.put("days", days);
		m.put("hours", hours);
		m.put("minutes", minutes);
		m.put("seconds", seconds);
		
		return m;
	}
	
	public static Map<String, Integer> parseTimeStamp(Integer stamp) {
		return parseTimeStamp(new Long(stamp));
	}
	
	/***
	 * Replaces all chat color codes in the given text by their real ChatColor counterparts.
	 */
	public static String replaceChatColors(String s) {
		ChatColor[] cc = ChatColor.values();
		
		for (ChatColor c : cc) {
			s = s.replaceAll("&" + c.getChar(), ChatColor.getByChar(c.getChar()) + "");
		}
		
		return s;
	}
	
	/***
	 * Gets the closest material matches  and returns them
	 * @author iKeirNez
	 * @param input
	 * @return
	 */
	public static List<Material> closestMatches(String input) {
        ArrayList<Material> matches = new ArrayList<Material>();
        
        for (Material mat : Material.values()){
        	// Quick fix for stone brick
        	if (input.equalsIgnoreCase("stonebrick")){
        		return Arrays.asList(Material.SMOOTH_BRICK);
        	} else if ((mat.name().replace("_", "").toLowerCase().equals(input.toLowerCase()) || String.valueOf(mat.getId()).equals(input))){
        		return Arrays.asList(mat);
            } else if (mat.name().replace("_", "").toLowerCase().contains(input.toLowerCase())){
            	matches.add(mat);
            }
        }
        
        return matches;
    }
	
	/***
	 * Takes a value e.g. a material name and makes it look more user friendly. E.g. GLASS_PANE would become Glass Pane
	 */
	
	public static String userFriendlyNames(String name){
		String formattedName = WordUtils.capitalize(name.replaceAll("_", " ").toLowerCase());
		return formattedName;
	}
	
	/***
	 * Allows you to use functions for an offline player like they where an online player
	 * By creating a fake entity
	 * @param player
	 * @return
	 */
	public static Player getOfflinePlayer(String player) {
		// Taken from the OpenInv Source
		// https://github.com/lishd/OpenInv/blob/master/src/lishid/openinv/commands/OpenInvPluginCommand.java#L106
		Player player2 = null;
		try {
			File playerfolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "players");
			for (File playerfile : playerfolder.listFiles()) {
				String filename = playerfile.getName();
				String playername = filename.substring(0, filename.length() - 4);

				if (playername.trim().equalsIgnoreCase(player)) {
					final MinecraftServer server = ((CraftServer) CommandsEX.plugin.getServer()).getServer();
					final EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), playername, new ItemInWorldManager(server.getWorldServer(0)));
					player2 = (entity == null) ? null : (Player) entity.getBukkitEntity();
					if (player2 != null) {
						player2.loadData();
					} else {
						
					}
				}
			}
		} catch (final Exception e) {
			// Exception :0
		}
		return player2;
	}
}