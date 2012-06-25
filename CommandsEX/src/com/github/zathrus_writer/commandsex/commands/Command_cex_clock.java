package com.github.zathrus_writer.commandsex.commands;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

import static com.github.zathrus_writer.commandsex.Language._;

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
		
		String sname = sender.getName();
		String months[] = {_("monthJanuary", sname), _("monthFebruary", sname), _("monthMarch", sname), _("monthApril", sname), _("monthMay", sname), _("monthJune", sname), _("monthJuly", sname), _("monthAugust", sname), _("monthSeptember", sname), _("monthOctober", sname), _("monthNovember", sname), _("monthDecember", sname)};
		GregorianCalendar gcalendar = new GregorianCalendar();				      
		LogHelper.showInfo("clockTime#####[" + gcalendar.get(Calendar.DATE) + " " + months[gcalendar.get(Calendar.MONTH)] + " " + gcalendar.get(Calendar.YEAR) + ", " + (String.valueOf(gcalendar.get(Calendar.HOUR)).length() != 2 ? "0" + gcalendar.get(Calendar.HOUR) : gcalendar.get(Calendar.HOUR)) + ":" + (String.valueOf(gcalendar.get(Calendar.MINUTE)).length() != 2 ? "0" + gcalendar.get(Calendar.MINUTE) : gcalendar.get(Calendar.MINUTE)) + (gcalendar.get(Calendar.AM_PM) == 0 ? "AM" : "PM"), sender);
		
		return true;
	}
}
