package com.github.zathrus_writer.commandsex.api;

import org.bukkit.command.CommandSender;

import com.github.zathrus_writer.commandsex.CommandsEX;

public class CommandLang {
	
	/***
	 * <ol>
	 *  <li><u>CommandLang.execute(<em>Player</em>)</u></li>
	 * 	<ul>
	 * 		<li>displays user's current language</li>
	 *		<li>displays list of languages to choose from</li>
	 *	</ul>
	 *
	 * 	<li><u>CommandLang.execute(<em>Player, &quot;en&quot;</em>)</u></li>
	 * 	<ul>
	 * 		<li>set plugin language for the player to general English (en)</li>
	 *		<li>(optionally) store this setting in a database</li>
	 *	</ul>
	 * </ol>
	 * List of languages can be found in CommandsEX's config file under &quot;availableLangs&quot;.<br /><br />
	 * 
	 * @param sender <strong><em>CommandSender</em></strong>	Player instance - use <em>getServer().getPlayer(&quot;name&quot;)</em>
	 * @param args <strong><em>String[]</em></strong>			Optional, language code to change the language of Player to.
	 */
	public static void execute(CommandSender sender, String... args) {
		CommandsEX.plugin.getServer().getPluginCommand("cex_lang").execute(sender, "cex_lang", args);
	}
}