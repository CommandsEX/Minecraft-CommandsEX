package com.github.zathrus_writer.commandsex.commands;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.internal.Pair;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.api.economy.Economy;
import com.github.zathrus_writer.commandsex.helpers.Econ;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

import static com.github.zathrus_writer.commandsex.Language._;

public class Command_cex_economy extends Economy {

	/**
	 * Full Economy Suite (balance, pay, take, spawn, request, purge, save and clear)
	 */
	
	// Target, Requester, Amount, TaskID
	public static HashMap<Pair<String, String>, Pair<Double, BukkitTask>> requests = new HashMap<Pair<String, String>, Pair<Double, BukkitTask>>();
	
	public static HashMap<String, BukkitTask> clearRequests = new HashMap<String, BukkitTask>();
	
	public static Boolean run(final CommandSender sender, String alias, String[] args){
		if (sender instanceof Player && Utils.checkCommandSpam((Player) sender, "cex_economy")){
			return true;
		}
		
		String function = (args.length > 0 ? args[0] : "balance");
		final String sName = sender.getName();
		
		if (function.equalsIgnoreCase("balance")){
			if (sender instanceof Player){
				if (Permissions.checkPerms(sender, "cex.economy.balance")){
					if (args.length > 2){
						sender.sendMessage(getEconomyHelp(sender));
					} else if (args.length == 2){
						if (Permissions.checkPerms(sender, "cex.economy.balance.others")){
							Player target = Bukkit.getPlayer(args[1]);
							if (target != null){
								LogHelper.showInfo("[" + target.getName() + " #####economyBalanceOther#####[" + getCurrencySymbol() + fixDecimals(getBalance(target.getName())), sender);
							} else {
								LogHelper.showWarning("invalidPlayer", sender);
							}
						}
					} else {
						LogHelper.showInfo("economyBalance#####[" + getCurrencySymbol() + fixDecimals(getBalance(sName)), sender);
					}
				}
			} else {
				LogHelper.showWarning("inWorldCommandOnly", sender);
			}
		} else if (function.equalsIgnoreCase("pay")){
			if (Permissions.checkPerms(sender, "cex.economy.pay")){
				if (args.length != 3){
					sender.sendMessage(getEconomyHelp(sender));
				} else {
					Player target = Bukkit.getPlayer(args[1]);
					if (target != null){
						try {
							double amount = Double.parseDouble(args[2]);
							
							// this avoids players giving a negative number
							// which allows them to basically spawn money
							if (amount < 0){
								LogHelper.showWarning("economyNegative", sender);
								return true;
							}
							
							if (has(sName, amount)){
								withdraw(sName, amount);
								deposit(target.getName(), amount);
								
								LogHelper.showInfo("economySent#####[" + getCurrencySymbol() + fixDecimals(amount) + " #####to#####[" + target.getName(), sender);
								LogHelper.showInfo("[" + sName + " #####economyPayNotify1#####[" + getCurrencySymbol() + fixDecimals(amount) + " #####economyPayNotify2", target);
							} else {
								LogHelper.showWarning("economyNotEnough", sender);
							}
						} catch (NumberFormatException e){
							LogHelper.showWarning("economyIncorrectAmount", sender);
						}
					} else {
						LogHelper.showWarning("invalidPlayer", sender);
					}
				}
			}
		} else if (function.equalsIgnoreCase("take")){
			if (Permissions.checkPerms(sender, "cex.economy.take")){
				if (args.length != 3){
					sender.sendMessage(getEconomyHelp(sender));
				} else {
					Player target = Bukkit.getPlayer(args[1]);
					if (target != null){
						try {
							double amount = Double.parseDouble(args[2]);
							
							// this avoids players giving a negative number
							// which allows them to basically spawn money
							if (amount < 0){
								LogHelper.showWarning("economyNegative", sender);
								return true;
							}
							
							if (has(target.getName(), amount)){
								withdraw(target.getName(), amount);
								deposit(sName, amount);
								
								LogHelper.showInfo("economyTake#####[" + getCurrencySymbol() + fixDecimals(amount) + " #####from#####[" + target.getName(), sender);
								LogHelper.showInfo("[" + sName + " #####economyTakeNotify1#####[" + getCurrencySymbol() + fixDecimals(amount) + " #####economyTakeNotify2", target);
							} else {
								LogHelper.showWarning("economyTakeNotEnough", sender);
							}
						} catch (NumberFormatException e){
							LogHelper.showWarning("economyIncorrectAmount", sender);
						}
					} else {
						LogHelper.showWarning("invalidPlayer", sender);
					}
				}
			}
		} else if (function.equalsIgnoreCase("set")){
			if (args.length != 3){
				sender.sendMessage(getEconomyHelp(sender));
			} else {
				Player target = Bukkit.getPlayer(args[1]);
				if (target != null){
					try {
						double amount = Double.parseDouble(args[2]);
						
						// this avoids players giving a negative number
						// which allows them to basically spawn money
						if (amount < 0){
							LogHelper.showWarning("economyNegative", sender);
							return true;
						}
						
						setBalance(target.getName(), amount);
						LogHelper.showInfo("economySet#####[" + target.getName() + " #####to#####[" + fixDecimals(amount), sender);
					} catch (NumberFormatException e){
						LogHelper.showWarning("economyIncorrectAmount", sender);
					}
				} else {
					LogHelper.showWarning("invalidPlayer", sender);
				}
			}
		} else if (function.equalsIgnoreCase("request")){
			if (args.length == 3 || args.length == 2){
				if (PlayerHelper.checkIsPlayer(sender)){
					if (args[1].equalsIgnoreCase("accept") && Permissions.checkPerms(sender, "cex.economy.request.accept")){
						if (args.length == 3){
							Pair<String, String> pair = new Pair<String, String>(sName, args[2]);
							
							if (requests.containsKey(pair)){
								Player player = Bukkit.getPlayerExact(pair.first);
								if (player != null){
									double amount = requests.get(pair).first;
									LogHelper.showInfo("economySent#####[" + getCurrencySymbol() + fixDecimals(amount) + " #####to#####[" + pair.first, sender);
									LogHelper.showInfo("[" + sName + " #####economyRequestAccepted#####[" + getCurrencySymbol() + fixDecimals(amount), sender);
									
									withdraw(sName, amount);
									deposit(pair.first, amount);
									
									// cancel task to expire request
									requests.get(pair).second.cancel();
									requests.remove(pair);
								} else {
									LogHelper.showWarning("[" + pair.first + " #####economyRequestAcceptOffline", sender);
								}
							} else {
								LogHelper.showWarning("economyRequestInvalid", sender);
							}
						} else {
							sender.sendMessage(getEconomyHelp(sender));
						}
					} else if (args[1].equalsIgnoreCase("deny") && Permissions.checkPerms(sender, "cex.economy.request.deny")){
						if (args.length == 3){
							Pair<String, String> pair = new Pair<String, String>(sName, args[2]);
							
							if (requests.containsKey(pair)){
								Player player = Bukkit.getPlayerExact(pair.first);
								
								if (player != null){
									double amount = requests.get(pair).first;
									LogHelper.showInfo("economyRequestDeny#####[" + pair.first + " #####for#####[ " + getCurrencySymbol() + fixDecimals(amount), sender);
									LogHelper.showInfo("[" + sName + " #####economyRequestDenied#####[" + getCurrencySymbol() + fixDecimals(amount), player);
									requests.get(pair).second.cancel();
									requests.remove(pair);
								} else {
									LogHelper.showWarning("[" + pair.first + " #####economyRequestDenyOffline", sender);
								}
							} else {
								LogHelper.showWarning("economyRequestInvalid", sender);
							}
						} else {
							sender.sendMessage(getEconomyHelp(sender));
						}
					} else if (Permissions.checkPerms(sender, "cex.economy.request")){
						if (args.length == 2){
							Player target = Bukkit.getPlayerExact(args[2]);

							if (target != null){
								try {
									final double amount = Double.parseDouble(args[2]);

									// this avoids players giving a negative number
									// which allows them to basically spawn money
									if (amount < 0){
										LogHelper.showWarning("economyNegative", sender);
										return true;
									}
									
									if (has(target.getName(), amount)){
										final Pair<String, String> pair = new Pair<String, String>(target.getName(), sName);
										if (requests.containsKey(pair)){
											requests.remove(pair);
										}
										
										BukkitTask task = Bukkit.getScheduler().runTaskLaterAsynchronously(CommandsEX.plugin, new Runnable(){
											@Override
											public void run() {
												requests.remove(pair);
												Player player = Bukkit.getPlayerExact(pair.second);
												if (player != null){
													LogHelper.showWarning("economyRequestExpired1#####[" + getCurrencySymbol() + fixDecimals(amount) + " #####from#####[ " + pair.first + "#####economyRequestExpired2", player);
												}
												
												Player target = Bukkit.getPlayerExact(pair.first);
												if (target != null){
													LogHelper.showInfo("economyRequestExpiredNotify1#####[" + pair.second + " #####for#####[ " + getCurrencySymbol() + fixDecimals(amount) + " #####economyRequestExpiredNotify1", target);
												}
											}
										}, CommandsEX.getConf().getInt("economy.requestExpireTime") * 20);
										
										requests.put(pair, new Pair<Double, BukkitTask>(amount, task));
										
										LogHelper.showInfo("economyRequest#####[" + getCurrencySymbol() + fixDecimals(amount) + " #####to#####[ " + target, sender);
										
										LogHelper.showInfo("[" + sName + "economyRequestNotify1#####[" + fixDecimals(amount) + " #####economyRequestNotify2", target);
										LogHelper.showInfo("economyRequestNotify3#####[" + sName, target);
										LogHelper.showInfo("economyRequestNotify4#####[" + sName, target);
									} else {
										LogHelper.showWarning("economyTakeNotEnough", sender);
									}
								} catch (NumberFormatException e){
									LogHelper.showWarning("economyIncorrectAmount", sender);
								}
							} else {
								LogHelper.showWarning("invalidPlayer", sender);
							}
						} else {
							sender.sendMessage(getEconomyHelp(sender));
						}
					}
				}
			} else {
				sender.sendMessage(getEconomyHelp(sender));
			}
		} else if (function.equalsIgnoreCase("spawn")){
			if (Permissions.checkPerms(sender, "cex.economy.spawn")){
				if (args.length != 2 || args.length != 3){
					sender.sendMessage(getEconomyHelp(sender));
				} else {
					Player target = null;
					
					if (args.length == 2){
						if (sender instanceof Player){
							target = (Player) sender;
						} else {
							sender.sendMessage(getEconomyHelp(sender));
							return true;
						}
					} else {
						target = Bukkit.getPlayer(args[1]);
					}
					
					if (target == null){
						LogHelper.showWarning("invalidPlayer", sender);
					}
					
					boolean self = false;
					
					if (target.equals(sender)){
						self = true;
					}

					try {
						double amount = Double.parseDouble((self ? args[1] : args[2]));
						
						// this avoids players giving a negative number
						if (amount < 0){
							LogHelper.showWarning("economyNegative", sender);
							return true;
						}
						
						deposit(target.getName(), amount);
						LogHelper.showInfo("economySpawn#####[" + fixDecimals(amount) + " #####" + (self ? "economySpawnAccount" : "for#####[ " + target.getName()), sender);
					} catch (NumberFormatException e){
						LogHelper.showWarning("economyIncorrectAmount", sender);
					}
				}
			}
		}  else if (function.equalsIgnoreCase("purge")){
			if (args.length > 1){
				sender.sendMessage(getEconomyHelp(sender));
			} else {
				if (Permissions.checkPerms(sender, "cex.economy.purge")){
					int amount = Econ.purgeAccounts();
					
					if (amount > 1){
						LogHelper.showInfo("economyPurge1#####[" + amount + " #####economyPurge2", sender);
					} else {
						LogHelper.showWarning("economyPurgeNone", sender);
					}
				}
			}
		} else if (function.equalsIgnoreCase("save")){
			if (Permissions.checkPerms(sender, "cex.economy.save")){
				Econ.saveDatabase();
				LogHelper.showInfo("economySave", sender);
			}
		} else if (function.equalsIgnoreCase("clear")){
			if (args.length == 1){
				if (clearRequests.containsKey(sName)){
					
				} else {
					LogHelper.showWarnings(sender, "economyClear1", "economyClear2", "economyClear3");
					BukkitTask task = Bukkit.getScheduler().runTaskLaterAsynchronously(CommandsEX.plugin, new Runnable(){
						@Override
						public void run() {
							if (clearRequests.containsKey(sName)){
								clearRequests.remove(sName);
								LogHelper.showWarning("economyClearExpired", sender);
							}
						}
					}, 30 * 20);
					
					clearRequests.put(sName, task);
				}
			} else if (args.length == 2 && args[1].equalsIgnoreCase("confirm")){
				if (clearRequests.containsKey(sName)){
					LogHelper.showInfo("economyClearConfirm", sender);
					clearRequests.get(sName).cancel();
					clearRequests.remove(sName);
					Econ.balances.clear();
					Econ.saveDatabase();
				} else {
					LogHelper.showWarning("economyClearInvalid", sender);
				}
			} else {
				sender.sendMessage(getEconomyHelp(sender));
			}
		} else {
			sender.sendMessage(getEconomyHelp(sender));
		}
		
		return true;
	}
	
