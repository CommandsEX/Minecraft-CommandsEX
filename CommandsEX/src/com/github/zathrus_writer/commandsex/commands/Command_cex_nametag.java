package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Nametags;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_nametag extends Nametags {

	/**
	 * Nametag - Change your nametag, with colour support too!
	 * @param sender
	 * @param alias
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args){
		if (sender instanceof Player && Utils.checkCommandSpam((Player) sender, "cex_nametag")){
			return true;
		}
		
		if (!CommandsEX.tagAPIPresent){
			LogHelper.showWarning("tagNoTagAPI", sender);
			return true;
		}
		
		if (args.length == 0 || args.length > 2 || (args.length == 1 && !(sender instanceof Player))){
			Commands.showCommandHelpAndUsage(sender, "cex_nametag", alias);
			return true;
		}
		
		Player target;
		String tagTo;
		
		if (args.length == 1){
			target = (Player) sender;
			tagTo = args[0];
		} else {
			if (!sender.hasPermission("cex.nametag.others")){
				LogHelper.showInfo("tagOthersNoPerm", sender, ChatColor.RED);
				return true;
			}
			
			target = Bukkit.getPlayer(args[0]);
			if (target == null){
				LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
				return true;
			}
			
			tagTo = args[1];
		}
		
		// if nickto = reset then reset their nametag to default
		if (tagTo.equalsIgnoreCase("reset")){
			resetTag(target);
			
			if (!sender.equals(target)){
				LogHelper.showInfo("tagResetConfirm#####[" + Nicknames.getNick(target.getName()), sender, ChatColor.AQUA);
				LogHelper.showInfo("[" + Nicknames.getNick(sender.getName()) + " #####tagResetNotify", target, ChatColor.AQUA);
			} else {
				LogHelper.showInfo("tagResetSelf", sender, ChatColor.AQUA);
			}
			
			return true;
		}
		
		if (Utils.hasChatColor(tagTo)){
			if (!sender.hasPermission("cex.nametag.color")){
				LogHelper.showInfo("tagColorNoPerm", sender);
				return true;
			}
		}
		
		setTag(target.getName(), tagTo);
		
		if (!sender.equals(target)){
			LogHelper.showInfo("tagConfirm#####[" + Nicknames.getNick(target.getName()) + " #####nickSetTo#####[" + Utils.replaceChatColors(tagTo), sender, ChatColor.AQUA);
			LogHelper.showInfo("[" + Nicknames.getNick(sender.getName()) + " #####tagNotify#####[" + Utils.replaceChatColors(tagTo), target, ChatColor.AQUA);
		} else {
			LogHelper.showInfo("tagSelf#####[" + Utils.replaceChatColors(tagTo), sender, ChatColor.AQUA);
		}
		
		return true;
	}
	
}
