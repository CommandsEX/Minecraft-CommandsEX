package com.github.zathrus_writer.commandsex.helpers.scripting;

import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/*** 
 * @author tustin2121, Commander plugin (http://dev.bukkit.org/server-mods/commander)
 */
public abstract class ReplacementPair {
	protected String regexString;
	protected Pattern regex;
	protected String replacement;
	protected Boolean caseSentitive = true;
	protected Boolean sameOutputCase = false;
	
	protected ReplacementPair(String regex, Boolean caseSensitive, Boolean sameOutputCase) throws PatternSyntaxException {
		if (!caseSensitive) {
			this.regex = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		} else {
			this.regex = Pattern.compile(regex);
		}
		this.regexString = regex;
		this.sameOutputCase = sameOutputCase;
		this.caseSentitive = caseSensitive;
	}
	
	public Pattern getRegex() {return regex;}
	public String getRegexString() {return regexString;}
	public String getReplacement() {return replacement;}
	public Boolean getCaseSensitive() {return caseSentitive;}
	public Boolean getSameOutputCase() {return sameOutputCase;}
	
	public void setRegexOptions(String regexOpts) {
		if (regexOpts == null || regexOpts.isEmpty()) return;

		char[] ro = regexOpts.toCharArray();
		boolean caseSensitive = false;
		boolean literal = false;

		for (int i = 0; i < ro.length; i++){
			switch(ro[i]){
			case 's': caseSensitive = true; break;
			case 'l': literal = true; break;
			}
		}

		//recompile pattern with the new options
		regex = Pattern.compile(regexString, 
					((caseSensitive)?0:Pattern.CASE_INSENSITIVE) |
					((literal)?Pattern.LITERAL:0)
				);
	}
	
	/** Performs effects of this replacement for replaced commands. 
	 * Execute in a command replacement context. */
	public abstract void executeEffects(ScriptEnvironment e);
	/** Retrieves a replacement string for insertion into chat. 
	 * Execute in a chat or string replacement context. */
	public abstract String executeString(ScriptEnvironment e);

	public boolean playerWillVanish() { return false; }
	public String predicateString() { return "==> "+replacement; }

	@Override public String toString() {
		return "ReplacementPair ["+regexString+"]";
	}
	@Override public int hashCode() {
		return regexString.hashCode();
	}

	public Properties parseOpts(String opts) {
		Properties p = new Properties();
		String[] kvpairs = opts.split(",");

		for (String kv : kvpairs){
			if (kv.contains("=")){
				String[] o = kv.split("=");
				p.setProperty(o[0], o[1]);
			} else {
				p.setProperty(kv, "true");
			}
		}

		return p;
	}
}
