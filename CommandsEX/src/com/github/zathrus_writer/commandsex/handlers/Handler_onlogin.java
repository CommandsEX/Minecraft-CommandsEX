package com.github.zathrus_writer.commandsex.handlers;

import java.sql.ResultSet;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.SQLManager;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;

public class Handler_onlogin implements Listener{

	
	/***	  
	 * onLogin handler --When a player logs in, it checks the db for relevant entries and executes.
	 * @author EnvisionRed 
	 */
	public Handler_onlogin(){
		//Register events, yo.
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	@EventHandler (priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerLogin(PlayerJoinEvent event) {
		String playerName = event.getPlayer().getName();
		String prefix = CommandsEX.getConf().getString("prefix", "");
		try {
		ResultSet set = SQLManager.query_res("SELECT Player, Command FROM " + prefix + "onlogin WHERE Player = '" + playerName + "';");
		while (set.next()) {
            String j = set.getString("Command");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), j);
            LogHelper.logInfo("Command executed on " + playerName + "'s login.");
        }
		SQLManager.query("DELETE FROM " + prefix +"onlogin WHERE Player = '" + playerName + "';");
		} catch (Exception x) {
			LogHelper.logSevere("OnLogin - Failed to get the commands and execute them from " + playerName + "'s entries.");
		}
	}
}