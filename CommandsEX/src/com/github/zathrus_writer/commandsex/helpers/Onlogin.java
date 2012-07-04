package com.github.zathrus_writer.commandsex.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.SQLManager;

public class Onlogin {

	public Onlogin() {
		
	}
	public static void Init(CommandsEX plugin){
		CommandsEX.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(CommandsEX.plugin, new Runnable() {
			public void run() {
			checkForOld();
			}
		}, 20L ,12000L);

	}
	public static void checkForOld(){
		ResultSet set = SQLManager.query_res("SELECT * from " + CommandsEX.getConf().getString("prefix", "") + "onlogin;");
		try {
		while (set.next()) {
			long currentTime = System.currentTimeMillis();
			long oldTime = set.getLong("TimeStamp");
			int maxAge = CommandsEX.getConf().getInt("onLoginTimeLimit", 48);
			long maxAgeLong =(long) maxAge * 3600000;
			long oldTimeNew = oldTime + maxAge;
			if (oldTimeNew < currentTime) {
				String Player = set.getString("Player");
				String Command = set.getString("Command");
				SQLManager.query("DELETE FROM " + CommandsEX.getConf().getString("prefix", "") + "onlogin WHERE Player='"+Player+"' AND Command='" 
				+ Command + "' AND TimeStamp='" + oldTime + "';");
			}
		}
		} catch (SQLException x){
			
		}
	}
}