package com.github.zathrus_writer.commandsex.handlers;

import static com.github.zathrus_writer.commandsex.Language._;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.SQLManager;
import com.github.zathrus_writer.commandsex.Vault;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.Utils;
import com.github.zathrus_writer.commandsex.helpers.scripting.CommanderCommandSender;

public class Handler_deathgroup implements Listener {

	public static Map<String, String> oldPlayerGroups = new HashMap<String, String>();
	
	/***
	 * Check if a database of old groups exists and activate event listener.
	 */
	public Handler_deathgroup() {
		// check for Vault permissions
		if (!Vault.permsEnabled()) {
			LogHelper.logWarning("[CommandsEX] " + _("afterDeathVaultMissing", ""));
			return;
		}
		
		if (CommandsEX.sqlEnabled) {
			// create old groups table if it's not present yet
			SQLManager.query("CREATE TABLE IF NOT EXISTS "+ SQLManager.prefix +"old_groups (player_name varchar(32) NOT NULL" + (SQLManager.sqlType.equals("mysql") ? "" : " COLLATE 'NOCASE'") + ", group_name varchar(50) NOT NULL, PRIMARY KEY (player_name))" + (SQLManager.sqlType.equals("mysql") ? " ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='stores old player groups where players belonged when they died, so we can revive them back into those groups'" : ""));
			
			// load old player groups from database and delete the ones that did not play for a configured amount of time
			ResultSet res = SQLManager.query_res("SELECT * FROM "+ SQLManager.prefix +"old_groups");
			List<String> remPlayers = new ArrayList<String>();
			try {
				while (res.next()) {
					OfflinePlayer op = Bukkit.getOfflinePlayer(res.getString("player_name"));
					if ((CommandsEX.getConf().getInt("deathGroupsCleanupTime", 14) > 0) && ((Utils.getUnixTimestamp(0L) - (int) (op.getLastPlayed() / 1000)) > (CommandsEX.getConf().getInt("deathGroupsCleanupTime", 14) * 60 * 60 * 24))) {
						// if a player did not play for given number of days, remove his previous group from database
						remPlayers.add(res.getString("player_name"));
					} else {
						oldPlayerGroups.put(res.getString("player_name"), res.getString("group_name"));
					}
				}
				res.close();
				
				Integer remSize = remPlayers.size();
				if (remSize > 0) {
					SQLManager.query("DELETE FROM "+ SQLManager.prefix +"old_groups WHERE player_name IN (\"" + Utils.implode(remPlayers, "\", \"") + "\")");
					LogHelper.logInfo("[CommandsEX] " + _("afterDeathGroupsCleanupComplete", "") + remSize);
				}
			} catch (Throwable e) {
				// unable to load groups
				LogHelper.logSevere("[CommandsEX] " + _("afterDeathCannotLoadData", ""));
				LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
				return;
			}
		}
		
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	
	/***
	 * When a player dies, his group is changed based on settings in a config file.
	 * @param e
	 * @return
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void passChat(PlayerDeathEvent e) {
		// load groups information from config and check if our player belongs to any of them, then change his group
		Player p = (Player) e.getEntity();
		if (Permissions.checkPermEx(p, "cex.deathgroup.ignore")) return;
		
		FileConfiguration f = CommandsEX.getConf();
		ConfigurationSection configGroups = f.getConfigurationSection("deathGroupChanges");
		if (configGroups == null){ return; }
		Set<String> groups = configGroups.getKeys(false);
		String pName = p.getName();
		String toGroup = null;
		String command = null;
		List<String> removedGroups = new ArrayList<String>();
		final CommanderCommandSender ccs = new CommanderCommandSender();
		
		LogHelper.logDebug("groups: " + Utils.implode(Vault.perms.getPlayerGroups(p), ", "));
		for (String group : groups) {
			LogHelper.logDebug("testing against " + group);
			if (Vault.perms.playerInGroup(p, group)) {
				LogHelper.logDebug("test ok");
				// make sure that these are not null and don't cause problems
				if (f.getString("deathGroupChanges." + group + ".toGroup", "") != null && f.getString("deathGroupChanges." + group + ".command", "") != null){
					toGroup = f.getString("deathGroupChanges." + group + ".toGroup", "");
					command = f.getString("deathGroupChanges." + group + ".command", "");
					
					// change player's group based on config
					if (!toGroup.equals("")) {
						removedGroups.add(group + "##" + toGroup);
						Vault.perms.playerRemoveGroup(p, group);
						Vault.perms.playerAddGroup(p, toGroup);
					}
					
					// check if we should execute a command
					if ((command != null) && !command.equals("")) {
						command = Utils.replaceChatColors(command).replace("$p", pName);
						CommandsEX.plugin.getServer().dispatchCommand(ccs, command);
					}
				}
			}
		}

		if (removedGroups.size() > 0) {
			LogHelper.logDebug("removed groups: " + Utils.implode(removedGroups, ", "));
			// store our changes into database
			String imploded = Utils.implode(removedGroups, ";;;");
			SQLManager.query("INSERT "+ (SQLManager.sqlType.equals("mysql") ? "" : "OR REPLACE ") +"INTO " + SQLManager.prefix + "old_groups VALUES(?, ?)" + (SQLManager.sqlType.equals("mysql") ? " ON DUPLICATE KEY UPDATE group_name = VALUES(group_name)" : ""), pName, imploded);
			oldPlayerGroups.put(pName, imploded);
		}
	}
	
}
