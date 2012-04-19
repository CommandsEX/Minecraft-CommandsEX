package com.github.zathrus_writer.commandsex.helpers.scripting;

import java.util.Random;
import java.util.regex.PatternSyntaxException;

/*** 
 * @author tustin2121, Commander plugin (http://dev.bukkit.org/server-mods/commander)
 */
public class ReplacementRandom extends ReplacementPair {
	private static final Random random = new Random();
	private String[] replacementArray;
	
	public ReplacementRandom(String regex, String replacement, Boolean caseSensitive, Boolean sameOutputCase) throws PatternSyntaxException {
		super(regex, caseSensitive, sameOutputCase);
		this.replacement = replacement;
		this.replacementArray = replacement.trim().split(";");
	}
	
	@Override public String predicateString() {
		return "==> one of ("+replacement+")";
	}

	@Override public void executeEffects(ScriptEnvironment e) {}

	@Override public String executeString(ScriptEnvironment e) {
		int rnd = random.nextInt(replacementArray.length);
		return replacementArray[rnd];
	}
}
