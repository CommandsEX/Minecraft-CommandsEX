package com.github.zathrus_writer.commandsex.helpers;

import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.Vault;

public class Permissions {
	
	public static Boolean checkPermEx(Player player, String perm) {
		if (CommandsEX.vaultPresent) {
			return Vault.checkPerm(player, perm);
		} else {
			return player.hasPermission(perm);
		}
	}
	
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
			hasPerms = checkPermEx(player, customPerm[0]);
		} else if (cLength > 1) {
			// multiple nodes check
			if (customPerm[0].equals("OR") || customPerm[0].equals("AND")) {
				for (int i = 1; i < cLength; i++) {
					hasPerms = checkPermEx(player, customPerm[i]);

					// only 1 permission node must be present, check if this one can pull it off
					if (customPerm[0].equals("OR") && hasPerms) {
						return true;
					}
					
					// all permissions must be true if we're handling "AND", check it here
					if (customPerm[0].equals("AND") && !hasPerms) {
						LogHelper.showWarning("insufficientPerms", player);
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
			LogHelper.showWarning("insufficientPerms", player);
		}
		
		return hasPerms;
	}
	
	/***
	 * Gives permission to the player in question.
	 * @param p
	 * @param perm
	 * @return
	 */
	public static Boolean addPerm(Player p, String perm) {
		if (CommandsEX.vaultPresent && Vault.permsEnabled()) {
			Vault.perms.playerAdd(p, perm);
		}
		
		return true;
	}
	
	public static Boolean addPerm(String world, String player, String perm) {
		if (CommandsEX.vaultPresent && Vault.permsEnabled()) {
			Vault.perms.playerAdd(world, player, perm);
		}
		
		return true;
	}
}
