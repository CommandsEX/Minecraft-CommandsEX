package com.github.zathrus_writer.commandsex.helpers;

import static com.github.zathrus_writer.commandsex.Language._;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.SQLManager;

public class Kits implements Listener {
	
	// used to check whether a player did not claim one-time kit before
	private static Map<String, String> usedOneTimeKits = new HashMap<String, String>();
	// used to check for kit cooldowns
	private static Map<String, Integer> kitCooldowns = new HashMap<String, Integer>();
	
	public Kits() {
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	
	/***
	 * INIT - initialization function, called when plugin is enabled to check or create the kits database
	 * @return
	 */
	public static void init(CommandsEX plugin) {
		// first of all, check if we can use any DB
		if (!CommandsEX.sqlEnabled) return;
		
		// next create kits table if it's not present yet
		SQLManager.query("CREATE TABLE IF NOT EXISTS "+ SQLManager.prefix +"kits_usage (player_name varchar(32) NOT NULL" + (SQLManager.sqlType.equals("mysql") ? "" : " COLLATE 'NOCASE'") + ", kit_name varchar(75) NOT NULL, PRIMARY KEY (player_name, kit_name))" + (SQLManager.sqlType.equals("mysql") ? " ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='stored info about which one-time kits were already used by which players'" : ""));
		
		// load kits of players that have been playing recently to speed up database lookups
		ResultSet res = SQLManager.query_res("SELECT player_name, kit_name FROM " + SQLManager.prefix + "kits_usage");
		try {
			while (res.next()) {
				OfflinePlayer op = Bukkit.getOfflinePlayer(res.getString("player_name"));
				if ((Utils.getUnixTimestamp(0L) - (int) (op.getLastPlayed() / 1000)) <= 1209600) {
					// only load data of players that were online in the past 2 weeks
					usedOneTimeKits.put(res.getString("player_name"), res.getString("kit_name"));
				}
			}
			res.close();
		} catch (Throwable e) {
			// unable to load kits data
			LogHelper.logSevere("[CommandsEX] " + _("kitsUnableToLoad", ""));
			LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
		}
		
		// initialize events listening
		new Kits();
	}
	
	/***
	 * LIST - lists all available kits for the given player
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static void list(CommandSender sender, String[] args) {
		// show all kits available to us
		List<String> available = new ArrayList<String>();
		FileConfiguration f = CommandsEX.getConf();
		ConfigurationSection configGroups = f.getConfigurationSection("kits");
		if (configGroups != null){
			Set<String> kitGroups = configGroups.getKeys(false);
			
			for (String group : kitGroups) {
				if (group.equals("*") || ((sender instanceof Player) && Permissions.checkPermEx(((Player)sender), "cex.kits." + group)) || !(sender instanceof Player)) {
					ConfigurationSection kits = f.getConfigurationSection("kits." + group);
					Set<String> kitNames = kits.getKeys(false);
					
					for (String kit : kitNames) {
						available.add(kit);
					}
				}
			}
		} else {
			LogHelper.showInfo("kitsNoneSetup", sender, ChatColor.RED);
			return;
		}
		
		LogHelper.showInfo("kitsAvailable#####[" + Utils.implode(available, ", "), sender);
	}
	
	/***
	 * GIVE - gives a kit to a player, provided they are entitled to it
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static void give(CommandSender sender, String[] args, Boolean suppressWarnings) {
		Boolean forOtherPlayer = false;
		Player player;
		Player sendingPlayer = (Player) sender;
		if (args.length > 1) {
			// we're giving a kit to another player
			player = Bukkit.getServer().getPlayer(args[1]);
			
			if (player == null) {
				LogHelper.showWarning("invalidPlayer", sender);
				return;
			}
			
			forOtherPlayer = true;
		} else {
			player = (Player)sender;
		}

		String pName = player.getName();
		Boolean oneTimeKitWarning = false;
		Boolean cooldownWarning = false;
		Integer kitsAdded = 0;
		Integer stamp = Utils.getUnixTimestamp(0L);

		if (!Utils.checkCommandSpam(sendingPlayer, "kits-give")) {
			// check if we can use the kit in question
			FileConfiguration f = CommandsEX.getConf();
			ConfigurationSection kitConfigGroups = f.getConfigurationSection("kits");
			if (kitConfigGroups != null){
				Set<String> kitGroups = kitConfigGroups.getKeys(false);

				for (String group : kitGroups) {
					if (group.equals("*") || Permissions.checkPermEx(sendingPlayer, "cex.kits." + group)) {
						ConfigurationSection kits = f.getConfigurationSection("kits." + group);
						Set<String> kitNames = kits.getKeys(false);

						for (String kit : kitNames) {
							if (kit.equalsIgnoreCase(args[0])) {
								if (f.getBoolean("kits." + group + "." + kit + ".onetime", false) && usedOneTimeKits.containsKey(pName)) {
									// a one-time kit that was used by this player already
									oneTimeKitWarning = true;
								} else if ((f.getInt("kits." + group + "." + kit + ".cooldown", 0) > 0) && !Permissions.checkPermEx(player, "cex.kits.cooldown.bypass") && kitCooldowns.containsKey(pName) && ((stamp - kitCooldowns.get(pName)) < f.getInt("kits." + group + "." + kit + ".cooldown"))) {
									// kit on a cooldown
									cooldownWarning = true;
								} else {
									// one-time kit?
									if (f.getBoolean("kits." + group + "." + kit + ".onetime", false)) {
										// store one-time usage into database - if this returns error, the record already exists, so we just return a warning
										if (CommandsEX.sqlEnabled) {
											SQLManager.omitErrorLogs = true;
											if (!SQLManager.query("INSERT INTO "+ SQLManager.prefix +"kits_usage VALUES (?, ?)", pName, kit)) {
												oneTimeKitWarning = true;
											} else {
												usedOneTimeKits.put(pName, kit);
											}
											SQLManager.omitErrorLogs = false;
										}
									}

									// cooldown kit?
									if (f.getInt("kits." + group + "." + kit + ".cooldown", 0) > 0) {
										kitCooldowns.put(pName, Utils.getUnixTimestamp(0L));
									}

									// if we don't have a one-time kit warning from database, give items to the player
									if (!oneTimeKitWarning) {
										// load all items for this player's reward
										ConfigurationSection configGroups = f.getConfigurationSection("kits." + group + "." + kit);
										Set<String> s = configGroups.getKeys(false);
										// remove onetime and cooldown keys
										s.remove("onetime");
										s.remove("cooldown");
										s.remove("onjoin");

										// first of all, count all kit blocks and see if they'd fit into player's inventory
										Integer allBlocks = 0;
										Inventory pi = player.getInventory();
										Integer maxStackSize = pi.getMaxStackSize();
										for (String kitItem : s) {
											Integer blockCount = f.getInt("kits." + group + "." + kit + "." + kitItem);
											if (blockCount > maxStackSize) {
												allBlocks = (int) (allBlocks + Math.ceil(blockCount / maxStackSize));
											} else {
												allBlocks++;
											}
										}

										// calculate available slots
										Integer fullSlots = 0;
										for (ItemStack istack : player.getInventory().getContents()) {
											if ((istack != null) && istack.getAmount() > 0) {
												fullSlots++;
											}
										}

										if ((pi.getSize() - fullSlots) >= allBlocks) {
											// fill player's inventory with kit items
											for (String kitItem : s) {
												try {
													if (kitItem.contains(":")) {
														String[] expl = kitItem.split(":");
														pi.addItem(new ItemStack(Integer.parseInt(expl[0]), f.getInt("kits." + group + "." + kit + "." + kitItem), (short) 0, Byte.parseByte(expl[1])));
													} else {
														pi.addItem(new ItemStack(Integer.parseInt(kitItem), f.getInt("kits." + group + "." + kit + "." + kitItem)));
													}
												} catch (Throwable e) {
													// unable to add item into inventory, inform server owner
													LogHelper.logSevere("[CommandsEX] " + _("kitsUnableToAddItem", "") + kitItem + ":" + f.getInt("kits." + group + "." + kit + "." + kitItem));
													LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
												}
											}
											kitsAdded++;
										} else {
											if (!suppressWarnings) {
												// not sure if we could fit reward in, better let player empty their inventory
												LogHelper.showInfo("kitsInsufficientSpace", sendingPlayer);
											}

											// reverse one-time kit data insert
											if (CommandsEX.sqlEnabled) {
												SQLManager.query("DELETE FROM "+ SQLManager.prefix +"kits_usage WHERE player_name = ? AND kit_name = ?", pName, kit);
												usedOneTimeKits.remove(pName);
											}
										}
									}
								}
							}
						}
					}
				}
			} else {
				LogHelper.showInfo("kitsNotExist", sender, ChatColor.RED);
				return;
			}
		}

			if (oneTimeKitWarning) {
				if (!suppressWarnings) {
					LogHelper.showInfo("kitsOneTimeWarning", sendingPlayer);
				}
			} else if (cooldownWarning) {
				if (!suppressWarnings) {
					LogHelper.showInfo("kitsCooldownWarning", sendingPlayer);
				}
			} else if (kitsAdded > 0) {
				if (!suppressWarnings) {
					if (forOtherPlayer) {
						// tell receiving player as well
						LogHelper.showInfo("kitsAddedToOtherPlayer1#####" + (sender.getName().equalsIgnoreCase("console") ? "kitsServerAdmin" : "[" + sender.getName()), player);
						LogHelper.showInfo("kitsAddedToOtherPlayer2", player);
						// now tell the sender
						LogHelper.showInfo("kitsAddedToPlayer", sendingPlayer);
						// and broadcast, if we have it enabled
						if (CommandsEX.getConf().getBoolean("kitsBroadcastGifts", true)) {
							Bukkit.getServer().broadcastMessage(player.getName() + " " + _("kitsGiftBroadcast", "") + sender.getName() + "!");
						}
					} else {
						LogHelper.showInfo("kitsAdded", sendingPlayer);
					}
				}
			}
		}
	
	/***
	 * When a player joins and never played on the server, give him initial kit(s).
	 * @param e
	 * @return
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void giveInitialKits(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		if (player.hasPlayedBefore()) return;

		FileConfiguration f = CommandsEX.getConf();
		ConfigurationSection kitConfigGroups = f.getConfigurationSection("kits");
		if (kitConfigGroups == null) return;
		
		Set<String> kitGroups = kitConfigGroups.getKeys(false);
		
		for (String group : kitGroups) {
			if (group.equals("*") || Permissions.checkPermEx(player, "cex.kits." + group)) {
				ConfigurationSection kits = f.getConfigurationSection("kits." + group);
				Set<String> kitNames = kits.getKeys(false);
				
				for (String kit : kitNames) {
					if (f.getBoolean("kits." + group + "." + kit + ".onjoin", false)) {
						// we have an on-join kit, give it out
						give(player, new String[] {kit}, true);
					}
				}
			}
		}
	}
	
}
