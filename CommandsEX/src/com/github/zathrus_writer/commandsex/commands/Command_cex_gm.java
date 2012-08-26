package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_gm {
	
	/***
	 * GM - changes a game mode of a player
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		
		Player target = null;
		GameMode toGM = null;
		
		if (args.length == 0){
			if (!(sender instanceof Player)){
				Commands.showCommandHelpAndUsage(sender, "cex_gm", alias);
				return true;
			}
			
			target = (Player) sender;
		}
		
		if (args.length > 3){
			Commands.showCommandHelpAndUsage(sender, "cex_gm", alias);
			return true;
		}
		
		if (args.length == 1){
			toGM = matchGM(args[0]);
			if (toGM == null){
				if (!sender.hasPermission("cex.gamemode.others")){
					LogHelper.showInfo("gamemodeOthersNoPerm", sender, ChatColor.RED);
					return true;
				}
				
				target = Bukkit.getPlayer(args[0]);
				
				if (target == null){
					LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
					return true;
				}
			} else {
				if (!(sender instanceof Player)){
					Commands.showCommandHelpAndUsage(sender, "cex_gm", alias);
					return true;
				}
				
				target = (Player) sender;
			}
		}
		
		if (args.length == 2){
			if (!sender.hasPermission("cex.gamemode.others")){
				LogHelper.showInfo("gamemodeOthersNoPerm", sender, ChatColor.RED);
				return true;
			}
			
			target = Bukkit.getPlayer(args[0]);
			if (target == null){
				LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
				return true;
			}
			
			toGM = matchGM(args[1]);
			if (toGM == null){
				LogHelper.showInfo("gamemodeInvalid", sender, ChatColor.RED);
				return true;
			}
		}
		
		if (toGM == null){
			if (target.getGameMode() == GameMode.SURVIVAL){
				toGM = GameMode.CREATIVE;
			} else {
				toGM = GameMode.SURVIVAL;
			}
		}
		
		
		target.setGameMode(toGM);
		if (sender != target){
			LogHelper.showInfo("gamemodeToSender#####[" + Nicknames.getNick(target.getName()) + " #####to#####[" + Utils.userFriendlyNames(toGM.name()), sender, ChatColor.AQUA);
			LogHelper.showInfo("gamemodeNotify#####[" + Nicknames.getNick(sender.getName()) + " #####to#####[" + Utils.userFriendlyNames(toGM.name()), target, ChatColor.AQUA);
		} else {
			LogHelper.showInfo("gamemodeSelf#####[" + Utils.userFriendlyNames(toGM.name()), sender, ChatColor.AQUA);
		}
		
		return true;
	}
	
	public static GameMode matchGM(String input){
		GameMode gamemode = null;
		
		if (input.matches(CommandsEX.intRegex)){
			int intValue = Integer.valueOf(input);
			for (GameMode gm : GameMode.values()){
				if (gm.getValue() == intValue){
					gamemode = gm;
				}
			}
		} else {
			for (GameMode gm : GameMode.values()){
				if (input.equalsIgnoreCase(gm.name())){
					gamemode = gm;
				}
			}
		}
		
		return gamemode;
	}
}