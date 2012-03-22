package com.github.zathrus_writer.commandsex.api;

import org.bukkit.command.CommandSender;

import com.github.zathrus_writer.commandsex.CommandsEX;

public class CommandSpawn {
	
	/***
	 * <ol>
	 *  <li>
	 *  	<u>CommandSpawn.execute(<em>Player</em>)</u>
	 * 	</li>
	 * 	<ul>
	 * 		<li>teleports Player to current world's spawn location - if Player has permissions</li>
	 *	</ul>
	 * </ol>
	 * 
	 * @param sender <strong><em>CommandSender</em></strong>	Player instance - use <em>getServer().getPlayer(&quot;name&quot;)</em>
	 */
	public static void execute(CommandSender sender) {
		CommandsEX.plugin.getServer().getPluginCommand("cex_spawn").execute(sender, "cex_spawn", new String[] {});
	}
}