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
				// don't send any messages if all functions are disabled
				if (!CommandsEX.getConf().getBoolean("autoSave.players") && !CommandsEX.getConf().getBoolean("autoSave.worlds")
						&& !CommandsEX.getConf().getBoolean("autoSave.nicknames")){
					return;
				}
				
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
					if (CommandsEX.loadedClasses.contains("Init_Nicknames")){
						Nicknames.saveNicks();
					}
				}
				
				if (CommandsEX.getConf().getBoolean("autoSave.nametags")){
					if (CommandsEX.loadedClasses.contains("Init_Nametags")){
						Nametags.saveTags();
					}
				}
				
				if (CommandsEX.getConf().getBoolean("autoSave.spawns")){
					if (CommandsEX.loadedClasses.contains("Init_Spawns")){
						Spawning.saveDatabase();
					}
				}
				
				for (String s : CommandsEX.getConf().getStringList("autoSave.messagesFinish")){
					Bukkit.broadcastMessage(Utils.replaceChatColors(CommandsEX.getConf().getString("autoSave.prefix") + s));
				}
			}
		}, delay, delay);
	}
}
