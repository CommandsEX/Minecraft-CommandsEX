package com.github.zathrus_writer.commandsex.helpers.scripting;

import java.util.regex.Matcher;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*** 
 * @author tustin2121, Commander plugin (http://dev.bukkit.org/server-mods/commander)
 * This object holds the environment that scripts are executed in. Scripts use the objects
 * in this to execute.
 * @author timpittman
 */
public class ScriptEnvironment {
	
	private Server server;
	private CommandSender commandSender;
	private EchoControl wrappedSender;
	private Matcher matcher;
	
	public Server getServer() {return server;}
	public void setServer(Server server) {this.server = server;}

	public CommandSender getCommandSender() {
	//	return commandSender;
		return wrappedSender;
	}
	
	public void setCommandSender(CommandSender commandSender) {
		this.commandSender = commandSender;
		if (commandSender instanceof Player)
			this.wrappedSender = new EchoControlPlayer((Player)commandSender);
		else
			this.wrappedSender = new EchoControlSender(commandSender);
	}

	public Matcher getMatcher() {return matcher;}
	public void setMatcher(Matcher matcher) {this.matcher = matcher;}

	public Player getPlayer() {
		if (commandSender instanceof Player) return (Player)wrappedSender;
		return null;
	}

	////////////////////////////////////////////////

	public String substituteTokens(String str){
		/* Note: the regex "(?<!a)b" tests to see if there is a 'b' that is not
		 * preceeded by an 'a'. This is called a negative look-back. There are also
		 * look-aheads in the form "(?=positive lookahead)", "(?!negative lookahead)"
		 * and "(?<=positive lookbehind)", "(?<!negative lookbehind)"
		 */

		if (commandSender instanceof Player) {
			//player name. Replace "$p" but not "\$p"
			str = str.replaceAll("(?<!\\\\)\\$p", commandSender.getName()).replaceAll("\\\\$p", "$p"); 
		}

		return str;
	}
}
