package com.github.zathrus_writer.commandsex.commands;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_clock {
	
	/***
	 * CLOCK - displays the current time/date (in the real world)
	 * Could be improved in the future by adding the players time
	 * Into /whois, possibly by using the players IP to determine
	 * their location.
	 * @author Kezz101, iKeirNez
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		
		// Player check
		if (sender instanceof Player){
			Player player = (Player) sender;
			if(Utils.checkCommandSpam(player, "cex_clock")) {
				return true;
			}
		}
		
		if (args.length != 0){
			Commands.showCommandHelpAndUsage(sender, "cex_clock", alias);
			return true;
		}
		
		String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		GregorianCalendar gcalendar = new GregorianCalendar();				      
		LogHelper.showInfo("clockTime#####[" + months[gcalendar.get(Calendar.MONTH)] + " " + gcalendar.get(Calendar.DATE) + " " + gcalendar.get(Calendar.YEAR) + ", " + gcalendar.get(Calendar.HOUR) + ":" + gcalendar.get(Calendar.MINUTE), sender);
		
		return true;
	}
}
