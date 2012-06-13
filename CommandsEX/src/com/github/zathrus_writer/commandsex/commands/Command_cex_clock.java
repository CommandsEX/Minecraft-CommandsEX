package com.github.zathrus_writer.commandsex.commands;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.bukkit.command.CommandSender;

import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;

public class Command_cex_clock {
	
	/***
	 * CLOCK - displays the current time/date (in the real world)
	 * @author Kezz101
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		
		// Player check
		if(PlayerHelper.checkIsPlayer(sender)) {
			String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
			GregorianCalendar gcalendar = new GregorianCalendar();				      
			LogHelper.showInfo(months[gcalendar.get(Calendar.MONTH)], sender);
			LogHelper.showInfo(" " + gcalendar.get(Calendar.DATE) + " ", sender);
			LogHelper.showInfo("Time: " + gcalendar.get(Calendar.HOUR) + ":" + gcalendar.get(Calendar.MINUTE), sender);
		}
		
		return true;
	}
}
