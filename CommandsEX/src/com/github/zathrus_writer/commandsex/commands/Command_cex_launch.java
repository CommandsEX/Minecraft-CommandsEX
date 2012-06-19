package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;

public class Command_cex_launch {

	/***
	 * Launch - Launches a player into the air while avoiding fall damage.
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args){
		
		if (PlayerHelper.checkIsPlayer(sender)){
			Player player = (Player) sender;
			if (args.length == 0){
				launch(player, player);
			} else {
				Player target = Bukkit.getPlayer(args[0]);
				
				if (target != null){
					launch(player, target);
				} else {
					LogHelper.showInfo("invalidPlayer", player, ChatColor.RED);
				}
			}
		}
		
		return true;
	}
	
	// Method to launch a player
	private static void launch(Player sender, Player target){
		// Launch the player
		target.setVelocity(new Vector(0, 64, 0));
		// Make sure the player doesn't take damage
		target.setFallDistance(-64.0F);
		
		LogHelper.showInfo("launchMessageToTarget", target, ChatColor.GREEN);
		
		// Make sure the sender and target are not the same and send a message
		if (sender != target){
			LogHelper.showInfo("launchMessageToSender#####[ " + target.getName(), sender, ChatColor.GREEN);
		}
	}
	
}
