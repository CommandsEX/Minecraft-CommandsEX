package com.github.zathrus_writer.commandsex.helpers;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.mail.*;
import org.bukkit.command.CommandSender;

import com.github.zathrus_writer.commandsex.CommandsEX;

public class Email {
	static HashMap<String, SimpleEmail> simpleEmail = new HashMap<String, SimpleEmail>();
	static HashMap<String, MultiPartEmail> multiEmail = new HashMap<String, MultiPartEmail>();
	static HashMap<String, HtmlEmail> htmlEmail = new HashMap<String, HtmlEmail>();
	
	public static void compose(CommandSender sender, String type){
		if(inProgress(sender.getName())) {
			LogHelper.showWarning("emailAlreadyInProgress", sender);
			return;
		}
		if(type.equalsIgnoreCase("simple")) {
			SimpleEmail email = new SimpleEmail();
			email.setHostName(CommandsEX.getConf().getString("Email.Host"));
			try {
				email.setFrom(CommandsEX.getConf().getString("Email.From"));
			} catch (EmailException e) {
				sendErrorMessage(sender, e); 
				return;
			}
			email = setAuthentication(email);
			simpleEmail.put(sender.getName(), email);
		} else if(type.equalsIgnoreCase("attachment")) {
			MultiPartEmail email = new MultiPartEmail();
			email.setHostName(CommandsEX.getConf().getString("Email.Host"));
			try {
				email.setFrom(CommandsEX.getConf().getString("Email.From"));
			} catch (EmailException e) {
				sendErrorMessage(sender, e);
				return;
			}
			email = setAuthentication(email);
			multiEmail.put(sender.getName(), email);
		} else if(type.equalsIgnoreCase("html")) {
			HtmlEmail email = new HtmlEmail();
			email.setHostName(CommandsEX.getConf().getString("Email.Host"));
			try {
				email.setFrom(CommandsEX.getConf().getString("Email.From"));
			} catch (EmailException e) {
				sendErrorMessage(sender, e);
				return;
			}
			email = setAuthentication(email);
			htmlEmail.put(sender.getName(), email);
		}
		LogHelper.showInfo("emailCreated", sender);
	}
	
	public static void delete(CommandSender sender){
		if(inProgress(sender.getName())) {
			LogHelper.showWarning("emailNotInProgressDelete", sender);
		} else {
			switch(getCurrentEmail(sender.getName())) {
			case "simple": simpleEmail.remove(sender.getName()); LogHelper.showInfo("emailDeleted", sender);
			case "multi": multiEmail.remove(sender.getName()); LogHelper.showInfo("emailDeleted", sender);
			case "html": multiEmail.remove(sender.getName()); LogHelper.showInfo("emailDeleted", sender);
			default: LogHelper.showWarning("emailNotInProgressDelete", sender);
			}
		}
	}
	
	public static void edit(CommandSender sender){
		if(inProgress(sender.getName())) {
			LogHelper.showInfo("emailEditOptions", sender);
		} else {
			LogHelper.showWarning("emailNotInProgressEdit", sender);
		}
	}
	
	public static void editSubject(CommandSender sender, String[] args){
		if(inProgress(sender.getName())) {
			if(args.length == 1) {
				LogHelper.showWarning("emailValueMissing", sender);
			} else {
				switch(getCurrentEmail(sender.getName())) {
				case "simple": editSubjectSimple(sender, args);
				case "multi": editSubjectMulti(sender, args);
				case "html": editSubjectHTML(sender, args);
				default: LogHelper.showWarning("emailNotInProgressEdit", sender);
				}
			}
		} else {
			LogHelper.showWarning("emailNotInProgressEdit", sender);
		}
	}
	
	static void editSubjectSimple(CommandSender sender, String[] args) {
		SimpleEmail email = simpleEmail.get(sender.getName());
		email.setSubject(Utils.implode(args, " "));
		LogHelper.showInfo("emailSubjectSet", sender);
		simpleEmail.put(sender.getName(), email);
	}
	
	static void editSubjectMulti(CommandSender sender, String[] args) {
		MultiPartEmail email = multiEmail.get(sender.getName());
		email.setSubject(Utils.implode(args, " "));
		LogHelper.showInfo("emailSubjectSet", sender);
		multiEmail.put(sender.getName(), email);
	}
	
	static void editSubjectHTML(CommandSender sender, String[] args) {
		HtmlEmail email = htmlEmail.get(sender.getName());
		email.setSubject(Utils.implode(args, " "));
		LogHelper.showInfo("emailSubjectSet", sender);
		htmlEmail.put(sender.getName(), email);
	}
	
