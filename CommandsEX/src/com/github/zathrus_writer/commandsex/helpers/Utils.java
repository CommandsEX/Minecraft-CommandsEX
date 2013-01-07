package com.github.zathrus_writer.commandsex.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
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
			CommandsEX.plugin.getServer().getScheduler().runTaskTimerAsynchronously(CommandsEX.plugin, new Runnable() {
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
	 * Returns parsed time value from a command in this format: (<years, 1> <months, 0> <days, 10> <hours, 3> <minutes, 24> <seconds, 11>)
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
				Pattern Regex = Pattern.compile("(\\d+)(years?|y|months?|m|weeks?|w|days?|d|hours?|hrs?|h|minutes?|mins?|m|seconds?|secs?|s)",
						Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
					Matcher RegexMatcher = Regex.matcher(s.substring(2, s.length()));
					while (RegexMatcher.find()) {
						Integer v;
						if (RegexMatcher.group(1).matches(CommandsEX.intRegex)) {
							v = Integer.parseInt(RegexMatcher.group(1));
						} else {
							v = 0;
						}
						
						if (RegexMatcher.group(2).startsWith("y")){
							ret.put("years", v);
						} else if (RegexMatcher.group(2).startsWith("m")){
							ret.put("months", v);
						} else if (RegexMatcher.group(2).startsWith("w")) {
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
		if (ret.get("years") == null){
			ret.put("years", 0);
		}
		
		if (ret.get("months") == null){
			ret.put("months", 0);
		}
		
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
	 * Parses given Unix Timestamp and returns hashmap with years, months, days, hours, minutes and seconds.
	 * @param stamp
	 * @return
	 */
	public static Map<String, Integer> parseTimeStamp(Long stamp) {
		Map<String, Integer> m = new HashMap<String, Integer>();
		Integer years = (int) Math.floor(stamp / 29030400);
		Integer months = (int) Math.floor((stamp - (years * 29030400)) / 2419200);
		Integer weeks = (int) Math.floor((stamp - (months * 2419200)) / 604800);
		Integer days = (int) Math.floor((stamp - (weeks * 604800)) / 86400);
		Integer hours = (int) Math.floor((stamp - (days * 86400)) / 3600);
		Integer minutes = (int) Math.floor((stamp - (days * 86400) - (hours * 3600)) / 60);
		Integer seconds = (int) (stamp - (days * 86400) - (hours * 3600) - (minutes * 60));
		
		m.put("years", years);
		m.put("months", months);
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
		for (ChatColor c : ChatColor.values()) {
			s = s.replaceAll("&" + c.getChar(), ChatColor.getByChar(c.getChar()) + "");
		}
		
		return s;
	}
	
	/***
	 * Checks a string for any chat colors, useful for restricting players from using colors
	 * @param s
	 * @return
	 */
	
	public static boolean hasChatColor(String s) {
		boolean hasChatColor = false;
		
		for (ChatColor c : ChatColor.values()){
			if (s.contains("&" + c.getChar())){
				hasChatColor = true;
			}
		}
		
		return hasChatColor;
	}
	
	/***
	 * Takes a value e.g. a material name and makes it look more user friendly. E.g. GLASS_PANE would become Glass Pane
	 */
	
	public static String userFriendlyNames(String name){
		String formattedName = WordUtils.capitalize(name.replaceAll("_", " ").toLowerCase());
		return formattedName;
	}
	
	/***
	 * Converts seconds to a HH:MM:SS format
	 * @author iKeirNez
	 * @param currentTime
	 * @return
	 */
	
	public static String convertToHHMMSS(long currentTime, boolean seconds){
		long hours = currentTime / 3600;
		long remainder = currentTime % 3600;
		long minutes = remainder / 60;
		long seconds1 = remainder % 60;
		String string = (hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes + ":" + (seconds ? (seconds1 < 10 ? "0" : "") + seconds1 : "");
		
		return string;
	}
	
	/***
	 * Parses a worlds time into realtime.
	 * @param time
	 * @return
	 */
	
	public static String parseTime(long time){
        long gameTime = time;
        long hours = gameTime / 1000 + 6;
        long minutes = (gameTime % 1000) * 60 / 1000; 
        String ampm = "AM";
        if (hours >= 12)
        {
            hours -= 12; ampm = "PM"; 
        }
 
        if (hours >= 12)
        {
            hours -= 12; ampm = "AM"; 
        }
 
        if (hours == 0) hours = 12;
 
        String mm = "0" + minutes; 
        mm = mm.substring(mm.length() - 2, mm.length());
 
        return hours + ":" + mm + " " + ampm;
    }
	
	public static byte degreeToByte(float degree){
		return (byte) (int) ((int) degree * 256.0F / 360.0F);
	}
	
	public static String typeOfEntity(EntityType eType){
		String type = "none";
		EntityType[] passives = {
				EntityType.CHICKEN,
				EntityType.COW,
				EntityType.MUSHROOM_COW,
				EntityType.OCELOT,
				EntityType.PIG,
				EntityType.SHEEP,
				EntityType.WOLF,
				EntityType.SQUID,
				EntityType.VILLAGER,
				EntityType.SNOWMAN,
				EntityType.IRON_GOLEM
			};
		
		EntityType[] aggressives = {
				EntityType.BLAZE,
				EntityType.CAVE_SPIDER,
				EntityType.CREEPER,
				EntityType.ENDER_DRAGON,
				EntityType.ENDERMAN,
				EntityType.GHAST,
				EntityType.GIANT,
				EntityType.MAGMA_CUBE,
				EntityType.PIG_ZOMBIE,
				EntityType.SILVERFISH,
				EntityType.SKELETON,
				EntityType.SLIME,
				EntityType.SPIDER,
				EntityType.ZOMBIE
			};
		
		for (EntityType et : passives){
			if (eType == et){
				type = "passive";
			}
		}
		
		if (type.equals("none")){
			for (EntityType et : aggressives){
				if (eType == et){
					type = "aggressive";
				}
			}
		}
		
		return type;
	}
	
	/***
	 * Splits a comma separated string list into a List<String>
	 * @param s
	 * @return
	 */
	
	public static List<String> separateCommaList(String s){
		return Arrays.asList(s.split("\\s*,\\s*"));
	}
	
	/**
	 * Collects all arguments into a string
	 * @param args
	 * @return
	 */
	
	public static String collectArgs(String[] args){
		return collectArgs(args, 0);
	}
	
	/**
	 * Collects all arguments into a string
	 * @param args
	 * @param startAt
	 * @return
	 */
	
	public static String collectArgs(String[] args, int startAt){
		StringBuilder sb = new StringBuilder();
		for (int i = startAt; i < args.length; i++){
			sb.append(args[i]).append(" ");
		}
		
		return sb.toString().trim();
	}
	
	/**
	 * Gets all keys matching a value
	 * @param hm
	 * @param value
	 * @return
	 */
	
	public static List<Object> getKeysFromValue(Map<?, ?> hm, Object value){
		List <Object>list = new ArrayList<Object>();
		for(Object o:hm.keySet()){
			if(hm.get(o).equals(value)) {
				list.add(o);
			}
		}
		return list;
	}

	public static String twoDecimalPlaces(double input){
		DecimalFormat df = new DecimalFormat("####0.00");
		return df.format(input);
	}
	
	/**
	 * Shortens a URL using v.gd
	 * @param urlToShorten
	 * @return 
	 */
	public static URL shortenURL(URL urlToShorten) throws IOException {
		URL vGD = new URL("http://v.gd/create.php?format=simple&url=" + urlToShorten.getPath());
		URLConnection con = vGD.openConnection();
		BufferedReader content = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String shortened;
        while ((shortened = content.readLine()) != null) {
           	content.close();
           	return new URL(shortened);
        }
		return null;
	}
	
	/**
	 * Returns the current time
	 * @return
	 */
	public static String getTime() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime());
	}
	
	/**
	 * Converts a List to a List<String>
	 */
	
	public static List<String> noGenericTypeToStringType(@SuppressWarnings("rawtypes") List list){
		List<String> toReturn = new ArrayList<String>();
		for (Object o : list){
			toReturn.add(o.toString());
		}
		
		return toReturn;
	}
	
}
