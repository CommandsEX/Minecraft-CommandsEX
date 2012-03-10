package com.github.zathrus_writer.commandsex;


import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.github.zathrus_writer.commandsex.helpers.LogHelper;

public class Language {
	public transient static String defaultLocale; // the default locale from config
	public static Map<String, ResourceBundle> langs = new HashMap<String, ResourceBundle>(); // ["en", English bundle] .. OR .. ["en_us", English bundle]
	public static Map<String, String> perUserLocale = new HashMap<String, String>(); // ["Zathrus_Writer", "en_us"]
	private static Boolean perUserDataLoaded = false;
	private static CommandsEX plugin;
	
	public static void init(CommandsEX plug) {
		plugin = plug;
		
		// try to set a default locale from our config file, otherwise go by English
		// ... first try custom file from our plugin's data folder
		defaultLocale = plugin.getConfig().getString("defaultLang").toLowerCase();
		try {
			if (defaultLocale.contains("_")) {
				String[] localeSplit = defaultLocale.split("_");
				langs.put(defaultLocale, ResourceBundle.getBundle("lang", new Locale(localeSplit[0], localeSplit[1]), new FileResClassLoader(CommandsEX.class.getClassLoader(), plugin)));
			} else {
				langs.put(defaultLocale, ResourceBundle.getBundle("lang", new Locale(defaultLocale), new FileResClassLoader(CommandsEX.class.getClassLoader(), plugin)));
			}
		} catch (MissingResourceException r) {
			// custom file not found, try internals
			try {
				if (defaultLocale.contains("_")) {
					String[] localeSplit = defaultLocale.split("_");
					langs.put(defaultLocale, ResourceBundle.getBundle("lang", new Locale(localeSplit[0], localeSplit[1])));
				} else {
					langs.put(defaultLocale, ResourceBundle.getBundle("lang", new Locale(defaultLocale)));
				}
			} catch (Exception e) {
				// internal nor custom file found, revert to default and reset config variable
				plugin.getConfig().set("defaultLang", "en");
				plugin.saveConfig();
				LogHelper.logWarning("Unable to load locale " + defaultLocale + ", trying English");
				// something went wrong, load the default English locale
				defaultLocale = "en";
				langs.put(defaultLocale, ResourceBundle.getBundle("lang", Locale.ENGLISH));
			}
		} catch (Exception e) {
			// something strange happened, revert to default and reset config variable
			plugin.getConfig().set("defaultLang", "en");
			plugin.saveConfig();
			LogHelper.logSevere("[CommandsEX] Unable to load locale " + defaultLocale + ", trying English");
			// something went wrong, load the default English locale
			defaultLocale = "en";
			langs.put(defaultLocale, ResourceBundle.getBundle("lang", Locale.ENGLISH));
		}
	}
	
	/***
	 * Translates a given string into either default locale or player's set locale.
	 * @param s
	 * @param player
	 * @return
	 */
	public static String _(String s, final String playerName) {
		String loc = defaultLocale;

		// if we don't have per-user data loaded from DB, do it now
		if (!perUserDataLoaded && !CommandsEX.avoidDB && CommandsEX.sqlEnabled) {
			try {
				ResultSet res = SQLManager.query_res("SELECT * FROM " + SQLManager.prefix + "user2lang");
				while (res.next()) {
					perUserLocale.put(res.getString("username"), res.getString("lang"));
				}
				res.close();
			} catch (Exception e) {
				CommandsEX.avoidDB = true;
				LogHelper.logSevere("[CommandsEX] " + _("dbReadError", ""));
				LogHelper.logDebug("SQL: SELECT * FROM " + SQLManager.prefix + "user2lang");
				LogHelper.logDebug("Message: " + e.getMessage());
			}
			perUserDataLoaded = true;
		}
		
		if (!playerName.equals("") && !playerName.toLowerCase().equals("console") && perUserLocale.containsKey(playerName)) {
			loc = perUserLocale.get(playerName);
		}

		// try to get a translation or failsafe with the same String as we get to translate
		try {
			// load the translation locale if not loaded yet
			if (!langs.containsKey(loc)) {
				try {
					if (loc.contains("_")) {
						String[] localeSplit = loc.split("_");
						langs.put(loc, ResourceBundle.getBundle("lang", new Locale(localeSplit[0], localeSplit[1]), new FileResClassLoader(CommandsEX.class.getClassLoader(), plugin)));
					} else {
						langs.put(loc, ResourceBundle.getBundle("lang", new Locale(loc), new FileResClassLoader(CommandsEX.class.getClassLoader(), plugin)));
					}
				} catch (MissingResourceException r) {
					// custom file not found, try internals
					try {
						if (loc.contains("_")) {
							String[] localeSplit = loc.split("_");
							langs.put(loc, ResourceBundle.getBundle("lang", new Locale(localeSplit[0], localeSplit[1])));
						} else {
							langs.put(loc, ResourceBundle.getBundle("lang", new Locale(loc)));
						}
					} catch (Exception e) {
						// internal nor custom file found, revert to default
						LogHelper.logWarning("Unable to load locale " + loc + ", trying English");
						// something went wrong, load the default English locale
						loc = "en";
						langs.put(defaultLocale, ResourceBundle.getBundle("lang", Locale.ENGLISH));
					}
				} catch (Exception e) {
					// we should not get here, but if we do... revert to default :-)
					LogHelper.logWarning("Unable to load locale " + loc + ", trying English");
					// something went wrong, load the default English locale
					loc = "en";
					langs.put(defaultLocale, ResourceBundle.getBundle("lang", Locale.ENGLISH));
				}
			}
			
			// translate
			s = langs.get(loc).getString(s);
		} catch (MissingResourceException ex) {
			LogHelper.logWarning("Missing translation of '" + s + "' for language '" + loc + "'");
		} catch (Exception e) {
			// unspecified bad fail, revert to English momentarily to prevent further bad fails
			plugin.getConfig().set("defaultLang", "en");
			plugin.saveConfig();
			defaultLocale = "en";
			LogHelper.logSevere("[CommandsEX] Translation failed for message '" + s + "', language '" + loc + "'");
			LogHelper.logDebug("Message: " + e.getMessage());
		}

		return s;
	}
}
