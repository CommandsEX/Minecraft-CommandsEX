package com.github.zathrus_writer.commandsex.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.FileListHelper;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.ReplacementPair;
import com.github.zathrus_writer.commandsex.listeners.PlayerCommandListener;

public class Handler_replaceplayercommand {

	public static List<ReplacementPair> pairs = new ArrayList<ReplacementPair>();
	
	/***
	 * Tells our main class which function we want to execute on PlayerChatEvent
	 * and loads existing chat replacements from the config file.
	 * @param plugin
	 */
	public static void init(CommandsEX plugin) {
		PlayerCommandListener.plugin.addEvent("normal", "replaceplayercommand", "replaceCommand");

		// load replacement values from config file
		File playerCommandsFile = new File(plugin.getDataFolder(), plugin.getConfig().getString("playerCommandsReplaceFile"));
		FileListHelper.checkListFile(playerCommandsFile, "playercmd.txt");
		pairs = FileListHelper.loadListFromFile(playerCommandsFile);
	}
	
	public static void addReplacementPair(ReplacementPair pair) {
		pairs.add(pair);
	}
	
	public static void clearReplacementPairs() {
		pairs.clear();	
	}
	
	/***
	 * The main function which replaces chat with matched replacements.
	 * @param e
	 * @return
	 */
	public static void replaceCommand(PlayerCommandPreprocessEvent e) {
		for (ReplacementPair rp : pairs) {
			Matcher m = rp.getRegex().matcher(e.getMessage().substring(1));
			if (m.matches()){
				String rps = m.replaceFirst(replacementString(rp.getReplacement(), e));
				e.setCancelled(true); 
				LogHelper.logDebug("[CommandsEX] "+e.getPlayer().getName()+": "+e.getMessage()+" ==> "+rps);
				e.getPlayer().performCommand(rps);
				return;
			}
		}
	}
	
	private static String replacementString(String rep, PlayerCommandPreprocessEvent e){
		return rep.replaceAll("\\$p", e.getPlayer().getName());
	}
}
