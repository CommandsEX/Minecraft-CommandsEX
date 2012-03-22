package com.github.zathrus_writer.commandsex.api;

import org.bukkit.command.CommandSender;

import com.github.zathrus_writer.commandsex.CommandsEX;

public class CommandTpa {
	
	/***
	 * <ol>
	 *  <li><u>CommandTpa.execute(<em>Player (1)</em>, <em>Player (2)</em>)</u></li>
	 * 	<ul>
	 * 		<li>asks Player (2) if Player (1) can teleport to him - if Player (1) has permissions to use the command</li>
	 *	</ul>
	 * </ol>
	 * 
	 * @param sender <strong><em>CommandSender</em></strong>	Player instance - use <em>getServer().getPlayer(&quot;name&quot;)</em>
	 * @param args <strong><em>String[]</em></strong>			Player instance
	 */
	public static void execute(CommandSender sender, String... args) {
		CommandsEX.plugin.getServer().getPluginCommand("cex_tpa").execute(sender, "cex_tpa", args);
	}
}