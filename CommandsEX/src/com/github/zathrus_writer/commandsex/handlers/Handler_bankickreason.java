package com.github.zathrus_writer.commandsex.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.SQLManager;

import static com.github.zathrus_writer.commandsex.Language._;

public class Handler_bankickreason implements Listener {

	public Handler_bankickreason(){
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerLoginEvent e){
		if (e.getResult() == Result.KICK_BANNED){
			if (CommandsEX.sqlEnabled) {
				try {
					Player player = e.getPlayer();
					String pName = player.getName();
					
					String kickReason = ChatColor.RED + _("bansHeader", pName) + ChatColor.AQUA + "\n" + _("bansReason", pName)
					+ ChatColor.GOLD + "%reason%\n" + ChatColor.AQUA + _("bansExpires", pName) + ChatColor.GOLD + "%expire%\n"
					+ ChatColor.AQUA + _("bansBanTime", pName) + ChatColor.GOLD + "%date% " + ChatColor.AQUA + _("by", pName)
					+ ChatColor.GOLD + " %banner%";
					
					ResultSet res = SQLManager.query_res("SELECT player_name, creation_date, expiration_date, creator, reason FROM " + SQLManager.prefix + "bans WHERE player_name = ? ORDER BY id_ban DESC LIMIT 1", player.getName());

					final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s");
					final String creation_date = dateFormat.format(res.getTimestamp("creation_date").getTime());
					final String expiration_date = dateFormat.format(res.getTimestamp("expiration_date").getTime());

					kickReason = kickReason.replaceFirst("%banner%", res.getString("creator"));

					kickReason = kickReason.replaceFirst("%date%", creation_date);
					if (res.getString("expiration_date").equals("0000-00-00 00:00:00")){
						kickReason = kickReason.replaceFirst("%expire%", "NEVER");
					} else {
						Date d;
						Date c = new Date();

						try {
							d = new Date(dateFormat.parse(res.getString("expiration_date")).getTime());
						} catch (Throwable ex) {
							d = new Date(res.getTimestamp("expiration_date").getTime());
						}

						Integer timeAll = (int) ((d.getTime() - c.getTime()) / 1000); // total ban time

						Integer days = (int) Math.floor(timeAll / 86400);
						Integer hours = (int) Math.floor((timeAll - (days * 86400)) / 3600);
						Integer minutes = (int) Math.floor((timeAll - (days * 86400) - (hours * 3600)) / 60);
						Integer seconds = (timeAll - (days * 86400) - (hours * 3600) - (minutes * 60));

						kickReason = kickReason.replaceFirst("%expire%", expiration_date);
						kickReason = kickReason + "\n Remaining Time: " + (days != 0 ? days + " " +  _("days", pName)
								+ " " : "") + (hours != 0 ? hours + " " + _("hours", pName) + " " : "")
								+ (minutes != 0 ? minutes + " " + _("minutes", pName) + " " : "") + seconds
								+ " " + _("seconds", pName);
					}

					String reason = res.getString("reason");
					kickReason = kickReason.replaceFirst("%reason%", (!reason.equals("") ? reason : _("bansGenericReason", pName)));
					e.setKickMessage(kickReason);

					res.close();
				} catch (SQLException ex){
					if (CommandsEX.getConf().getBoolean("debugMode")){
						ex.printStackTrace();
					}
				}
			}
		}
	}
}
