package com.github.zathrus_writer.commandsex.helpers;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;

public class AutoSaving {

	static int delay = CommandsEX.getConf().getInt("autoSave.time") * 20;
	
	public static void init(CommandsEX plugin){
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				for (String s : CommandsEX.getConf().getStringList("autoSave.messagesBegin")){
					Bukkit.broadcastMessage(Utils.replaceChatColors(CommandsEX.getConf().getString("autoSave.prefix") + s));
				}
				
				if (CommandsEX.getConf().getBoolean("autoSave.players")){
					for (Player p : Bukkit.getOnlinePlayers()){
						p.saveData();
					}
				}
				
				if (CommandsEX.getConf().getBoolean("autoSave.worlds")){
					for (World w : Bukkit.getWorlds()){
						w.save();
					}
				}
				
				if (CommandsEX.getConf().getBoolean("autoSave.nicknames")){
					try {
						Nicknames.saveNicks();
					} catch (Exception ex){
					}
				}
				
				for (String s : CommandsEX.getConf().getStringList("autoSave.messagesFinish")){
					Bukkit.broadcastMessage(Utils.replaceChatColors(CommandsEX.getConf().getString("autoSave.prefix") + s));
				}
			}
		}, delay, delay);
	}
}
