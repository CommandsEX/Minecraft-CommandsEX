package com.github.zathrus_writer.commandsex.commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.Vault;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.ExperienceManager;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
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
			if (args[0].equalsIgnoreCase("dispname") || args[0].equalsIgnoreCase("displayname") || args[0].equalsIgnoreCase("balance") || args[0].equalsIgnoreCase("health") || args[0].equalsIgnoreCase("hearts")
					|| args[0].equalsIgnoreCase("armour") || args[0].equalsIgnoreCase("armourpoints") || args[0].equalsIgnoreCase("helmet") || args[0].equalsIgnoreCase("chestplate")
					|| args[0].equalsIgnoreCase("leggings") || args[0].equalsIgnoreCase("boots") || args[0].equalsIgnoreCase("food") || args[0].equalsIgnoreCase("hunger")
					|| args[0].equalsIgnoreCase("xp") || args[0].equalsIgnoreCase("exp") || args[0].equalsIgnoreCase("experience") || args[0].equalsIgnoreCase("xplevels")
					|| args[0].equalsIgnoreCase("explevels") || args[0].equalsIgnoreCase("experiencelevels") || args[0].equalsIgnoreCase("explevel") || args[0].equalsIgnoreCase("experiencelevel")
					|| args[0].equalsIgnoreCase("xplevel")|| args[0].equalsIgnoreCase("coords") || args[0].equalsIgnoreCase("coordinates")
					|| args[0].equalsIgnoreCase("world") || args[0].equalsIgnoreCase("x") || args[0].equalsIgnoreCase("y") || args[0].equalsIgnoreCase("z")
					|| args[0].equalsIgnoreCase("ip") || args[0].equalsIgnoreCase("ipaddress") || args[0].equalsIgnoreCase("gamemode") || args[0].equalsIgnoreCase("gm")
					|| args[0].equalsIgnoreCase("potions")){
				function = args[0];
				if (!(sender instanceof Player)){
					Commands.showCommandHelpAndUsage(sender, "cex_pinfo", alias);
					return true;
				} else {
					target = (Player) sender;
				}
			} else {
				if (sender.hasPermission("cex.pinfo.others")){
					target = Bukkit.getPlayer(args[0]);
					if (target == null){
						LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
						return true;
					}
				} else {
					LogHelper.showInfo("pinfoOthersNoPerm", sender, ChatColor.RED);
					return true;
				}
			}
		} else if (args.length == 2){
			if (sender.hasPermission("cex.pinfo.others")){
				target = Bukkit.getPlayer(args[0]);
				if (target == null){
					LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
					return true;
				}
			} else {
				LogHelper.showInfo("pinfoOthersNoPerm", sender, ChatColor.RED);
				return true;
			}
			
			if (args[1].equalsIgnoreCase("dispname") || args[1].equalsIgnoreCase("displayname") || args[1].equalsIgnoreCase("balance") || args[1].equalsIgnoreCase("health") || args[1].equalsIgnoreCase("hearts")
					|| args[1].equalsIgnoreCase("armour") || args[1].equalsIgnoreCase("armourpoints") || args[1].equalsIgnoreCase("helmet") || args[1].equalsIgnoreCase("chestplate")
					|| args[1].equalsIgnoreCase("leggings") || args[1].equalsIgnoreCase("boots") || args[1].equalsIgnoreCase("food") || args[1].equalsIgnoreCase("hunger")
					|| args[1].equalsIgnoreCase("xp") || args[1].equalsIgnoreCase("exp") || args[1].equalsIgnoreCase("experience") || args[1].equalsIgnoreCase("xplevels")
					|| args[1].equalsIgnoreCase("explevels") || args[1].equalsIgnoreCase("experiencelevels") || args[1].equalsIgnoreCase("explevel") || args[1].equalsIgnoreCase("experiencelevel")
					|| args[1].equalsIgnoreCase("xplevel")|| args[1].equalsIgnoreCase("coords") || args[1].equalsIgnoreCase("coordinates")
					|| args[1].equalsIgnoreCase("world") || args[1].equalsIgnoreCase("x") || args[1].equalsIgnoreCase("y") || args[1].equalsIgnoreCase("z")
					|| args[1].equalsIgnoreCase("ip") || args[1].equalsIgnoreCase("ipaddress") || args[1].equalsIgnoreCase("gamemode") || args[1].equalsIgnoreCase("gm")
					|| args[1].equalsIgnoreCase("potions")){
				function = args[1];
			} else {
				Commands.showCommandHelpAndUsage(sender, "cex_pinfo", alias);
				return true;
			}
		} else {
			Commands.showCommandHelpAndUsage(sender, "cex_pinfo", alias);
			return true;
		}
		
		if (function == null || function.equalsIgnoreCase("dispname") || function.equalsIgnoreCase("displayname")){ LogHelper.showInfo("pinfoDispName#####[" + ChatColor.GOLD + target.getDisplayName(), sender, ChatColor.GRAY); }
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
		if (function == null || function.equalsIgnoreCase("health") || function.equalsIgnoreCase("hearts")){ LogHelper.showInfo("pinfoHealth#####[" + ChatColor.GOLD + target.getHealth(), sender, ChatColor.GRAY); }
		if (function == null || function.equalsIgnoreCase("food") || function.equalsIgnoreCase("hunger")){ LogHelper.showInfo("pinfoFood#####[" + ChatColor.GOLD + target.getFoodLevel(), sender, ChatColor.GRAY); }
		if (function == null || function.equalsIgnoreCase("xp") || function.equalsIgnoreCase("exp") || function.equalsIgnoreCase("experience")){
			ExperienceManager expman = new ExperienceManager(target);
			LogHelper.showInfo("pinfoXP#####[" + ChatColor.GOLD + expman.getCurrentExp(), sender, ChatColor.GRAY);
		}
		if (function == null || function.equalsIgnoreCase("explevel") || function.equalsIgnoreCase("explevels") || function.equalsIgnoreCase("xplevel")
				|| function.equalsIgnoreCase("xplevels") || function.equalsIgnoreCase("experiencelevel") || function.equalsIgnoreCase("experiencelevels")){
			ExperienceManager expman = new ExperienceManager(target);
			LogHelper.showInfo("pinfoXPlvls#####[" + ChatColor.GOLD + expman.getLevelForExp(expman.getCurrentExp()), sender, ChatColor.GRAY);
		}
		
		if (function == null || function.equalsIgnoreCase("coords") || function.equalsIgnoreCase("coordinates") || function.equalsIgnoreCase("world")){ LogHelper.showInfo("pinfoWorld#####[" + ChatColor.GOLD + target.getLocation().getWorld().getName(), sender, ChatColor.GRAY); }
		if (function == null || function.equalsIgnoreCase("coords") || function.equalsIgnoreCase("coordinates") || function.equalsIgnoreCase("x")){ LogHelper.showInfo("pinfoX#####[" + ChatColor.GOLD + target.getLocation().getX(), sender, ChatColor.GRAY); }
		if (function == null || function.equalsIgnoreCase("coords") || function.equalsIgnoreCase("coordinates") || function.equalsIgnoreCase("y")){ LogHelper.showInfo("pinfoY#####[" + ChatColor.GOLD + target.getLocation().getY(), sender, ChatColor.GRAY); }
		if (function == null || function.equalsIgnoreCase("coords") || function.equalsIgnoreCase("coordinates") || function.equalsIgnoreCase("z")){ LogHelper.showInfo("pinfoZ#####[" + ChatColor.GOLD + target.getLocation().getZ(), sender, ChatColor.GRAY); }
		if (function == null || function.equalsIgnoreCase("potions")) {
			ArrayList<String> potions = new ArrayList<String>();
			for (PotionEffect pot : target.getActivePotionEffects()){
				String time = Utils.convertToHHMMSS(pot.getDuration() / 20);
				System.out.println(time);
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
	
}
