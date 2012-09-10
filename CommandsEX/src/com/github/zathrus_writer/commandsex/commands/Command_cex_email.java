package com.github.zathrus_writer.commandsex.commands;

import java.io.File;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.handlers.Handler_email;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.Email;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_email {

	/***
	 * EMAIL - allows sending of E-Mails through CommandsEX
	 * @author Kezz101
	 * @param sender
	 * @param args
	 * @return
	 */

	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (sender instanceof Player && Utils.checkCommandSpam((Player) sender, "cex_email")){
			return true;
		}
		
		File jar = new File(CommandsEX.plugin.getDataFolder() + "/mail.jar");
		
		if (!jar.exists()){
			LogHelper.showWarning("emailNoJar", sender);
			return true;
		}
		
		if (!Handler_email.classpathAdded){
			LogHelper.showWarning("emailErrorLoadingJar", sender);
			return true;
		}
		
		if(sender.hasPermission("cex.email")) {
			if(args.length == 0 || args[0].equalsIgnoreCase("help")) {
				Commands.showCommandHelpAndUsage(sender, "cex_email", alias);
			} else {
				//Compose email
				if(args[0].equalsIgnoreCase("new")) {
					Email.compose(sender);
					return true;
				}

				//Delete email
				if(args[0].equalsIgnoreCase("delete")) {
					Email.delete(sender);
					return true;
				}

				//Edit email
				if(args[0].equalsIgnoreCase("edit")) {
					Email.edit(sender);
					return true;
				}

				//Edit subject
				if(args[0].equalsIgnoreCase("subject")) {
					Email.editSubject(sender, args);
					return true;
				}

				//Edit message
				if(args[0].equalsIgnoreCase("message")) {
					Email.editMessage(sender, args);
					return true;
				}

				//Edit recipient
				if(args[0].equalsIgnoreCase("recipient")) {
					Email.recipient(sender, args);
					return true;
				}

				//Send email
				if(args[0].equalsIgnoreCase("send")) {
					Email.send(sender, args);
					return true;
				}

			}
		}
		return true;
	}
}
