package com.github.zathrus_writer.commandsex.handlers;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Chat;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;

public class Handler_chatspy implements Listener {
	
	/***
	 * Activate event listener.
	 */
	public Handler_chatspy() {
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	
	/***
	 * Passes the chat event to players that would not normally see it (and have sufficient permissions).
	 * @param e
	 * @return
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void passChat(PlayerChatEvent e) {
		if (e.getRecipients().size() == 0) return;
		Player sender = e.getPlayer();
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			Boolean playerReceiving = false;
			for (Iterator<Player> it = e.getRecipients().iterator(); it.hasNext();) {
				if (it.next().equals(p)) {
					playerReceiving = true;
					break;
				}
			}
			
			if (!sender.equals(p) && !playerReceiving && Chat.spyActivePlayers.contains(p.getName())) {
				LogHelper.showInfo("[" + ChatColor.GREEN + "(" + sender.getName()+")" + ChatColor.WHITE + ": " + e.getMessage(), p);
			}
		}
	}
	
	/***
	 * Passes the chat event to players that would not normally see it (and have sufficient permissions).
	 * @param e
	 * @return
	 */
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void spyPrivate(PlayerCommandPreprocessEvent e) {
		String cmd = e.getMessage();
		String[] s = cmd.split(" ");
		// no need to check for commands with less than 2 parameters
		if (s.length >= 3) {
			String sName = e.getPlayer().getName();
	
			// check if we're executing a private message command
			if (CommandsEX.getConf().getList("privateMsgCommands").contains(s[0].substring(1))) {
				// spy out :-D
				if (s.length >= 3) {
					for (Player p : Bukkit.getServer().getOnlinePlayers()) {
						String pName = p.getName();
						if (!pName.equals(sName) && !pName.equalsIgnoreCase(s[1]) && Chat.spyActivePlayers.contains(pName)) {
							LogHelper.showInfo("[" + ChatColor.GREEN + "(" + sName +" -> " + s[1] + ")" + ChatColor.WHITE + ": " + e.getMessage().replace(s[0] + " " + s[1], ""), p);
						}
					}
				}
			}
		}
	}
	
}
