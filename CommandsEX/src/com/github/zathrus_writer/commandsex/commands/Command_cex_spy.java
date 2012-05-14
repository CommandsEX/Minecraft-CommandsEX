package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Chat;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;

public class Command_cex_spy extends Chat {
	/***
	 * SPY - activates or deactivates spying on other player (i.e. seeing all chat)
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player = (Player)sender;

			// check permissions and call to action
			if (Permissions.checkPerms(player, "cex.chatspy")) {
				if (Chat.spyActivePlayers.contains(player.getName())) {
					LogHelper.showInfo("chatSpyOff", sender);
					Chat.spyActivePlayers.remove(player.getName());
				} else {
					LogHelper.showInfo("chatSpyOn", sender);
					Chat.spyActivePlayers.add(player.getName());
				}
			}
		}
		
        return true;
	}
}
