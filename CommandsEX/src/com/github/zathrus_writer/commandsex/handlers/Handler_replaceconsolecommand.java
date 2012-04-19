package com.github.zathrus_writer.commandsex.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.FileListHelper;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.scripting.ReplacementPair;
import com.github.zathrus_writer.commandsex.helpers.scripting.ScriptEnvironment;

import static com.github.zathrus_writer.commandsex.Language._;

public class Handler_replaceconsolecommand implements Listener {

	public static List<ReplacementPair> pairs = new ArrayList<ReplacementPair>();
	
	/***
	 * Tells our main class which function we want to execute on PlayerChatEvent
	 * and loads existing chat replacements from the config file.
	 * @param plugin
	 */
	public Handler_replaceconsolecommand() {
		// load replacement values from config file
		File playerCommandsFile = new File(CommandsEX.plugin.getDataFolder(), CommandsEX.plugin.getConfig().getString("consoleCommandsReplaceFile"));
		FileListHelper.checkListFile(playerCommandsFile, "consolecmd.txt");
		addReplacementPairs(FileListHelper.loadListFromFile(playerCommandsFile, FileListHelper.MatchingContext.Command));
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	
	public static void addReplacementPairs(List<ReplacementPair> pair) {
		for (ReplacementPair rp : pair) {
			pairs.add(rp);
		}
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
	public void replaceCommand(ServerCommandEvent e) {
		try {
			ScriptEnvironment env = new ScriptEnvironment(); {
				env.setCommandSender(e.getSender());
				env.setServer(e.getSender().getServer());
			}
			
			for (ReplacementPair rp : pairs) {
				Matcher m = rp.getRegex().matcher(e.getCommand());
				if (m.matches()){
					env.setMatcher(m);
					rp.executeEffects(env);
					e.setCommand("cex null"); //does nothing, prints nothing
					return;
				}
			}
		} catch (Exception ex){
			LogHelper.logSevere("[CommandsEX] " + _("cmdOrChatreplacementFailed", ""));
			LogHelper.logDebug("Message: " + ex.getMessage() + ", cause: " + ex.getCause());
		}
	}
}
