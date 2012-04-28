package com.github.zathrus_writer.commandsex.commands;

import static com.github.zathrus_writer.commandsex.Language._;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.Language;
import com.github.zathrus_writer.commandsex.SQLManager;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_lang {
	/***
	 * LANG - without a parameter, it shows a list of available languages. With parameter,
	 * it changes player's language, should he have permissions to do so.
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player = (Player)sender;
			
			if (!Utils.checkCommandSpam(player, "lang")) {
				// check permissions and call to action
				if (args.length == 1) {
					// change language
					if (Permissions.checkPerms(player, "cex.lang")) {
						// check if the language is within our allowed languages
						String pName = player.getName();
						if (CommandsEX.getConf().getList("availableLangs").contains(args[0])) {
							if (CommandsEX.sqlEnabled) {
								SQLManager.query("INSERT "+ (SQLManager.sqlType.equals("sqlite") ? "OR REPLACE " : "") +" INTO " + SQLManager.prefix + "user2lang VALUES (?, ?)" + ((SQLManager.sqlType.equals("mysql") ? " ON DUPLICATE KEY UPDATE lang = VALUES(lang)" : "")), pName, args[0]);
							}
							Language.perUserLocale.put(pName, args[0]);
							player.sendMessage(ChatColor.YELLOW + _("languageChanged", pName) + ChatColor.WHITE + args[0]);
						} else {
							player.sendMessage(ChatColor.YELLOW + _("noSuchLanguage", pName));
							player.sendMessage(ChatColor.YELLOW + _("availableLangs", pName) + ChatColor.WHITE + CommandsEX.getConf().getList("availableLangs").toString());
						}
					}
				} else if (Permissions.checkPerms(player, "cex.lang")) {
					// show all available lanaguages
					String pName = player.getName();
					player.sendMessage(ChatColor.YELLOW + _("langYourLang", pName) + ChatColor.WHITE + (Language.perUserLocale.containsKey(pName) ? Language.perUserLocale.get(pName) : CommandsEX.getConf().getString("defaultLang")));
					player.sendMessage(ChatColor.YELLOW + _("availableLangs", pName) + ChatColor.WHITE + CommandsEX.getConf().getList("availableLangs").toString());
				}
			}
		}
        return true;
	}
}
