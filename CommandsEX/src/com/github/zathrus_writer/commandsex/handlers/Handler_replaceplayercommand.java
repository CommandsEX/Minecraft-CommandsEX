package com.github.zathrus_writer.commandsex.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.FileListHelper;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.ReplacementPair;

public class Handler_replaceplayercommand implements Listener {

	public static List<ReplacementPair> pairs = new ArrayList<ReplacementPair>();
	
	/***
	 * Tells our main class which function we want to execute on PlayerChatEvent
	 * and loads existing chat replacements from the config file.
	 * @param plugin
	 */
	public Handler_replaceplayercommand() {
		// load replacement values from config file
		File playerCommandsFile = new File(CommandsEX.plugin.getDataFolder(), CommandsEX.plugin.getConfig().getString("playerCommandsReplaceFile"));
		FileListHelper.checkListFile(playerCommandsFile, "playercmd.txt");
		pairs = FileListHelper.loadListFromFile(playerCommandsFile);
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
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
	@EventHandler(priority = EventPriority.LOWEST)
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
