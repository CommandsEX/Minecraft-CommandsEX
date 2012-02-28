package com.github.zathrus_writer.commandsex.commands;

import static com.github.zathrus_writer.commandsex.CommandsEX._;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;

public class Command_cex_lang {
	/***
	 * LANG - without a parameter, it shows a list of available languages. With parameter,
	 * it changes player's language, should he have permissions to do so.
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (CommandsEX.checkIsPlayer(sender)) {
			Player player = (Player)sender;
			
			// check permissions and call to action
			if (args.length == 1) {
				// change language
				if (CommandsEX.checkPerms(player, "cex.lang")) {
					// check if the language is within our allowed languages
					if (CommandsEX.plugin.getConfig().getList("availableLangs").contains(args[0])) {
						String pName = player.getName();
						CommandsEX.perUserLocale.put(pName, args[0]);
						player.sendMessage(ChatColor.YELLOW + _("languageChanged", pName) + ChatColor.WHITE + args[0]);
					} else {
						player.sendMessage(ChatColor.YELLOW + _("noSuchLanguage", player.getName()));
					}
				}
			} else {
				// show all available lanaguages
				player.sendMessage(ChatColor.YELLOW + _("availableLangs", player.getName()) + ChatColor.WHITE + CommandsEX.plugin.getConfig().getList("availableLangs").toString());
			}
		}
        return true;
	}
}