	public static void editMessage(CommandSender sender, String[] args){
		if(inProgress(sender.getName())) {
			if(args.length == 1) {
				LogHelper.showWarning("emailValueMissing", sender);
			} else {
				switch(getCurrentEmail(sender.getName())) {
				case "simple": editMessageSimple(sender, args);
				case "multi": editMessageMulti(sender, args);
				case "html": LogHelper.showWarning("emailEditErrorMessage", sender);
				default: LogHelper.showWarning("emailNotInProgressEdit", sender);
				}
			}
		} else {
			LogHelper.showWarning("emailNotInProgressEdit", sender);
		}
	}
	
	static void editMessageSimple(CommandSender sender, String[] args) {
		SimpleEmail email = simpleEmail.get(sender.getName());
		try {
			email.setMsg(Utils.implode(args, " "));
		} catch (EmailException e) {
			sendErrorMessage(sender, e);
			return;
		}
		LogHelper.showInfo("emailMessageSet", sender);
		simpleEmail.put(sender.getName(), email);
	}
	
	static void editMessageMulti(CommandSender sender, String[] args) {
		MultiPartEmail email = multiEmail.get(sender.getName());
		try {
			email.setMsg(Utils.implode(args, " "));
		} catch (EmailException e) {
			sendErrorMessage(sender, e);
			return;
		}
		LogHelper.showInfo("emailMessageSet", sender);
		multiEmail.put(sender.getName(), email);
	}
	
	public enum RecipientAction {
		ADD, ADD_CC, ADD_BCC, LIST, DELETE;
	}
	
	public static void recipient(CommandSender sender, String email, RecipientAction action) {
		if(inProgress(sender.getName())) {
			if(email == null || email.isEmpty()) {
				LogHelper.showWarning("emailValueMissing", sender);
			} else {
				switch(getCurrentEmail(sender.getName())) {
				case "simple": editRecipientSimple(sender, email, action);
				case "multi": editRecipientMulti(sender, email, action);
				case "html": editRecipientHtml(sender, email, action);
				default: LogHelper.showWarning("emailNotInProgressEdit", sender);
				}
			}
		} else {
			LogHelper.showWarning("emailNotInProgressEdit", sender);
		}
	}
	
	static void editRecipientSimple(CommandSender sender, String emailString, RecipientAction action) {
		SimpleEmail email = simpleEmail.get(sender.getName());
		switch(action) {
		case ADD: try {email.addTo(emailString);} catch (EmailException e) {sendErrorMessage(sender, e);return;} LogHelper.showInfo("emailRecipientAdd", sender);
		case ADD_BCC: try {email.addBcc(emailString);} catch (EmailException e) {sendErrorMessage(sender, e);return;} LogHelper.showInfo("emailRecipientAdd", sender);
		case ADD_CC: try {email.addCc(emailString);} catch (EmailException e) {sendErrorMessage(sender, e);return;} LogHelper.showInfo("emailRecipientAdd", sender);
		case DELETE: try {email = removeRecipient(email, emailString);} catch (EmailException e) {sendErrorMessage(sender, e);return;} LogHelper.showInfo("emailRecipientDeleted", sender); 
		case LIST: sender.sendMessage(composeReadableRecipientList(email));
		default: 
		}
		simpleEmail.put(sender.getName(), email);
	}
	
	static void editRecipientMulti(CommandSender sender, String emailString, RecipientAction action) {
		MultiPartEmail email = multiEmail.get(sender.getName());
		switch(action) {
		case ADD: try {email.addTo(emailString);} catch (EmailException e) {sendErrorMessage(sender, e);return;} LogHelper.showInfo("emailRecipientAdd", sender); return;
		case ADD_BCC: try {email.addBcc(emailString);} catch (EmailException e) {sendErrorMessage(sender, e);return;} LogHelper.showInfo("emailRecipientAdd", sender); return;
		case ADD_CC: try {email.addCc(emailString);} catch (EmailException e) {sendErrorMessage(sender, e);return;} LogHelper.showInfo("emailRecipientAdd", sender); return;
		case DELETE: try {email = removeRecipient(email, emailString);} catch (EmailException e) {sendErrorMessage(sender, e);return;} LogHelper.showInfo("emailRecipientDeleted", sender); return; 
		case LIST: sender.sendMessage(composeReadableRecipientList(email));
		default: 
		}
		multiEmail.put(sender.getName(), email);
	}
	
