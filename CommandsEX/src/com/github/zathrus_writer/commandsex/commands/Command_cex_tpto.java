package com.github.zathrus_writer.commandsex.commands;


import com.github.zathrus_writer.commandsex.CombatTag;
import com.github.zathrus_writer.commandsex.helpers.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command_cex_tpto extends Teleportation {
	/***
	 * TPTO - teleports sender to another player
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player = (Player)sender;

			if (Utils.checkCommandSpam(player, "cex_tpto")){
				return true;
			}

            if (CombatTag.isInCombat(player)){
                LogHelper.showWarning("combatTagCannotDo", player);
                return true;
            }

			// check permissions and call to action
			if (Permissions.checkPerms(player, "OR", "cex.tp", "cex.tpto")) {
				tp_common(sender, args, "tp", alias);
			}
		}
		return true;
	}
}
