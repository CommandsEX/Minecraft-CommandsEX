package com.github.zathrus_writer.commandsex.api;

import org.bukkit.command.CommandSender;

import com.github.zathrus_writer.commandsex.CommandsEX;

public class CommandBack {
	
	/***
	 * <ol>
	 *  <li>
	 *  	<u>CommandBack.execute(<em>Player</em>)</u>
	 * 	</li>
	 * 	<ul>
	 * 		<li>returns Player to his last known position - if Player has permissions</li>
	 *	</ul>
	 * </ol>
	 * 
	 * @param sender <strong><em>CommandSender</em></strong>	Player instance - use <em>getServer().getPlayer(&quot;name&quot;)</em>
	 */
	public static void execute(CommandSender sender) {
		CommandsEX.plugin.getServer().getPluginCommand("cex_back").execute(sender, "cex_back", new String[] {});
	}
}