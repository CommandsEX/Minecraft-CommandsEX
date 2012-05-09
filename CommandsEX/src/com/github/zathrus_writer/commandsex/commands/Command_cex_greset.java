package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.SQLManager;
import com.github.zathrus_writer.commandsex.Vault;
import com.github.zathrus_writer.commandsex.handlers.Handler_deathgroup;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_greset {
	/***
	 * GRESET - reset player's group to an old one that he was a part of before his death
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (!Vault.permsEnabled()) {
			LogHelper.showWarning("afterDeathVaultDisabledInWorld", sender);
			return true;
		}
		
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player;
			Boolean forOther = false;
			// check if we're reverting group for ourselves or for somebody else
			if (args.length > 0) {
				player = Bukkit.getPlayer(args[0]);
				forOther = true;
				
				if (player == null) {
					LogHelper.showWarning("invalidPlayer", sender);
					return true;
				}
			} else {
				player = (Player)sender;
			}
			String pName = player.getName();
			
			if (!Utils.checkCommandSpam((Player) sender, "greset") && Permissions.checkPerms((Player) sender, "cex.deathgroup.reset")) {
				// check if we have a record for this player
				if (!Handler_deathgroup.oldPlayerGroups.containsKey(pName)) {
					LogHelper.showInfo("afterDeathNoRecord", sender);
					return true;
				}
				
				// revert player's group(s)
				String[] changes = Handler_deathgroup.oldPlayerGroups.get(pName).split(";;;");
				for (String change : changes) {
					String[] combo = change.split("##");
					Vault.perms.playerRemoveGroup(player, combo[1]);
					Vault.perms.playerAddGroup(player, combo[0]);
				}
				
				// remove database record
				SQLManager.query("DELETE FROM "+ SQLManager.prefix +"old_groups WHERE player_name = ?", pName);
				
				// inform player(s)
				LogHelper.showInfo("afterDeathGroupReverted", player);
				
				if (forOther) {
					LogHelper.showInfo("afterDeathGroupRevertedForPlayer", sender);
				}
			}
		}
        return true;
	}
}
