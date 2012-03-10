package com.github.zathrus_writer.commandsex.api;

import org.bukkit.command.CommandSender;

import com.github.zathrus_writer.commandsex.CommandsEX;

public class CommandTploc {
	
	/***
	 * <ol>
	 *  <li>
	 *  	<u>CommandTploc.execute(<em>Player</em>, <em>String coords</em>)</u>
	 * 	</li>
	 * 	<li>
	 *  	<u>CommandTploc.execute(<em>Player</em>, <em>Integer coord X</em>, <em>Integer coord Y</em>, <em>Integer coord Z</em>)<br /><br /></u>
	 *  </li>
	 * 	<ul>
	 * 		<li>teleports Player to given coordinates - if Player has permissions</li>
	 *	</ul>
	 * </ol>
	 * 
	 * @param sender <strong><em>CommandSender</em></strong>	Player instance - use <em>getServer().getPlayer(&quot;name&quot;)</em>
	 * @param args <strong><em>String[]</em></strong>			String / Integer - if string, use &quot;X,Y,Z&quot; - i.e. &quot;280,80,315&quot;
	 */
	public static void execute(CommandSender sender, String... args) {
		CommandsEX.plugin.getServer().getPluginCommand("cex_tploc").execute(sender, "cex_tploc", args);
	}
}