	static void editRecipientHtml(CommandSender sender, String emailString, RecipientAction action) {
		HtmlEmail email = htmlEmail.get(sender.getName());
		switch(action) {
		case ADD: try {email.addTo(emailString);} catch (EmailException e) {sendErrorMessage(sender, e);return;} LogHelper.showInfo("emailRecipientAdd", sender); return;
		case ADD_BCC: try {email.addBcc(emailString);} catch (EmailException e) {sendErrorMessage(sender, e);return;} LogHelper.showInfo("emailRecipientAdd", sender); return;
		case ADD_CC: try {email.addCc(emailString);} catch (EmailException e) {sendErrorMessage(sender, e);return;} LogHelper.showInfo("emailRecipientAdd", sender); return;
		case DELETE: try {email = removeRecipient(email, emailString);} catch (EmailException e) {sendErrorMessage(sender, e);return;} LogHelper.showInfo("emailRecipientDeleted", sender); return; 
		case LIST: sender.sendMessage(composeReadableRecipientList(email));
		default: 
		}
		htmlEmail.put(sender.getName(), email);
	}
	
	
	public static void send(CommandSender sender, String[] args){
		if(inProgress(sender.getName())) {
			switch(getCurrentEmail(sender.getName())) {
			case "simple": LogHelper.showInfo("emailMessageSending", sender); 
				try {
					simpleEmail.get(sender.getName()).send();
				} catch (EmailException e) {
					sendErrorMessage(sender, e);
					return;
				}
				LogHelper.showInfo("emailMessageSent", sender);
				simpleEmail.remove(sender.getName());
			case "multi": LogHelper.showInfo("emailMessageSending", sender); 
				try {
					multiEmail.get(sender.getName()).send();
				} catch (EmailException e) {
					sendErrorMessage(sender, e);
					return;
				}
				LogHelper.showInfo("emailMessageSent", sender);
				multiEmail.remove(sender.getName());
			case "html": LogHelper.showInfo("emailMessageSending", sender);
				try {
					htmlEmail.get(sender.getName()).send();
				} catch (EmailException e) {
					sendErrorMessage(sender, e);
					return;
				}
				LogHelper.showInfo("emailMessageSent", sender);
				htmlEmail.remove(sender.getName());
			default: LogHelper.showWarning("emailNotInProgressEdit", sender);
			}
		} else {
			LogHelper.showWarning("emailNotInProgressEdit", sender);
		}
	}
	
	public static void setBounceback(CommandSender sender, String bounceback) {
		if(inProgress(sender.getName())) {
			if(bounceback == null || bounceback.isEmpty()) {
				LogHelper.showWarning("emailValueMissing", sender);
			} else {
				switch(getCurrentEmail(sender.getName())) {
				case "simple": setBouncebackSimple(sender, bounceback);
				case "multi": setBouncebackMulti(sender, bounceback);
				case "html": setBouncebackHtml(sender, bounceback);
				default: LogHelper.showWarning("emailNotInProgressEdit", sender);
				}
			}
		} else {
			LogHelper.showWarning("emailNotInProgressEdit", sender);
		}
	}
	
	static void setBouncebackSimple(CommandSender sender, String bounceback) {
		SimpleEmail email = simpleEmail.get(sender.getName());
		email.setBounceAddress(bounceback);
		LogHelper.showInfo("emailBouncebackSet", sender);
		simpleEmail.put(sender.getName(), email);
	}
	
	static void setBouncebackMulti(CommandSender sender, String bounceback) {
		MultiPartEmail email = multiEmail.get(sender.getName());
		email.setBounceAddress(bounceback);
		LogHelper.showInfo("emailBouncebackSet", sender);
		multiEmail.put(sender.getName(), email);
	}
	
	static void setBouncebackHtml(CommandSender sender, String bounceback) {
		HtmlEmail email = htmlEmail.get(sender.getName());
		email.setBounceAddress(bounceback);
		LogHelper.showInfo("emailBouncebackSet", sender);
		htmlEmail.put(sender.getName(), email);
	}
	
