package com.github.zathrus_writer.commandsex.api;

import org.bukkit.command.CommandSender;

import com.github.zathrus_writer.commandsex.CommandsEX;

public class CommandSetspawn {
	
	/***
	 * <ol>
	 *  <li>
	 *  	<u>CommandSetspawn.execute(<em>Player</em>)</u>
	 * 	</li>
	 * 	<ul>
	 * 		<li>sets spawning point of the current world where Player is standing - if Player has permissions</li>
	 *	</ul>
	 * </ol>
	 * 
	 * @param sender <strong><em>CommandSender</em></strong>	Player instance - use <em>getServer().getPlayer(&quot;name&quot;)</em>
	 */
	public static void execute(CommandSender sender) {
		CommandsEX.plugin.getServer().getPluginCommand("cex_setspawn").execute(sender, "cex_setspawn", new String[] {});
	}
}