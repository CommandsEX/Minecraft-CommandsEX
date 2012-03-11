package com.github.zathrus_writer.commandsex.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.bukkit.event.server.ServerCommandEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.FileListHelper;
import com.github.zathrus_writer.commandsex.helpers.ReplacementPair;
import com.github.zathrus_writer.commandsex.listeners.ServerCommandListener;

public class Handler_replaceconsolecommand {

	public static List<ReplacementPair> pairs = new ArrayList<ReplacementPair>();
	
	/***
	 * Tells our main class which function we want to execute on PlayerChatEvent
	 * and loads existing chat replacements from the config file.
	 * @param plugin
	 */
	public static void init(CommandsEX plugin) {
		ServerCommandListener.plugin.addEvent("normal", "replaceconsolecommand", "replaceCommand");

		// load replacement values from config file
		File playerCommandsFile = new File(plugin.getDataFolder(), plugin.getConfig().getString("consoleCommandsReplaceFile"));
		FileListHelper.checkListFile(playerCommandsFile, "consolecmd.txt");
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
	public static void replaceCommand(ServerCommandEvent e) {
		for (ReplacementPair rp : pairs) {
			Matcher m = rp.getRegex().matcher(e.getCommand());
			if (m.matches()){
				e.setCommand(m.replaceFirst(rp.getReplacement()));
				return;
			}
		}
	}
}
