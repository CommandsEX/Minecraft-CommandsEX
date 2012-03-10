package com.github.zathrus_writer.commandsex.api;

import org.bukkit.command.CommandSender;

import com.github.zathrus_writer.commandsex.CommandsEX;

public class CommandTp {
	
	/***
	 * <ol>
	 *  <li><u>CommandTp.execute(<em>Player (1)</em>, <em>Player (2)</em>)</u></li>
	 * 	<ul>
	 * 		<li>teleports Player (1) to Player (2) - if Player (1) has permissions</li>
	 *	</ul>
	 *
	 * 	<li><u>CommandTp.execute(<em>Player (1)</em>, <em>Player (2)</em>, <em>Player (3)</em>)</u></li>
	 * 	<ul>
	 * 		<li>allows Player (1) to teleport Player (2) to Player (3), if he has permissions</li>
	 *	</ul>
	 * </ol>
	 * 
	 * @param sender <strong><em>CommandSender</em></strong>	Player instance - use <em>getServer().getPlayer(&quot;name&quot;)</em>
	 * @param args <strong><em>String[]</em></strong>			Player instance (either 1 or 2 [args] parameters accepted)
	 */
	public static void execute(CommandSender sender, String... args) {
		CommandsEX.plugin.getServer().getPluginCommand("cex_tp").execute(sender, "cex_tp", args);
	}
}