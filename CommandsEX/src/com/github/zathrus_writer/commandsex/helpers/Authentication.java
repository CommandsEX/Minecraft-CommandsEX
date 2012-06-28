package com.github.zathrus_writer.commandsex.helpers;

import com.github.zathrus_writer.commandsex.SQLManager;

import com.github.zathrus_writer.commandsex.CommandsEX;

public class Authentication {

	public static void init(CommandsEX plugin){
		SQLManager.query((SQLManager.sqlType.equals("mysql") ? "" : "BEGIN; ") + "CREATE TABLE IF NOT EXISTS "+ SQLManager.prefix +"userinfo (id_user integer " + (SQLManager.sqlType.equals("mysql") ? "unsigned " : "") +"NOT NULL" + (SQLManager.sqlType.equals("mysql") ? " AUTO_INCREMENT" : "") +", player_name varchar(32) NOT NULL" + (SQLManager.sqlType.equals("mysql") ? "" : " COLLATE 'NOCASE'") + ", creation_date " + (SQLManager.sqlType.equals("mysql") ? "TIMESTAMP" : "DATETIME") + " NOT NULL DEFAULT CURRENT_TIMESTAMP, expiration_date " + (SQLManager.sqlType.equals("mysql") ? "TIMESTAMP" : "DATETIME") + " NOT NULL DEFAULT '0000-00-00 00:00:00', creator VARCHAR(32) NOT NULL, reason VARCHAR(120) DEFAULT NULL, active BOOLEAN NOT NULL DEFAULT '1', PRIMARY KEY (id_ban)" + (SQLManager.sqlType.equals("mysql") ? ", KEY player_name (player_name), KEY expiration_date (expiration_date), KEY active (active)" : "" ) + ")" + (SQLManager.sqlType.equals("mysql") ? " ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='holds ban records for players along with reasons and ban expiration times' AUTO_INCREMENT=1" : "") + (SQLManager.sqlType.equals("mysql") ? "" : "; CREATE INDEX IF NOT EXISTS player_name ON "+ SQLManager.prefix +"bans (player_name); CREATE INDEX IF NOT EXISTS expiration_date ON "+ SQLManager.prefix +"bans (expiration_date); CREATE INDEX IF NOT EXISTS active ON "+ SQLManager.prefix +"bans (active); COMMIT;"));
	}
	
	public static void registerPlayer(String pName, String email, String password){
		
	}
	
}
