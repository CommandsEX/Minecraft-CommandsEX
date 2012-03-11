package com.github.zathrus_writer.commandsex.helpers;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/*** 
 * @author tustin2121, Commander plugin (http://dev.bukkit.org/server-mods/commander)
 */
public class ReplacementPair {
	private String regexString;
	private Pattern regex;
	private String replacement;
	private Boolean caseSentitive = true;
	private Boolean sameOutputCase = false;
	
	public ReplacementPair(String regex, String replacement, Boolean caseSensitive, Boolean sameOutputCase) throws PatternSyntaxException {
		if (!caseSensitive) {
			this.regex = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		} else {
			this.regex = Pattern.compile(regex);
		}
		this.regexString = regex;
		this.replacement = replacement;
		this.sameOutputCase = sameOutputCase;
		this.caseSentitive = caseSensitive;
	}
	
	public Pattern getRegex() {return regex;}
	public String getRegexString() {return regexString;}
	public String getReplacement() {return replacement;}
	public Boolean getCaseSensitive() {return caseSentitive;}
	public Boolean getSameOutputCase() {return sameOutputCase;}
	
	
	@Override public String toString() {
		return regexString + " ==> "+replacement;
	}
	
	@Override public int hashCode() {
		return regexString.hashCode() + replacement.hashCode();
	}
}
