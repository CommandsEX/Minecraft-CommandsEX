package com.github.zathrus_writer.commandsex.commands;

import java.util.HashMap;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_email {
	
	/***
	 * EMAIL - allows sending of E-Mails through CommandsEX
	 * @author Kezz101
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static HashMap<String, Message> mail = new HashMap<String, Message>();
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if(PlayerHelper.checkIsPlayer(sender)) {
			if(!Utils.checkCommandSpam((Player)sender, "cex_email") && sender.hasPermission("cex.email")) {
				if(args.length == 0 || args[0].equalsIgnoreCase("help")) {
					Commands.showCommandHelpAndUsage(sender, "cex_email", alias);
				} else {
					//Compose email
					if(args[0].equalsIgnoreCase("new")) {
						if(mail.containsKey(sender.getName())) {
							LogHelper.showWarning("emailAlreadyInProgress", sender);
						} else {
							Properties prop = new Properties();
							prop.put("mail.smtp.host", CommandsEX.getConf().getString("Email.Host"));
							prop.put("mail.debug", "true");
							mail.put(sender.getName(), new MimeMessage(Session.getInstance(prop)));
							LogHelper.showInfo("emailCreated", sender);
						}
						return true;
					}
					
					//Delete email
					if(args[0].equalsIgnoreCase("delete")) {
						if(mail.containsKey(sender.getName())) {
							mail.remove(sender.getName());
							LogHelper.showInfo("emailDeleted", sender);
						} else {
							LogHelper.showWarning("emailNotInProgressDelete", sender);
						}
						return true;
					}
					
					//Edit email
					if(args[0].equalsIgnoreCase("edit")) {
						if(mail.containsKey(sender.getName())) {
							LogHelper.showInfo("emailEditOptions", sender);
						} else {
							LogHelper.showWarning("emailNotInProgressEdit", sender);
						}
						return true;
					}
					
					//Edit subject
					if(args[0].equalsIgnoreCase("subject")) {
						if(mail.containsKey(sender.getName())) {
							if(args.length == 1) {
								LogHelper.showWarning("emailValueMissing", sender);
							} else {
								Message msg = mail.get(sender.getName());
								String[] subject = args;
								subject[0] = "";
								try {
									msg.setSubject(Utils.implode(args, " "));
								} catch (MessagingException e) {
									LogHelper.showWarning("emailMessingError", sender);
									e.printStackTrace();
									return true;
								}
								mail.put(sender.getName(), msg);
								LogHelper.showInfo("emailSubjectSet", sender);
							}
						} else {
							LogHelper.showWarning("emailNotInProgressEdit", sender);
						}
						return true;
					}
					
					//Edit message
					if(args[0].equalsIgnoreCase("message")) {
						if(mail.containsKey(sender.getName())) {
							if(args.length == 1) {
								LogHelper.showWarning("emailValueMissing", sender);
							} else {
								Message msg = mail.get(sender.getName());
								String[] message = args;
								message[0] = "";
								try {
									msg.setText(Utils.implode(message, " ") + "\n Sent via a Bukkit server running CommandsEX.");
								} catch (MessagingException e) {
									LogHelper.showWarning("emailMessingError", sender);
									e.printStackTrace();
									return true;
								}
								mail.put(sender.getName(), msg);
								LogHelper.showInfo("emailMessageSet", sender);
							}
						} else {
							LogHelper.showWarning("emailNotInProgressEdit", sender);
						}
						return true;
					}
					
					//Edit recipient
					if(args[0].equalsIgnoreCase("recipient")) {
						if(mail.containsKey(sender.getName())) {
							if(args.length == 1) {
								LogHelper.showWarning("emailValueMissing", sender);
							} else {
								Message msg = mail.get(sender.getName());
								InternetAddress address = null;
								try {
									address = new InternetAddress(args[1]);
								} catch (AddressException e1) {
									LogHelper.showWarning("emailAddressError", sender);
									e1.printStackTrace();
									return true;
								}
								try {
									msg.setRecipient(Message.RecipientType.TO, address);
								} catch (MessagingException e) {
									LogHelper.showWarning("emailMessingError", sender);
									e.printStackTrace();
									return true;
								}
								mail.put(sender.getName(), msg);
								LogHelper.showInfo("emailMessageSet", sender);
							}
						} else {
							LogHelper.showWarning("emailNotInProgressEdit", sender);
						}
						return true;
					}
					
					//Send email
					if(args[0].equalsIgnoreCase("send")) {
						if(mail.containsKey(sender.getName())) {
							Message msg = mail.get(sender.getName());
							try {
								msg.setFrom(new InternetAddress(CommandsEX.getConf().getString("Email.From")));
							} catch (AddressException e) {
								LogHelper.showWarning("emailAddressError", sender);
								e.printStackTrace();
								return true;
							} catch (MessagingException e) {
								LogHelper.showWarning("emailMessingError", sender);
								e.printStackTrace();
								return true;
							}
							LogHelper.showInfo("emailMessageSending", sender);
							try {
								Transport.send(msg);
							} catch (MessagingException e) {
								LogHelper.showWarning("emailMessingError", sender);
								e.printStackTrace();
								return true;
							}
							LogHelper.showInfo("emailMessageSent", sender);
							mail.remove(sender.getName());
						} else {
							LogHelper.showWarning("emailNotInProgressEdit", sender);
						}
						return true;
					}
					
				}
			}
		}
		return true;
	}

}
