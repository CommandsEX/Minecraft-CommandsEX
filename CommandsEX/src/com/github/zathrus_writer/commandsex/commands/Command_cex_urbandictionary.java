package com.github.zathrus_writer.commandsex.commands;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_urbandictionary {
	
	/***
	 * Urban Dictionary - Gets the definition of a word in the urban dictionary!
	 * @author Kezz101
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if(PlayerHelper.checkIsPlayer(sender) && !Utils.checkCommandSpam((Player)sender, "urbandictionary") && Permissions.checkPerms((Player)sender, "cex.urbandictionary")) {
			if(args.length == 1) {
				String def = null;
				try {
					HttpURLConnection url = (HttpURLConnection) new URL("http://urbanscraper.herokuapp.com/define/" + args[0] + ".json").openConnection();
					url.setConnectTimeout(10000);
					url.setReadTimeout(10000);
					url.connect();
					if(url.getErrorStream() == null) {
						def = convertStreamToString((InputStream)url.getContent());
					}
				} catch (Exception e) {
					LogHelper.showWarnings(sender, "urbanDictionaryError");
					return true;
				}
				Pattern pattern = Pattern.compile("\"definition\":\"(.*?)\",\"example\"");
				Matcher matcher = pattern.matcher(def);
				if (matcher.find()) {
				    def = matcher.group(1);
				    LogHelper.showInfo("urbanDictionaryDef#####[" + def, sender);
				} else {
					LogHelper.showWarnings(sender, "urbanDictionaryError");
				}
			} else {
				Commands.showCommandHelpAndUsage(sender, "cex_urbandictionary", alias);
			}
		}
		return true;
	}
	
	public static String convertStreamToString(java.io.InputStream is) {
	    try {
	        return new java.util.Scanner(is).useDelimiter("\\A").next();
	    } catch (java.util.NoSuchElementException e) {
	        return "";
	    }
	}

}
