package com.github.zathrus_writer.commandsex.helpers.scripting;

import java.util.ArrayList;

import com.github.zathrus_writer.commandsex.helpers.LogHelper;

/*** 
 * @author tustin2121, Commander plugin (http://dev.bukkit.org/server-mods/commander)
 */
public class ScriptBlock implements Executable {
	private ArrayList<Executable> commands;
	private String alias;

	public ScriptBlock(ArrayList<String> sl) throws BadScriptException{
		this(sl.toArray(new String[sl.size()]));
	}

	public ScriptBlock(ArrayList<String> sl, String alias) throws BadScriptException{
		this(sl.toArray(new String[sl.size()]));
		this.alias = alias;
	}

	public ScriptBlock(String[] scriptLines, String alias) throws BadScriptException {
		this(scriptLines);
		this.alias = alias;
	}

	public ScriptBlock(String[] scriptLines) throws BadScriptException {
		commands = new ArrayList<Executable>();

		int bracecount = 0;
		ArrayList<String> workingSubblock = null;
		for (String line : scriptLines){ //parse
			line = line.trim();
			if (line.equals("{")){ //block brace
				if (bracecount++ == 0){ //post increment
					workingSubblock = new ArrayList<String>();
				}
			} else if (line.equals("}")){
				if (--bracecount == 0){ //pre increment
					ScriptBlock sb = new ScriptBlock(workingSubblock);
					commands.add(sb);
					workingSubblock = null;
				}
				if (bracecount < 0) throw new BadScriptException("Error parsing script! Unbalanced braces!");
			} else {


				if (bracecount == 0){
					ScriptLine sl = new ScriptLine(line);
					commands.add(sl);
				} else {
					workingSubblock.add(line);
				}

			}
		}
	}

	public String getAlias() {return alias;}
	public void setAlias(String alias) {this.alias = alias;}

	@Override public String toString() {
		return "Script[alias="+alias+",lines="+commands.size()+"]";
	}

	@Override public void execute(ScriptEnvironment env) {
		LogHelper.logDebug("[CommandsEX:DEBUG:startScript] ");
		for (Executable ex : commands){
			ex.execute(env);
		}
		LogHelper.logDebug("[CommandsEX:DEBUG:endScript] ");
	}
}
