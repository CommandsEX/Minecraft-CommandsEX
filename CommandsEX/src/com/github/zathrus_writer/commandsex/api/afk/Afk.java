package com.github.zathrus_writer.commandsex.api.afk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;

public class Afk {
	private static int idleTime = CommandsEX.getConf().getInt("afk.idleTime");
	private static boolean kickEnabled = CommandsEX.getConf().getBoolean("afk.kick.enabled");
	private static int kickTime = CommandsEX.getConf().getInt("afk.kick.time");
	private static String kickMessage = ChatColor.translateAlternateColorCodes("&".charAt(0), CommandsEX.getConf().getString("afk.kick.message"));
	
	private static HashMap<String, Long> playerIdleTime = new HashMap<String, Long>();
	private static List<String> afkPlayers = new ArrayList<String>();
	
	public static void toggleAfk(Player player){
		if (afkPlayers.contains(player)){
			setNotAfk(player, false);
		} else {
			setAfk(player, false);
		}
	}
	
	public static void setAfk(Player player, boolean silent){
		if (!afkPlayers.contains(player)){
			PlayerAfkToggleEvent e = new PlayerAfkToggleEvent(player, true);
			Bukkit.getServer().getPluginManager().callEvent(e);
			String pName = player.getName();
			
			if (!e.isCancelled()){
				afkPlayers.add(pName);
				if (!silent){
					for (Player p : Bukkit.getOnlinePlayers()){
						LogHelper.showInfo("[" + pName + " #####afkIsNowAfk", p);
					}
				}
			}
		}
	}
	
	public static void setNotAfk(Player player, boolean silent){
		String pName = player.getName();
		
		if (afkPlayers.contains(pName)){
			PlayerAfkToggleEvent e = new PlayerAfkToggleEvent(player, false);
			Bukkit.getServer().getPluginManager().callEvent(e);
			
			if (!e.isCancelled()){
				afkPlayers.remove(pName);
				resetPlayerIdleTime(pName);
				
				if (!silent){
					for (Player p : Bukkit.getOnlinePlayers()){
						LogHelper.showInfo("[" + pName + " #####afkIsNoLongerAfk", p);
					}
				}
			}
		}
	}
	
	public static boolean getAfkStatus(String player){
		return afkPlayers.contains(player);
	}
	
	public static long getPlayerAfkTimeInMillis(String player){
		return (playerIdleTime.containsKey(player) ? playerIdleTime.get(player) : -1);
	}
	
	public static void checkPlayerIdleTimes(){
		Iterator<String> it = playerIdleTime.keySet().iterator();
		while (it.hasNext()){
			String player = it.next();
			Player p = Bukkit.getPlayerExact(player);
			if (p == null){
				continue;
			}
			
			if (kickEnabled && p.hasPermission("cex.afk.kick") && (System.currentTimeMillis() - playerIdleTime.get(player)) / 1000 >= kickTime){
				p.kickPlayer(kickMessage);
				
				// send the kick message to each player in their own language
				for (Player pl : Bukkit.getOnlinePlayers()){
					LogHelper.showWarning("[" + ChatColor.RED + player + " #####afkKickNotify", pl);
				}
			} else if ((System.currentTimeMillis() - playerIdleTime.get(player)) / 1000 >= idleTime){
				setAfk(p, false);
			}
		}
	}
	
	public static void resetPlayerIdleTime(String player){
		Player p = Bukkit.getPlayerExact(player);
		
		if (p != null && p.hasPermission("cex.afk.auto")){
			if (playerIdleTime.containsKey(player)){
				playerIdleTime.remove(player);
			}
			
			if (afkPlayers.contains(player)){
				setNotAfk(p, false);
			}
			
			playerIdleTime.put(player, System.currentTimeMillis());
		}
	}
	
	public static void removeAfkRecords(String player){
		if (afkPlayers.contains(player)){
			afkPlayers.remove(player);
		}
		
		if (playerIdleTime.containsKey(player)){
			playerIdleTime.remove(player);
		}
	}
	
	public static int getIdleTimeToAfk(){
		return idleTime;
	}
	
	public static int getTimeToKick(){
		return kickTime;
	}
	
	public static boolean isKickEnabled(){
		return kickEnabled;
	}
	
	public static String getKickMessage(){
		return kickMessage;
	}
	
}
