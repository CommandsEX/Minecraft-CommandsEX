package com.github.zathrus_writer.commandsex.helpers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import static com.github.zathrus_writer.commandsex.Language._;

public class Permissions {
	/***
	 * Check whether given player has required permission.
	 * @param player
	 * @param customPerm
	 * @return
	 */
	public static Boolean checkPerms(Player player, String... customPerm) {
		// if we have custom permissions to check, the behaviour is as follows...
		// the first parameter MUST BE either "AND" or "OR" (exception = when only 1 node is being checked)
		// ... this will allow us to see if we should check whether the player has either one
		// of these permissions (OR) or whether they have all of them (ALL) and return result
		// accordingly. Every other parameter is a permission node itself.
		Boolean hasPerms = false;
		int cLength = customPerm.length;
		if (cLength == 1) {
			// only a single node is being checked
			hasPerms = player.hasPermission(customPerm[0]);
		} else if (cLength > 1) {
			// multiple nodes check
			if (customPerm[0].equals("OR") || customPerm[0].equals("AND")) {
				for (int i = 1; i < cLength; i++) {
					hasPerms = player.hasPermission(customPerm[i]);

					// only 1 permission node must be present, check if this one can pull it off
					if (customPerm[0].equals("OR") && hasPerms) {
						return true;
					}
					
					// all permissions must be true if we're handling "AND", check it here
					if (customPerm[0].equals("AND") && !hasPerms) {
						player.sendMessage(ChatColor.RED + _("insufficientPerms", player.getName()));
						return false;
					}
				}
			} else {
				hasPerms = false;
				LogHelper.logSevere("[CommandsEX] Custom permissions check failed.");
				LogHelper.logDebug("Method: '" + Thread.currentThread().getStackTrace()[3].getMethodName() + "' (first parameter is not one of: AND/OR - it was '" + customPerm[0] + "')");
			}
		} else {
			hasPerms = false;
			LogHelper.logSevere("[CommandsEX] Permissions check failed.");
			LogHelper.logDebug("Method: '" + Thread.currentThread().getStackTrace()[3].getMethodName() + "' (no paramaters seem to be present)");
		}
		
		if (!hasPerms) {
			player.sendMessage(ChatColor.RED + _("insufficientPerms", player.getName()));
		}
		
		return hasPerms;
	}
}