	public static void addAttachment(CommandSender sender, String fileName, String name, String description) {
		if(inProgress(sender.getName())) {
			switch(getCurrentEmail(sender.getName())) {
			case "simple": LogHelper.showWarning("emailAttachmentNotAllowed", sender); return;
			case "multi": break;
			case "html": LogHelper.showWarning("emailAttachmentNotAllowed", sender); return;
			default: LogHelper.showWarning("emailNotInProgressEdit", sender); return;
			}
			MultiPartEmail email = multiEmail.get(sender.getName());
			EmailAttachment attachment = new EmailAttachment();
			attachment.setPath(CommandsEX.plugin.getDataFolder() + "Email Attachments" + File.separator + fileName);
			attachment.setName(name);
			attachment.setDescription(description);
			try {
				email.attach(attachment);
			} catch (EmailException e) {
				sendErrorMessage(sender, e);
				return;
			}
			LogHelper.showInfo("emailAttachmentSet", sender);
			multiEmail.put(sender.getName(), email);
		} else {
			LogHelper.showWarning("emailNotInProgressEdit", sender);
		}
	}
	
	public static void setHtmlMessage(CommandSender sender, String[] args) {
		if(inProgress(sender.getName())) {
			switch(getCurrentEmail(sender.getName())) {
			case "simple": LogHelper.showWarning("emailHtmlNotAllowed", sender); return;
			case "multi": LogHelper.showWarning("emailHtmlNotAllowed", sender); return;
			case "html": break;
			default: LogHelper.showWarning("emailNotInProgressEdit", sender); return;
			}
			HtmlEmail email = htmlEmail.get(sender.getName());
			try {
				email.setHtmlMsg(Utils.implode(args, " "));
			} catch (EmailException e) {
				sendErrorMessage(sender, e);
				return;
			}
			LogHelper.showWarning("emailHtmlMessageSet", sender);
			htmlEmail.put(sender.getName(), email);
		} else {
			LogHelper.showWarning("emailNotInProgressEdit", sender);
		}
	}
	
	public static void setNonHtmlMessage(CommandSender sender, String[] args) {
		if(inProgress(sender.getName())) {
			switch(getCurrentEmail(sender.getName())) {
			case "simple": LogHelper.showWarning("emailNonHtmlNotAllowed", sender); return;
			case "multi": LogHelper.showWarning("emailNonHtmlNotAllowed", sender); return;
			case "html": break;
			default: LogHelper.showWarning("emailNotInProgressEdit", sender); return;
			}
			HtmlEmail email = htmlEmail.get(sender.getName());
			try {
				email.setMsg(Utils.implode(args, " "));
			} catch (EmailException e) {
				sendErrorMessage(sender, e);
				return;
			}
			LogHelper.showWarning("emailNonHtmlMessageSet", sender);
			htmlEmail.put(sender.getName(), email);
		} else {
			LogHelper.showWarning("emailNotInProgressEdit", sender);
		}
	}
	
	//UTILS FOR EMAIL STUFF
	static SimpleEmail setAuthentication(SimpleEmail email) {
		if(!(CommandsEX.getConf().getString("Email.Username").isEmpty() && CommandsEX.getConf().getString("Email.Password").isEmpty())) {
			email.setAuthentication(CommandsEX.getConf().getString("Email.Username"), CommandsEX.getConf().getString("Email.Password"));
		}
		return email;
	}
	
	static MultiPartEmail setAuthentication(MultiPartEmail email) {
		if(!(CommandsEX.getConf().getString("Email.Username").isEmpty() && CommandsEX.getConf().getString("Email.Password").isEmpty())) {
			email.setAuthentication(CommandsEX.getConf().getString("Email.Username"), CommandsEX.getConf().getString("Email.Password"));
		}
		return email;
	}
	
	static HtmlEmail setAuthentication(HtmlEmail email) {
		if(!(CommandsEX.getConf().getString("Email.Username").isEmpty() && CommandsEX.getConf().getString("Email.Password").isEmpty())) {
			email.setAuthentication(CommandsEX.getConf().getString("Email.Username"), CommandsEX.getConf().getString("Email.Password"));
		}
		return email;
	}
	
	static boolean inProgress(String senderName) {
		if(simpleEmail.containsKey(senderName)) {return true;}
		if(multiEmail.containsKey(senderName)) {return true;}
		if(htmlEmail.containsKey(senderName)) {return true;}
		return false;
	}
	
	static String getCurrentEmail(String senderName) {
		if(simpleEmail.containsKey(senderName)) {return "simple";}
		if(multiEmail.containsKey(senderName)) {return "multi";}
		if(htmlEmail.containsKey(senderName)) {return "html";}
		return null;
	}
	
	static void sendErrorMessage(CommandSender sender, Exception e) {
		LogHelper.showWarning("emailException#####" + e.getMessage(), sender);
	}
	
