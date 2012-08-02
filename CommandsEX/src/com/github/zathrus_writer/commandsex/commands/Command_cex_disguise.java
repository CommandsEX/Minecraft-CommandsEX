package com.github.zathrus_writer.commandsex.commands;

import java.util.HashMap;

import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet24MobSpawn;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_disguise {

	public static HashMap<String, String> disguisedData = new HashMap<String, String>();
	
	public static Boolean run(CommandSender sender, String alias, String[] args){
		
		// [target] <type> <mob name/player name>
		
		Player target = null;
		String function = null;
		String disguisePlayer = null;
		String disguiseEntity = null;
		
		if (args.length > 3 || (args.length < 2 && (!args[0].equalsIgnoreCase("reset") || !args[1].equalsIgnoreCase("reset")))){
			Commands.showCommandHelpAndUsage(sender, "cex_disguise", alias);
			return true;
		}
		
		if (args.length == 1){
			if (!args[0].equalsIgnoreCase("reset")){
				Commands.showCommandHelpAndUsage(sender, "cex_disguise", alias);
				return true;
			}
			
			if (!(sender instanceof Player)){
				// In world only message
				return true;
			}
			
			target = (Player) sender;
			function = "reset";
		}
		
		if (args.length == 2){
			if (args[1].equalsIgnoreCase("reset")){
				target = Bukkit.getPlayer(args[0]);
				if (target == null){
					LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
					return true;
				}
				
				function = "reset";
			} else {
				target = (Player) sender;
				if (args[0].equalsIgnoreCase("player")){
					function = "player";
					disguisePlayer = Utils.replaceChatColors(args[1]);
				} else if (args[0].equalsIgnoreCase("mob")){
					function = "entity";
					disguiseEntity = args[1];
				} else {
					// Invalid function message
					return true;
				}
			}
		}
		
		if (args.length == 3){
			target = Bukkit.getPlayer(args[0]);
			if (target == null){
				LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
				return true;
			}
		}
		
		if (function.equals("player")){
			EntityPlayer eDisguisePlayer = ((CraftPlayer) target).getHandle();
			disguisedData.put(target.getName(), disguisePlayer);
			eDisguisePlayer.name = disguisePlayer;
			
			for (Player inWorld : Bukkit.getOnlinePlayers()){
				if (inWorld != target){
					((CraftPlayer) inWorld).getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(eDisguisePlayer));
				}
			}
			
			return true;
		}
		
		if (function.equals("entity")){
			EntityType eDisguiseEntity = null;
			for (int i = 0; i <= Utils.entityClosestMatches(disguiseEntity).size(); i++){
				EntityType match = Utils.entityClosestMatches(disguiseEntity).get(i);
				if (eDisguiseEntity == null){
					if (match.isAlive() && match.isSpawnable()){
						eDisguiseEntity = match;
					}
				}
			}
			
			if (eDisguiseEntity == null){
				// null message
				return true;
			}
			
			if (sender.hasPermission("cex.disguise.mob." + eDisguiseEntity.getClass().getName().replaceAll(".class", "").replaceAll("_", "").toLowerCase())){
				for (Player inWorld : Bukkit.getOnlinePlayers()){
					if (inWorld != target){
						((CraftPlayer) inWorld).getHandle().netServerHandler.sendPacket(new Packet24MobSpawn())
					}
				}
			}
		}

		return true;
	}
	
}
