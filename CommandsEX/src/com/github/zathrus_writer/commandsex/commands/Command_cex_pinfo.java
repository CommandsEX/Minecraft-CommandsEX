package com.github.zathrus_writer.commandsex.commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.Vault;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.Common;
import com.github.zathrus_writer.commandsex.helpers.ExperienceManager;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_pinfo {

	/***
	 * Player Info - Displays information about a player such as IP address, coords etc
	 * @author iKeirNez
	 * @param sender
	 * @param alias
	 * @param args
	 * @return
	 */

	public static Boolean run(CommandSender sender, String alias, String[] args){

		if (sender instanceof Player && Utils.checkCommandSpam((Player) sender, "cex_pinfo")){
			return true;
		}

		if (args.length > 2){
			Commands.showCommandHelpAndUsage(sender, "cex_pinfo", alias);
			return true;
		}

		Player target;
		String function = null;

		if (args.length == 0){
			if (!(sender instanceof Player)){
				Commands.showCommandHelpAndUsage(sender, "cex_pinfo", alias);
				return true;
			}
			target = (Player) sender;
		} else if (args.length == 1){
			if (validFunction(args[0])){
				function = args[0];
				if (!(sender instanceof Player)){
					Commands.showCommandHelpAndUsage(sender, "cex_pinfo", alias);
					return true;
				} else {
					target = (Player) sender;
				}
			} else {
				target = Bukkit.getPlayer(args[0]);
				if (target == null){
					LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
					return true;
				}
			}
		} else if (args.length == 2){
			target = Bukkit.getPlayer(args[0]);
			if (target == null){
				LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
				return true;
			}
			
			if (validFunction(args[1])){
				function = args[1];
			} else {
				Commands.showCommandHelpAndUsage(sender, "cex_pinfo", alias);
				return true;
			}
		} else {
			Commands.showCommandHelpAndUsage(sender, "cex_pinfo", alias);
			return true;
		}
		
		if (target != sender && !sender.hasPermission("cex.pinfo.others")){
			LogHelper.showInfo("pinfoOthersNoPerm", sender, ChatColor.RED);
			return true;
		}


		if (function == null || function.equalsIgnoreCase("username") || function.equalsIgnoreCase("ign")) { LogHelper.showInfo("pinfoUsername#####[" + ChatColor.GOLD + target.getName(), sender, ChatColor.GRAY); }
		if (function == null || function.equalsIgnoreCase("dispname") || function.equalsIgnoreCase("displayname")){ LogHelper.showInfo("pinfoDispName#####[" + ChatColor.GOLD + Nicknames.getNick(target.getName()), sender, ChatColor.GRAY); }
		if (function == null || function.equalsIgnoreCase("gamemode") || function.equalsIgnoreCase("gm")) { LogHelper.showInfo("pinfoGameMode#####[" + ChatColor.GOLD + Utils.userFriendlyNames(target.getGameMode().name()), sender, ChatColor.GRAY); }
		if (function == null || function.equalsIgnoreCase("ip") || function.equalsIgnoreCase("ipaddress")){
			if (sender != target && !sender.hasPermission("cex.pinfo.others.ip")){
				if (function != null){
					LogHelper.showInfo("pinfoIPOthersNoPerm", sender, ChatColor.RED);
				}
			} else {
				LogHelper.showInfo("pinfoIP#####[" + ChatColor.GOLD + target.getAddress().getAddress().toString().replace("/", ""), sender, ChatColor.GRAY);
			}
		}
		if (function == null || function.equalsIgnoreCase("balance")){
			if (CommandsEX.vaultPresent && Vault.ecoEnabled()){
				LogHelper.showInfo("pinfoBalance#####[" + ChatColor.GOLD + Vault.econ.getBalance(target.getName()), sender, ChatColor.GRAY);
			} else {
				if (function != null){
					LogHelper.showInfo("pinfoEco", sender, ChatColor.RED);
				}
			}
		}
		if (function == null || function.equalsIgnoreCase("health") || function.equalsIgnoreCase("hearts")){ LogHelper.showInfo("pinfoHealth#####[" + ChatColor.GOLD + target.getHealth() + "/20", sender, ChatColor.GRAY); }
		if (function == null || function.equalsIgnoreCase("food") || function.equalsIgnoreCase("hunger")){ LogHelper.showInfo("pinfoFood#####[" + ChatColor.GOLD + target.getFoodLevel() + "/20", sender, ChatColor.GRAY); }
		if (function == null || function.equalsIgnoreCase("xp") || function.equalsIgnoreCase("exp") || function.equalsIgnoreCase("experience")){
			ExperienceManager expman = new ExperienceManager(target);
			LogHelper.showInfo("pinfoXP#####[" + ChatColor.GOLD + expman.getCurrentExp(), sender, ChatColor.GRAY);
		}
		if (function == null || function.equalsIgnoreCase("explevel") || function.equalsIgnoreCase("explevels") || function.equalsIgnoreCase("xplevel")
				|| function.equalsIgnoreCase("xplevels") || function.equalsIgnoreCase("experiencelevel") || function.equalsIgnoreCase("experiencelevels")){
			ExperienceManager expman = new ExperienceManager(target);
			LogHelper.showInfo("pinfoXPlvls#####[" + ChatColor.GOLD + expman.getLevelForExp(expman.getCurrentExp()), sender, ChatColor.GRAY);
		}
		if (function == null || function.equalsIgnoreCase("god")) { LogHelper.showInfo("pinfoGod#####[" + ChatColor.GOLD + (Common.godPlayers.contains(target.getName()) ? "#####pinfoOn" : "#####pinfoOff"), sender, ChatColor.GRAY); }
		if (function == null || function.equalsIgnoreCase("inv") || function.equalsIgnoreCase("invisible")) { LogHelper.showInfo("pinfoInv#####[" + ChatColor.GOLD + (Common.invisiblePlayers.contains(target.getName()) ? "#####pinfoOn" : "#####pinfoOff"), sender, ChatColor.GRAY); }
		
		if (function == null || function.equalsIgnoreCase("coords") || function.equalsIgnoreCase("coordinates") || function.equalsIgnoreCase("world")){ LogHelper.showInfo("pinfoWorld#####[" + ChatColor.GOLD + target.getLocation().getWorld().getName(), sender, ChatColor.GRAY); }
		if (function == null || function.equalsIgnoreCase("coords")) {
			Location loc = target.getLocation();
			LogHelper.showInfo("pinfoX#####[" + ChatColor.GOLD + loc.getX() + ChatColor.GRAY + "#####pinfoY#####[" + ChatColor.GOLD + loc.getY() + ChatColor.GRAY + "#####pinfoZ#####[" + loc.getZ(), sender, ChatColor.GRAY);
		}
		if (function == null || function.equalsIgnoreCase("potions")) {
			ArrayList<String> potions = new ArrayList<String>();
			for (PotionEffect pot : target.getActivePotionEffects()){
				String time = Utils.convertToHHMMSS(pot.getDuration() / 20, true);
				if (time.startsWith("00:")){
					time = time.replaceFirst("00:", "");
					if (time.startsWith("00:")){
						time = time.replaceFirst("00:", "") + "s";
					}
				}
				System.out.println(time);
				potions.add(Utils.userFriendlyNames(pot.getType().getName()) + " : " + time);
			}
			LogHelper.showInfo("pinfoPotions#####[" + ChatColor.GOLD + (!potions.isEmpty() ? Utils.implode(potions, ", ") : "#####pinfoNone"), sender, ChatColor.GRAY);
		}

		if (function == null || function.equalsIgnoreCase("armour") || function.equalsIgnoreCase("helmet") || function.equalsIgnoreCase("head")){
			ArrayList<String> enchantments = new ArrayList<String>();
			ItemStack item = target.getInventory().getHelmet();
			if (item != null){
				for (Enchantment en : item.getEnchantments().keySet()){
					enchantments.add(Utils.userFriendlyNames(en.getName()) + " " + item.getEnchantmentLevel(en));
				}
			}

			LogHelper.showInfo("pinfoHelmet#####[" + ChatColor.GOLD + (item != null ? Utils.userFriendlyNames(item.getType().name()) + ChatColor.GRAY + ", #####pinfoEnchantments#####[" + ChatColor.GOLD + (!enchantments.isEmpty() ? Utils.userFriendlyNames(Utils.implode(enchantments, ", ")) : "#####pinfoNone") : ChatColor.GOLD + "#####pinfoNone"), sender, ChatColor.GRAY);
		}
		if (function == null || function.equalsIgnoreCase("armour") || function.equalsIgnoreCase("chestplate")){
			ArrayList<String> enchantments = new ArrayList<String>();
			ItemStack item = target.getInventory().getChestplate();
			if (item != null){
				for (Enchantment en : item.getEnchantments().keySet()){
					enchantments.add(Utils.userFriendlyNames(en.getName()) + " " + item.getEnchantmentLevel(en));
				}
			}

			LogHelper.showInfo("pinfoChestplate#####[" + ChatColor.GOLD + (item != null ? Utils.userFriendlyNames(item.getType().name()) + ChatColor.GRAY + ", #####pinfoEnchantments#####[" + ChatColor.GOLD + (!enchantments.isEmpty() ? Utils.userFriendlyNames(Utils.implode(enchantments, ", ")) : "#####pinfoNone") : ChatColor.GOLD + "#####pinfoNone"), sender, ChatColor.GRAY);
		}
		if (function == null || function.equalsIgnoreCase("armour") || function.equalsIgnoreCase("leggings") || function.equalsIgnoreCase("pants")){
			ArrayList<String> enchantments = new ArrayList<String>();
			ItemStack item = target.getInventory().getLeggings();
			if (item != null){
				for (Enchantment en : item.getEnchantments().keySet()){
					enchantments.add(Utils.userFriendlyNames(en.getName()) + " " + item.getEnchantmentLevel(en));
				}
			}

			LogHelper.showInfo("pinfoLeggings#####[" + ChatColor.GOLD + (item != null ? Utils.userFriendlyNames(item.getType().name()) + ChatColor.GRAY + ", #####pinfoEnchantments#####[" + ChatColor.GOLD + (!enchantments.isEmpty() ? Utils.userFriendlyNames(Utils.implode(enchantments, ", ")) : "#####pinfoNone") : ChatColor.GOLD + "#####pinfoNone"), sender, ChatColor.GRAY);
		}
		if (function == null || function.equalsIgnoreCase("armour") || function.equalsIgnoreCase("boots")){
			ArrayList<String> enchantments = new ArrayList<String>();
			ItemStack item = target.getInventory().getBoots();
			if (item != null){
				for (Enchantment en : item.getEnchantments().keySet()){
					enchantments.add(Utils.userFriendlyNames(en.getName()) + " " + item.getEnchantmentLevel(en));
				}
			}

			LogHelper.showInfo("pinfoBoots#####[" + ChatColor.GOLD + (item != null ? Utils.userFriendlyNames(item.getType().name()) + ChatColor.GRAY + ", #####pinfoEnchantments#####[" + ChatColor.GOLD + (!enchantments.isEmpty() ? Utils.userFriendlyNames(Utils.implode(enchantments, ", ")) : "#####pinfoNone") : ChatColor.GOLD + "#####pinfoNone"), sender, ChatColor.GRAY);
		}

		return true;
	}
	
	/***
	 * Determines whether a string is a valid function
	 * @param method
	 * @return
	 */
	
	public static boolean validFunction(String method){
		if (method.equalsIgnoreCase("dispname") || method.equalsIgnoreCase("displayname") || method.equalsIgnoreCase("balance") || method.equalsIgnoreCase("health") || method.equalsIgnoreCase("hearts")
				|| method.equalsIgnoreCase("armour") || method.equalsIgnoreCase("helmet") || method.equalsIgnoreCase("chestplate") || method.equalsIgnoreCase("username") || method.equalsIgnoreCase("ign")
				|| method.equalsIgnoreCase("leggings") || method.equalsIgnoreCase("boots") || method.equalsIgnoreCase("food") || method.equalsIgnoreCase("hunger")
				|| method.equalsIgnoreCase("xp") || method.equalsIgnoreCase("exp") || method.equalsIgnoreCase("experience") || method.equalsIgnoreCase("xplevels")
				|| method.equalsIgnoreCase("explevels") || method.equalsIgnoreCase("experiencelevels") || method.equalsIgnoreCase("explevel") || method.equalsIgnoreCase("experiencelevel")
				|| method.equalsIgnoreCase("xplevel")|| method.equalsIgnoreCase("coords") || method.equalsIgnoreCase("coordinates")
				|| method.equalsIgnoreCase("world") || method.equalsIgnoreCase("ip") || method.equalsIgnoreCase("ipaddress") || method.equalsIgnoreCase("gamemode") || method.equalsIgnoreCase("gm")
				|| method.equalsIgnoreCase("potions") || method.equalsIgnoreCase("inv") || method.equalsIgnoreCase("invisible") || method.equalsIgnoreCase("god")){
			return true;
		} else {
			return false;
		}
	}
}
