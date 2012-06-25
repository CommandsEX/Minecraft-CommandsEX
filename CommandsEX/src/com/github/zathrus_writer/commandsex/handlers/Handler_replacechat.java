package com.github.zathrus_writer.commandsex.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.FileListHelper;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.scripting.ReplacementPair;
import com.github.zathrus_writer.commandsex.helpers.scripting.ScriptEnvironment;

import static com.github.zathrus_writer.commandsex.Language._;

public class Handler_replacechat implements Listener {

	public static List<ReplacementPair> pairs = new ArrayList<ReplacementPair>();
	public static boolean allUpper = true;
	
	/***
	 * Loads existing chat replacements from the config file and activates event listeners.
	 */
	public Handler_replacechat() {
		// load replacement values from config file
		File playerChatFile = new File(CommandsEX.plugin.getDataFolder(), CommandsEX.getConf().getString("chatReplaceFile"));
		FileListHelper.checkListFile(playerChatFile, "playerchat.txt");
		addReplacementPairs(FileListHelper.loadListFromFile(playerChatFile, FileListHelper.MatchingContext.Chat));
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
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void replaceChat(PlayerChatEvent e) {
		if (e.isCancelled()) return;
		
		try {
			ScriptEnvironment env = new ScriptEnvironment(); {
				env.setCommandSender(e.getPlayer());
				env.setServer(e.getPlayer().getServer());
			}
			ArrayList<ReplacementPair> preparedEffects = new ArrayList<ReplacementPair>(); //holds all effects until all replacements done

			for (ReplacementPair rp : pairs) {
				StringBuffer sb = new StringBuffer();
				Matcher m = rp.getRegex().matcher(e.getMessage());
				if (!m.find()) continue;
				env.setMatcher(m);

				if (rp.playerWillVanish()) { //the player will vanish as a result of this, special handling
					int cutlen = CommandsEX.getConf().getInt("replacements.cutoff.length", 1);
					String cuttext = CommandsEX.getConf().getString("replacements.cutoff.indicator", "--*");
	
					String rep = m.group().substring(0, cutlen).concat(cuttext);
					m.appendReplacement(sb, rep);
					e.setMessage(sb.toString());
					//e.setCancelled(true);
					//e.getPlayer().chat(sb.toString()); //chat first
	
					rp.executeEffects(env); //then execute the replacement
					return;
				}

				//loop through with find/replace
				do { //use do while, due to the find() invocation above
					// test if it is all upper, and replace with all upper (if we have this set up in the regex itself - in config file)
					if (rp.getSameOutputCase() && allUpper && m.group().toUpperCase().equals(m.group())) {
						m.appendReplacement(sb, rp.executeString(env).toUpperCase());
					} else {
						m.appendReplacement(sb, rp.executeString(env));
					}
				} while (m.find());
				m.appendTail(sb);

				if (!preparedEffects.contains(rp)) {
					preparedEffects.add(rp);
				}
				e.setMessage(sb.toString());
			}
			
			//after all replacements are in: execute the effects
			if (!preparedEffects.isEmpty()) {
				//e.setCancelled(true);
				//e.getPlayer().chat(sb.toString()); //chat first
	
				env.setMatcher(null);
				for (ReplacementPair rp : preparedEffects){
					rp.executeEffects(env);
				}
			}
		} catch (Exception ex){
			LogHelper.logSevere("[CommandsEX] " + _("cmdOrChatreplacementFailed", ""));
			LogHelper.logDebug("Message: " + ex.getMessage() + ", cause: " + ex.getCause());
		}
	}
	
}
