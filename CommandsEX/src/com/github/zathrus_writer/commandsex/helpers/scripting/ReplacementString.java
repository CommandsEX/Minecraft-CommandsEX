package com.github.zathrus_writer.commandsex.helpers.scripting;

import java.util.regex.PatternSyntaxException;

/*** 
 * @author tustin2121, Commander plugin (http://dev.bukkit.org/server-mods/commander)
 */
public class ReplacementString extends ReplacementPair {
	
	public ReplacementString(String regex, String replacement, String options, Boolean caseSensitive, Boolean sameOutputCase) throws PatternSyntaxException {
		super(regex, caseSensitive, sameOutputCase);
		this.replacement = replacement;
	}
	
	@Override public void executeEffects(ScriptEnvironment e) {
	//	String command = e.getMatcher().replaceFirst(this.executeString(e));
	//	e.getServer().dispatchCommand(e.getCommandSender(), command);
	}

	@Override public String executeString(ScriptEnvironment e) {
		return e.substituteTokens(replacement);
	}
}