	static SimpleEmail removeRecipient(SimpleEmail email, String emailToRemove) throws EmailException {
		List<String> to = Utils.noGenericTypeToStringType(email.getToAddresses()), cc = Utils.noGenericTypeToStringType(email.getCcAddresses()), bcc = Utils.noGenericTypeToStringType(email.getBccAddresses());
		if(to.contains(emailToRemove)) {
			to.remove(emailToRemove);
			email.setTo(to);
		} else if(cc.contains(emailToRemove)) {
			cc.remove(emailToRemove);
			email.setCc(cc);
		} else if(bcc.contains(emailToRemove)) {
			bcc.remove(emailToRemove);
			email.setBcc(bcc);
		}
		return email;
	}
	
	static MultiPartEmail removeRecipient(MultiPartEmail email, String emailToRemove) throws EmailException {
		List<String> to = Utils.noGenericTypeToStringType(email.getToAddresses()), cc = Utils.noGenericTypeToStringType(email.getCcAddresses()), bcc = Utils.noGenericTypeToStringType(email.getBccAddresses());
		if(to.contains(emailToRemove)) {
			to.remove(emailToRemove);
			email.setTo(to);
		} else if(cc.contains(emailToRemove)) {
			cc.remove(emailToRemove);
			email.setCc(cc);
		} else if(bcc.contains(emailToRemove)) {
			bcc.remove(emailToRemove);
			email.setBcc(bcc);
		}
		return email;
	}
	
	static HtmlEmail removeRecipient(HtmlEmail email, String emailToRemove) throws EmailException {
		List<String> to = Utils.noGenericTypeToStringType(email.getToAddresses()), cc = Utils.noGenericTypeToStringType(email.getCcAddresses()), bcc = Utils.noGenericTypeToStringType(email.getBccAddresses());
		if(to.contains(emailToRemove)) {
			to.remove(emailToRemove);
			email.setTo(to);
		} else if(cc.contains(emailToRemove)) {
			cc.remove(emailToRemove);
			email.setCc(cc);
		} else if(bcc.contains(emailToRemove)) {
			bcc.remove(emailToRemove);
			email.setBcc(bcc);
		}
		return email;
	}
	
	static String[] composeReadableRecipientList(SimpleEmail email) {
		String[] RRL = {"To: ", "Cc: ", "Bcc: "};
		Iterator<String> toIT = Utils.noGenericTypeToStringType(email.getToAddresses()).iterator(), ccIT = Utils.noGenericTypeToStringType(email.getCcAddresses()).iterator(), bccIT = Utils.noGenericTypeToStringType(email.getBccAddresses()).iterator();
		while(toIT.hasNext()) {
			RRL[0] += toIT.next() + ", ";
		}
		while(ccIT.hasNext()) {
			RRL[1] += ccIT.next() + ", ";
		}
		while(bccIT.hasNext()) {
			RRL[2] += bccIT.next() + ", ";
		}
		return RRL;
	}
	
	static String[] composeReadableRecipientList(MultiPartEmail email) {
		String[] RRL = {"To: ", "Cc: ", "Bcc: "};
		Iterator<String> toIT = Utils.noGenericTypeToStringType(email.getToAddresses()).iterator(), ccIT = Utils.noGenericTypeToStringType(email.getCcAddresses()).iterator(), bccIT = Utils.noGenericTypeToStringType(email.getBccAddresses()).iterator();
		while(toIT.hasNext()) {
			RRL[0] += toIT.next() + ", ";
		}
		while(ccIT.hasNext()) {
			RRL[1] += ccIT.next() + ", ";
		}
		while(bccIT.hasNext()) {
			RRL[2] += bccIT.next() + ", ";
		}
		return RRL;
	}
	
	@SuppressWarnings("unchecked")
	static String[] composeReadableRecipientList(HtmlEmail email) {
		String[] RRL = {"To: ", "Cc: ", "Bcc: "};
		Iterator<String> toIT = email.getToAddresses().iterator(), ccIT = email.getCcAddresses().iterator(), bccIT = email.getBccAddresses().iterator();
		while(toIT.hasNext()) {
			RRL[0] += toIT.next() + ", ";
		}
		while(ccIT.hasNext()) {
			RRL[1] += ccIT.next() + ", ";
		}
		while(bccIT.hasNext()) {
			RRL[2] += bccIT.next() + ", ";
		}
		return RRL;
	}
	
}
