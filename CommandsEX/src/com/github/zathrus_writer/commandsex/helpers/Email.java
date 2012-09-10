package com.github.zathrus_writer.commandsex.helpers;

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

import com.github.zathrus_writer.commandsex.CommandsEX;

public class Email {

	public static HashMap<String, Message> mail = new HashMap<String, Message>();
	public static void compose(CommandSender sender){
		if(mail.containsKey(sender.getName())) {
			LogHelper.showWarning("emailAlreadyInProgress", sender);
		} else {
			Properties prop = new Properties();
			prop.put("mail.smtp.host", CommandsEX.getConf().getString("Email.Host"));
			prop.put("mail.debug", "true");
			mail.put(sender.getName(), new MimeMessage(Session.getInstance(prop)));
			LogHelper.showInfo("emailCreated", sender);
		}
	}
	
	public static void delete(CommandSender sender){
		if(mail.containsKey(sender.getName())) {
			mail.remove(sender.getName());
			LogHelper.showInfo("emailDeleted", sender);
		} else {
			LogHelper.showWarning("emailNotInProgressDelete", sender);
		}
	}
	
	public static void edit(CommandSender sender){
		if(mail.containsKey(sender.getName())) {
			LogHelper.showInfo("emailEditOptions", sender);
		} else {
			LogHelper.showWarning("emailNotInProgressEdit", sender);
		}
	}
	
	public static void editSubject(CommandSender sender, String[] args){
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
					return;
				}
				mail.put(sender.getName(), msg);
				LogHelper.showInfo("emailSubjectSet", sender);
			}
		} else {
			LogHelper.showWarning("emailNotInProgressEdit", sender);
		}
	}
	
	public static void editMessage(CommandSender sender, String[] args){
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
					return;
				}
				mail.put(sender.getName(), msg);
				LogHelper.showInfo("emailMessageSet", sender);
			}
		} else {
			LogHelper.showWarning("emailNotInProgressEdit", sender);
		}
	}
	
	public static void recipient(CommandSender sender, String[] args){
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
					return;
				}
				try {
					msg.setRecipient(Message.RecipientType.TO, address);
				} catch (MessagingException e) {
					LogHelper.showWarning("emailMessingError", sender);
					e.printStackTrace();
					return;
				}
				mail.put(sender.getName(), msg);
				LogHelper.showInfo("emailMessageSet", sender);
			}
		} else {
			LogHelper.showWarning("emailNotInProgressEdit", sender);
		}
	}
	
	public static void send(CommandSender sender, String[] args){
		if(mail.containsKey(sender.getName())) {
			Message msg = mail.get(sender.getName());
			try {
				msg.setFrom(new InternetAddress(CommandsEX.getConf().getString("Email.From")));
			} catch (AddressException e) {
				LogHelper.showWarning("emailAddressError", sender);
				e.printStackTrace();
				return;
			} catch (MessagingException e) {
				LogHelper.showWarning("emailMessingError", sender);
				e.printStackTrace();
				return;
			}
			LogHelper.showInfo("emailMessageSending", sender);
			try {
				Transport.send(msg);
			} catch (MessagingException e) {
				LogHelper.showWarning("emailMessingError", sender);
				e.printStackTrace();
				return;
			}
			LogHelper.showInfo("emailMessageSent", sender);
			mail.remove(sender.getName());
		} else {
			LogHelper.showWarning("emailNotInProgressEdit", sender);
		}
	}
	
}
