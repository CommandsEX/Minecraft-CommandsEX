package com.github.zathrus_writer.commandsex.api;

import org.bukkit.command.CommandSender;

import com.github.zathrus_writer.commandsex.CommandsEX;

public class CommandTpto {
	
	/***
	 * <ol>
	 *  <li>
	 *  	<u>CommandTpto.execute(<em>Player (1)</em>, <em>Player (2)</em>)</u>
	 * 	</li>
	 * 	<ul>
	 * 		<li>teleports Player (1) to Player (2) - if Player (1) has permissions</li>
	 *	</ul>
	 * </ol>
	 * 
	 * @param sender <strong><em>CommandSender</em></strong>	Player instance - use <em>getServer().getPlayer(&quot;name&quot;)</em>
	 * @param args <strong><em>String[]</em></strong>			Player instance
	 */
	public static void execute(CommandSender sender, String... args) {
		CommandsEX.plugin.getServer().getPluginCommand("cex_tpto").execute(sender, "cex_tpto", args);
	}
}