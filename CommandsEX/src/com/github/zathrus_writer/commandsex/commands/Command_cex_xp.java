package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.ExperienceManager;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;

public class Command_cex_xp {

	public static Boolean run(CommandSender sender, String alias, String[] args){
		
		boolean validCommand;
		
		if (args.length == 1){
			if (args[0].equalsIgnoreCase("view")){
				validCommand = true;
			} else {
				validCommand = false;
			}
		} else if (args.length == 2 || args.length == 3){
			validCommand = true;
		} else {
			validCommand = false;
		}
		
		if (!validCommand){
			Commands.showCommandHelpAndUsage(sender, "cex_xp", alias);
			return true;
		}

		Player target;
		String function;
		String amount;
		int amountint = 0;

		if ((args.length == 3 && !args[1].equalsIgnoreCase("view")) || (args.length == 2 && args[1].equalsIgnoreCase("view"))){
			target = Bukkit.getPlayer(args[0]);
			function = args[1];
		} else {
			if (sender instanceof Player){
				target = ((Player) sender);
			} else {
				Commands.showCommandHelpAndUsage(sender, "cex_xp", alias);
				return true;
			}
			function = args[0];
		}

		if (target == null){
			LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
			return true;
		}

		if (!function.equalsIgnoreCase("view") && !function.equalsIgnoreCase("set") && !function.equalsIgnoreCase("add") && !function.equalsIgnoreCase("take")){
			Commands.showCommandHelpAndUsage(sender, "cex_xp", alias);
			return true;
		}

		// If the function is not view then try to convert the string to an integer
		// If the string is not an integer then tell the sender and stop the code
		if (!function.equalsIgnoreCase("view")){
			if (args.length == 2){
				amount = args[1];
			} else {
				amount = args[2];
			}

			try {
				amountint = Integer.valueOf(amount);
			} catch (Exception e){
				LogHelper.showInfo("xpNotNumeric", sender, ChatColor.RED);
				return true;
			}
		}

		ExperienceManager expman = new ExperienceManager(target);
		// We manually define the maximum amount of XP a player can have before their experience bar disappears
		int xpMax = 99999999;
		// Very simple view function to show how much XP a player has
		if (function.equalsIgnoreCase("view")){
			if (!sender.hasPermission("cex.xp.view")){
				LogHelper.showInfo("xpViewNoPerm", sender, ChatColor.RED);
				return true;
			}
			
			if (sender != target && !sender.hasPermission("cex.xp.view.others")){
				LogHelper.showInfo("xpViewOthersNoPerm", sender, ChatColor.RED);
				return true;
			}
			
			LogHelper.showInfo((sender != target ? "[" + target.getName() + " #####xpHas#####[" : "xpViewSelf#####[") + expman.getCurrentExp() + " #####xpExperience", sender, ChatColor.AQUA);
			return true;
		}

		// Set function to set a players XP
		if (function.equalsIgnoreCase("set")){
			if (!sender.hasPermission("cex.xp.set")){
				LogHelper.showInfo("xpSetNoPerm", sender, ChatColor.RED);
				return true;
			}
			
			if (sender != target && !sender.hasPermission("cex.xp.set.others")){
				LogHelper.showInfo("xpSetOthersNoPerm", sender, ChatColor.RED);
				return true;
			}
			
			boolean overLimit = false;
			// See if the amount the player defined is over the limit
			if (amountint > xpMax){ overLimit = true; }
			// Check if it is over the limit, if it is set it to the maximum amount of XP otherwise set it to the amount the player defined
			expman.setExp((overLimit ? xpMax : amountint));
			// If the amount is over the limit, notify the sender
			if (overLimit) { LogHelper.showInfo("xpCouldNotAddAll", sender, ChatColor.RED); }
			// Send a message to the target with the actual amount of XP that was set
			if (sender != target) { LogHelper.showInfo("xpSet#####[" + target.getName() +  " #####xpTo#####[" + (overLimit ? xpMax : amountint), sender, ChatColor.AQUA); }
			// Send a success message to the sender with the actual amount of XP that was set
			LogHelper.showInfo((sender != target ? "[" + sender.getName() + " #####xpSetMsgToTarget1#####[" : "xpSetMsgToTarget2#####[") + (overLimit ? xpMax : amountint), target, ChatColor.AQUA);
		}

		// Add function to add to a players current experience
		if (function.equalsIgnoreCase("add")){
			if (!sender.hasPermission("cex.xp.add")){
				LogHelper.showInfo("xpAddNoPerm", sender, ChatColor.RED);
				return true;
			}
			
			if (sender != target && !sender.hasPermission("cex.xp.add.others")){
				LogHelper.showInfo("xpAddOthersNoPerm", sender, ChatColor.RED);
				return true;
			}
			
			boolean overLimit = false;
			int oldXP = expman.getCurrentExp();
			// Determin whether adding this amount of XP would go over the limit
			if (expman.getCurrentExp() + amountint > xpMax) { overLimit = true; }
			// If it is over the limit, then set their XP to maximum
			// Otherwise add the XP normally
			if (overLimit){
				expman.setExp(xpMax);
				LogHelper.showInfo("xpCouldNotAddAll", sender, ChatColor.RED);
			} else {
				expman.changeExp(amountint);
			}
			// Send a success message to the target with the actual amount of XP that was added
			if (sender != target) { LogHelper.showInfo("xpAdded#####[" + (overLimit ? xpMax - oldXP : amountint) + " #####xpExperience#####[ #####xpTo#####[" + target.getName(), sender, ChatColor.AQUA); }
			// Send a success message to the sender with the actual amount of XP that was added
			LogHelper.showInfo((sender != target ? "[" + sender.getName() + " #####xpAddedGave1#####[" : "xpAddedGave2#####[") + (overLimit ? xpMax - oldXP : amountint) + " #####xpExperience", target, ChatColor.AQUA);
		}

		// Take function to take XP from a player
		if (function.equalsIgnoreCase("take")){
			if (!sender.hasPermission("cex.xp.take")){
				LogHelper.showInfo("xpTakeNoPerm", sender, ChatColor.RED);
				return true;
			}
			
			if (sender != target && !sender.hasPermission("cex.xp.take.others")){
				LogHelper.showInfo("xpTakeOthersNoPerm", sender, ChatColor.RED);
				return true;
			}
			
			// Check if the player has the amount needed
			boolean hasXP = expman.hasExp(amountint);
			int oldXP = expman.getCurrentExp();
			// If the player has the XP, then take the amount the user defined
			// If not then take as much as we can
			expman.changeExp(-(hasXP ? amountint : expman.getCurrentExp()));
			// Send a message to sender saying the target didn't have that amount
			if (!hasXP) { LogHelper.showInfo("xpNotEnough", sender, ChatColor.RED); }
			// Alert the target that some of his XP has been stolen and the actual amount of XP taken
			if (sender != target) { LogHelper.showInfo("xpTaken#####[" + (hasXP ? amountint : oldXP) + " #####xpExperience#####[ #####xpFrom#####[" + target.getName(), sender, ChatColor.AQUA); }
			// Send a message to the sender with the actual amount of XP taken
			LogHelper.showInfo((sender != target ? "[" + sender.getName() + " #####xpTakenTook1#####[" : "xpTakenTook2#####[") + (hasXP ? amountint : oldXP) + " #####xpExperience", target, ChatColor.AQUA);
		}
		return true;
	}

}
