package com.github.zathrus_writer.commandsex.commands;

import java.io.File;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.handlers.Handler_email;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.Email;
import com.github.zathrus_writer.commandsex.helpers.Email.RecipientAction;
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
		
		File jar = new File(CommandsEX.plugin.getDataFolder() + "/commons-email-1.2.jar");
		
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
					if(args[1] == null || !(args[1].equalsIgnoreCase("simple") || args[1].equalsIgnoreCase("multi") || args[1].equalsIgnoreCase("html"))) {
						LogHelper.showWarning("emailComposeTypeError", sender);
					} else {
						Email.compose(sender, args[1]);
					}
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
					RecipientAction action = null;
					switch (args[1].toLowerCase()) {
					case "add": action = RecipientAction.ADD; Email.recipient(sender, args[2], action);
					case "addbcc": action = RecipientAction.ADD_BCC; Email.recipient(sender, args[2], action);
					case "addcc": action = RecipientAction.ADD_CC; Email.recipient(sender, args[2], action);
					case "delete": action = RecipientAction.DELETE; Email.recipient(sender, args[2], action);
					case "list": action = RecipientAction.LIST; Email.recipient(sender, args[2], action);
					default: LogHelper.showWarning("emailRecipientTypeNotSet", sender);
					}
					return true;
				}
				
				//Set HTML message
				if(args[0].equalsIgnoreCase("htmlmessage")) {
					Email.setHtmlMessage(sender, args);
					return true;
				}
				
				//Set Non HTML message
				if(args[0].equalsIgnoreCase("nonhtmlmessage")) {
					Email.setNonHtmlMessage(sender, args);
					return true;
				}
				
				//Set bounceback
				if(args[0].equalsIgnoreCase("bounceback")) {
					Email.setBounceback(sender, args[1]);
					return true;
				}
				
				//Add attachment
				if(args[0].equalsIgnoreCase("attachment")) {
					String file = args[1], name = args[2], description = args[3];
					if(file == null || name == null || description == null) {
						LogHelper.showWarning("emailAttachmentSyntax", sender);
					} else {
						Email.addAttachment(sender, file, name, description);
					}
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
