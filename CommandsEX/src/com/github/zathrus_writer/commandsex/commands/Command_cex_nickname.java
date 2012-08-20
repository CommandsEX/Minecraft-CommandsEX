package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_nickname extends Nicknames {

	/***
	 * Nickname - Sets a players nickname
	 * @param sender
	 * @param alias
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args){
		
		if (args.length == 0 || args.length > 2 || (args.length == 1 && !(sender instanceof Player))){
			Commands.showCommandHelpAndUsage(sender, "cex_nickname", alias);
			return true;
		}
		
		Player target;
		String nickTo;
		
		if (args.length == 1){
			target = (Player) sender;
			nickTo = args[0];
		} else {
			if (!sender.hasPermission("cex.nickname.others")){
				LogHelper.showInfo("nickOthersNoPerm", sender, ChatColor.RED);
				return true;
			}
			
			target = Bukkit.getPlayer(args[0]);
			if (target == null){
				LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
				return true;
			}
			
			nickTo = args[1];
		}
		
		// if nickto = reset then reset their nickname to default
		if (nickTo.equalsIgnoreCase("reset")){
			resetNick(target);
			
			if (!sender.equals(target)){
				LogHelper.showInfo("nickResetConfirm#####[" + Nicknames.getNick(target.getName()), sender, ChatColor.AQUA);
				LogHelper.showInfo("[" + Nicknames.getNick(sender.getName()) + " #####nickResetNotify", target, ChatColor.AQUA);
			} else {
				LogHelper.showInfo("nickResetSelf", sender, ChatColor.AQUA);
			}
			
			return true;
		}
		
		setNick(target.getName(), nickTo);
		
		if (!sender.equals(target)){
			LogHelper.showInfo("nickConfirm#####[" + Nicknames.getNick(target.getName()) + " #####nickSetTo#####[" + Utils.replaceChatColors(nickTo), sender, ChatColor.AQUA);
			LogHelper.showInfo("[" + Nicknames.getNick(sender.getName()) + " #####nickNotify#####[" + Utils.replaceChatColors(nickTo), target, ChatColor.AQUA);
		} else {
			LogHelper.showInfo("nickSelf#####[" + Utils.replaceChatColors(nickTo), sender, ChatColor.AQUA);
		}
		
		return true;
	}
	
}
