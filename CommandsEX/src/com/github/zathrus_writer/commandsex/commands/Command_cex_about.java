package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_about {
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender) && !Utils.checkCommandSpam((Player) sender, "cex") && Permissions.checkPerms((Player) sender, "cex.balance")) {
			
			
			sender.sendMessage(ChatColor.YELLOW + "===============");
			sender.sendMessage(ChatColor.RED + "CommandsEX");
			sender.sendMessage(ChatColor.YELLOW + "===============");
			sender.sendMessage(ChatColor.RED + "Developed By:");
			sender.sendMessage(ChatColor.GREEN + "iKeirNez");
			sender.sendMessage(ChatColor.GREEN + "Callum_White");
			sender.sendMessage(ChatColor.GREEN + "CarlosArias604");
			sender.sendMessage(ChatColor.GREEN + "booooo201");
			sender.sendMessage(ChatColor.GREEN + "Flumpy111");
			sender.sendMessage(ChatColor.GREEN + "wacossusca34");
			sender.sendMessage(ChatColor.GREEN + "zathrus_writer");
			sender.sendMessage(ChatColor.RED + "Version: " + CommandsEX.pdfFile.getVersion());
		}
		return true;
	}
}
//CommandsEX.pdfFile.getVersion() 