	public static String[] getEconomyHelp(CommandSender sender){
		String sName = (sender instanceof Player ? sender.getName() : "");
		
		String[] messages = 
				{ChatColor.YELLOW + _("economyHelp", sName),
				ChatColor.YELLOW + "/economy " + ChatColor.BOLD + "help" + ChatColor.RESET + ChatColor.YELLOW + " - " + ChatColor.AQUA + _("economyHelpHelp", sName),
				ChatColor.YELLOW + "/economy " + ChatColor.BOLD + "spawn [player] <amount>" + ChatColor.RESET + ChatColor.YELLOW + " - " + ChatColor.AQUA + _("economyHelpSpawn", sName),
				ChatColor.YELLOW + "/economy " + ChatColor.BOLD + "pay <player> <amount>" + ChatColor.RESET + ChatColor.YELLOW + " - " + ChatColor.AQUA + _("economyHelpPay", sName),
				ChatColor.YELLOW + "/economy " + ChatColor.BOLD + "take <player> <amount>" + ChatColor.RESET + ChatColor.YELLOW + " - " + ChatColor.AQUA + _("economyHelpTake", sName),
				ChatColor.YELLOW + "/economy " + ChatColor.BOLD + "set <player> <amount>" + ChatColor.RESET + ChatColor.YELLOW + " - " + ChatColor.AQUA + _("economyHelpSet", sName),
				ChatColor.YELLOW + "/economy " + ChatColor.BOLD + "request <player> <amount>" + ChatColor.RESET + ChatColor.YELLOW + " - " + ChatColor.AQUA + _("economyHelpRequest", sName),
				ChatColor.YELLOW + "/economy " + ChatColor.BOLD + "request accept" + ChatColor.RESET + ChatColor.YELLOW + " - " + ChatColor.AQUA + _("economyHelpRequestAccept", sName),
				ChatColor.YELLOW + "/economy " + ChatColor.BOLD + "purge" + ChatColor.RESET + ChatColor.YELLOW + " - " + ChatColor.AQUA + _("economyHelpPurge", sName),
				ChatColor.YELLOW + "/economy " + ChatColor.BOLD + "save" + ChatColor.RESET + ChatColor.YELLOW + " - " + ChatColor.AQUA + _("economyHelpSave", sName),
				ChatColor.YELLOW + "/economy " + ChatColor.BOLD + "clear" + ChatColor.RESET + ChatColor.YELLOW + " - " + ChatColor.AQUA + _("economyHelpClear", sName)};
		
		return messages;
	}
	
}
