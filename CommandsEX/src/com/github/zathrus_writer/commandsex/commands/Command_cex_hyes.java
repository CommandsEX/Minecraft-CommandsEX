package com.github.zathrus_writer.commandsex.commands;


import static com.github.zathrus_writer.commandsex.Language._;

import java.util.Arrays;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.SQLManager;
import com.github.zathrus_writer.commandsex.helpers.Home;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_hyes extends Home {

	/***
	 * HYES - confirms selection made via /home iclear to be deleted and removes records
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (CommandsEX.sqlEnabled) {
			Player player = (Player)sender;
			String pName = player.getName();
			
			// check if we have any IDs stored for this player
			if (!clearHomesConfirmation.containsKey(pName)) {
				LogHelper.showInfo("homeClearNoHomesCached", sender);
				return true;
			}
			
			if (Permissions.checkPerms(player, "cex.hyes")) {
				try {
					// remove homes
					String[] qMarks = new String[clearHomesConfirmation.get(pName).size()];
					Arrays.fill(qMarks, "?");
					SQLManager.query("DELETE FROM " + SQLManager.prefix + "homes WHERE id_home IN ("+ Utils.implode(qMarks, ",") +")", clearHomesConfirmation.get(pName));
					
					// inform player
					LogHelper.showInfo("homeClearDone", sender);
					
					// clear the IDs cache
					clearHomesConfirmation.remove(pName);
				} catch (Throwable e) {
					// something went wrong with our SQL...
					LogHelper.showWarning("internalError", sender);
					LogHelper.logSevere("[CommandsEX] " + _("dbReadError", ""));
					LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
				}
			}
		} else {
			// no database, no homes
			LogHelper.showInfo("homeNoDatabase", sender);
		}
		
        return true;
	}
}
