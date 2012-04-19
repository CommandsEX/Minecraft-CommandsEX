package com.github.zathrus_writer.commandsex.helpers.scripting;

import com.github.zathrus_writer.commandsex.helpers.LogHelper;

/*** 
 * @author tustin2121, Commander plugin (http://dev.bukkit.org/server-mods/commander)
 * 
 * TODO: make script lines abstract, and have a method that will make the line based on what it parses:
 *  if the line begins with [ then it is a construct:
 *     [if @var = literal] = if construct: if a variable equals something
 *     [!if @var = literal] = not if construct: if a variable is not equal to something
 *     [for @var in supportedCollection] = for each construct: for each in a supported collection (players, online players)
 *     [has "permission"] = has construct: if the current sender has the specified permission
 *     [!has "permission"] = has not contruct: if the current sender does not have the specified permission
 *     [switch @var] and [case #] = switch, on the variable
 *     [random # to #] and [case #] = same as switch, but with a random number 
 *  if the line begins with a @ then it is a variable assignment:
 *     @var = a number, string, or supported object with a name
 *  if the line begins with a ? then it is a scripting environment directive
 *     ?echo on/off = turns on/off echoing back messages from commands - the built-in echo command ignores it
 *  if the line begins with "say", 
 *  else:
 *     the line is a normal command to be executed by the current CommandSender, or mod if it begins with "sudo" 
 * @author timpittman
 */
public class ScriptLine implements Executable {
	
	public String cmd;
	public boolean modCommand;
	public static final CommanderCommandSender ccs = new CommanderCommandSender();

	public ScriptLine(String command) {
		if (command.trim().startsWith("sudo")) { //allow "sudo" commands to be run by the console
			modCommand = true;
			this.cmd = command.trim()
					.replaceFirst("(?<!\\\\)sudo", "").replaceFirst("\\sudo", "sudo"); //remove sudo, unless it has a \ in front
					//this is a negative look-back, see ReplacementString
		} else {
			this.cmd = command;
		}
		this.cmd = this.cmd.trim();
	}

	@Override public void execute(ScriptEnvironment env) {
		String command = env.substituteTokens(cmd);
		if (env.getMatcher() != null)
			command = env.getMatcher().replaceFirst(command);
			LogHelper.logDebug("[CommandsEX:DEBUG:line] "+command);

		if (modCommand)
			env.getServer().dispatchCommand(ccs, command);
		//	env.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
		else
			env.getServer().dispatchCommand(env.getCommandSender(), command);
	}
}
