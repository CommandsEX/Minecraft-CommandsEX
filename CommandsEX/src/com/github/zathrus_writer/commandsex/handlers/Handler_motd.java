package com.github.zathrus_writer.commandsex.handlers;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.Vault;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Handler_motd implements Listener {
	
	/***
	 * Activate event listener.
	 */
	public Handler_motd() {
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	
	public static void displayMOTD(CommandSender p) {
		// check if we can use Vault to determine player's group
		// check we aren't using SuperPerms, SuperPerms no like groups!
		if (Vault.permsEnabled() && !Vault.perms.getName().equals("SuperPerms") && (p instanceof Player)) {
			// check if we have extra MOTD for this group set up
			FileConfiguration conf = CommandsEX.getConf();
			Boolean privateMOTDsent = false;
			for (String s : Vault.perms.getPlayerGroups((Player) p)) {
				if (!conf.getString("motd_" + s, "").equals("")) {
					privateMOTDsent = true;
					String[] msg = CommandsEX.getConf().getString("motd_" + s).replace("{playername}", Nicknames.getNick(p.getName())).split("\\{newline\\}");
					for (String s1 : msg) {
						p.sendMessage(Utils.replaceChatColors(s1));
					}
				}
			}
			
			// show generic MOTD in case we did not find a custom one for this player's group
			if (!privateMOTDsent) {
				String[] msg = CommandsEX.getConf().getString("motd").replace("{playername}", Nicknames.getNick(p.getName())).split("\\{newline\\}");
				for (String s1 : msg) {
					p.sendMessage(Utils.replaceChatColors(s1));
				}
			}
		} else {
			String[] msg = CommandsEX.getConf().getString("motd").replace("{playername}", Nicknames.getNick(p.getName())).split("\\{newline\\}");
			for (String s1 : msg) {
				p.sendMessage(Utils.replaceChatColors(s1));
			}
		}
	}
	
	/***
	 * Welcomes a player on server join.
	 * @param e
	 * @return
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void passJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (p.hasPlayedBefore()) {
			displayMOTD(p);
		} else {
			String[] msg = CommandsEX.getConf().getString("motdNewPlayer").replace("{playername}", p.getName()).split("\\{newline\\}");
			for (String s : msg) {
				p.sendMessage(Utils.replaceChatColors(s));
			}
		}
	}
	
}
