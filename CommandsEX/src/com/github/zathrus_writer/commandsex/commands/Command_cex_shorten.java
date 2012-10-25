package com.github.zathrus_writer.commandsex.commands;

import java.io.IOException;
import java.net.URL;

import org.bukkit.command.CommandSender;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

/***
 * SHORTEN - will shorten any domain using v.gd url shortener
 * @param sender
 * @param args
 * @return
 */

public class Command_cex_shorten {
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if(args.length != 1) {
			Commands.showCommandHelpAndUsage(sender, "cex_shorten", alias);
		} else {
			try {
				URL url = new URL(args[0]);
				LogHelper.showInfo("shortenedURL#####" + Utils.shortenURL(url).getPath(), sender);
			} catch (IOException e) {
				LogHelper.showWarning("incorrectURL", sender);
				return true;
			}
		}
		return true;
	}
	
}
