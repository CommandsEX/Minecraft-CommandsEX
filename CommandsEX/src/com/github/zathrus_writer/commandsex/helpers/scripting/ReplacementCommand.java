package com.github.zathrus_writer.commandsex.helpers.scripting;

import java.util.Properties;
import java.util.regex.PatternSyntaxException;

/*** 
 * @author tustin2121, Commander plugin (http://dev.bukkit.org/server-mods/commander)
 */
public class ReplacementCommand extends ReplacementPair {
	private ScriptLine script;
	private Boolean cutoff = false;
	
	public ReplacementCommand(String regex, String replacement, String options, Boolean caseSensitive, Boolean sameOutputCase) throws PatternSyntaxException {
		super(regex, caseSensitive, sameOutputCase);
		script = new ScriptLine(replacement);
		
		if (options != null && !options.isEmpty()){
			Properties p = parseOpts(options);
			cutoff = Boolean.parseBoolean(p.getProperty("cutoff", "false"));
		//	CommanderPlugin.Log.info("["+options+"] cutoff = "+cutoff);
		}
	}
	
	public ReplacementCommand(String regex, String replacement) {
		this(regex, replacement, null, false, false);
	}

	public String predicateString() { return "==[cmd]==> "+replacement; }

	@Override public boolean playerWillVanish() {
		return cutoff;
	}

	@Override public void executeEffects(ScriptEnvironment e) {
		script.execute(e);
	}

	@Override public String executeString(ScriptEnvironment e) {
		return "$0";
	}
}
