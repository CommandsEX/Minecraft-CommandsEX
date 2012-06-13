package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;

public class Command_cex_ownerstatus {
	
	/***
	 * OWNERSTATUS - displays wether the owner is online with optional other stuff...
	 * @author Kezz101
	 * @param sender
	 * @param args
	 * @return
	 */
	
	// Owner status
	public static String ownerS = CommandsEX.getConf().getString("ServerOwner");
	public static String ownerStatus = "";
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		
		// Command Variables
		Player player = (Player)sender;
		Player owner = Bukkit.getPlayer(ownerS);
		
		// Is the sender the owner?
		if(player == owner) {
						
			// If they ain't used any correct args
			if(args.length != 1 || args[0]=="help" || args[0]=="check") {
				
				// Tell them what they are
				LogHelper.showInfo("ownerCheckStatus#####" + "[" + ownerStatus, sender);
				
				// Then show them how to change it
				LogHelper.showInfo("ownerChangeStatus", sender);
				LogHelper.showInfo("ownerDefineDoNotDisturb", sender);
				LogHelper.showInfo("ownerDefineAwayFromKeyboard", sender);
				LogHelper.showInfo("ownerDefineBusy", sender);
				LogHelper.showInfo("ownerDefineHere", sender);
				
				// And finally show them the syntax
				Commands.showCommandHelpAndUsage(sender, "cex_ownerstatus", alias);
				
				return true;
			}
			
			// Set there new status
			if(args[0].equalsIgnoreCase("here")) {
				ownerStatus="";
			} else if(args[0].equalsIgnoreCase("dnd")) {
				ownerStatus="dnd";
			} else if(args[0].equalsIgnoreCase("afk")) {
				ownerStatus="afk";
			} else if(args[0].equalsIgnoreCase("busy")) {
				ownerStatus="busy";
			} else {
				return false;
			}
			
			// Finally show them what they changed it to
			LogHelper.showInfo("ownerNewStatus#####" + "[" + ownerStatus, sender);
			
			return true;
		}
		
		// Is owner is offline 
		if(owner == null) {
			LogHelper.showInfo("[" + ownerS + " #####ownerOffline", sender, ChatColor.RED);
			return true;
		}
		
		// If owner is dnd
		if(ownerStatus.equalsIgnoreCase("dnd")) {
			LogHelper.showInfo("[" + ownerS + " #####ownerDoNotDisturb", sender, ChatColor.DARK_RED);
			return true;
		}
		
		// If owner is afk
		if(ownerStatus.equalsIgnoreCase("afk")) {
			LogHelper.showInfo("[" + ownerS + " #####ownerAwayFromKeyboard", sender, ChatColor.RED);
			return true;
		}
		
		// Is owner is busy
		if(ownerStatus.equalsIgnoreCase("busy")) {
			LogHelper.showInfo("[" + ownerS + " #####ownerBusy", sender, ChatColor.RED);
			return true;
		}
		
		// If they are just online
		if(ownerStatus.equalsIgnoreCase("")) {
			LogHelper.showInfo("[" + ownerS + " #####ownerOnline", sender, ChatColor.GREEN);
			return true;
		}
		
		return true;
	}
}
