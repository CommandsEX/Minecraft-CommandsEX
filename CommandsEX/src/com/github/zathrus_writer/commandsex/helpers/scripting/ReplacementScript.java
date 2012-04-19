package com.github.zathrus_writer.commandsex.helpers.scripting;

import java.util.regex.PatternSyntaxException;

/*** 
 * @author tustin2121, Commander plugin (http://dev.bukkit.org/server-mods/commander)
 */
public class ReplacementScript extends ReplacementPair {
	private String scriptAlias;
	private ScriptBlock block;
	
	public ReplacementScript(String regex, ScriptBlock block, String options, Boolean caseSensitive, Boolean sameOutputCase) throws PatternSyntaxException {
		super(regex, caseSensitive, sameOutputCase);
		this.block = block;
		this.scriptAlias = regex;
	}
	
	public ReplacementScript(String regex, ScriptBlock block, String alias, String options, Boolean caseSensitive, Boolean sameOutputCase) {
		super(regex, caseSensitive, sameOutputCase);
		this.block = block;
		this.scriptAlias = alias;
	}

	public String predicateString() { return "==[script]==> { "+scriptAlias+" }"; }

	@Override public void executeEffects(ScriptEnvironment e) {
		block.execute(e);
	}

	@Override public String executeString(ScriptEnvironment e) {
		return "";
	}
}
