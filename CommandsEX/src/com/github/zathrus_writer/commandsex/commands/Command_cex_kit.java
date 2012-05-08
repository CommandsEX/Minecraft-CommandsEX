package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import com.github.zathrus_writer.commandsex.helpers.Kits;

public class Command_cex_kit extends Kits {
	/***
	 * KIT - gives player a predefined kit or lists all available kits if no parameters are present
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (args.length == 0) {
			list(sender, args);
		} else {
			give(sender, args);
		}
        return true;
	}
}
