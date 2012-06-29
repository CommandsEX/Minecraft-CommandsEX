package com.github.zathrus_writer.commandsex.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_spawner {

	public static Boolean run(CommandSender sender, String alias, String[] args) {
		
		if (!PlayerHelper.checkIsPlayer(sender)){
			return true;
		}

		Player player = (Player) sender;

		if (Utils.checkCommandSpam(player, "cex_spawner")){
			return true;
		}

		ArrayList<String> list = new ArrayList<String>();
		for (EntityType entity : EntityType.values()){
			if (entity.isAlive() && entity.isSpawnable()){
				list.add(entity.getName().replaceAll(" ", ""));
			}
		}
		
		if (args.length == 0){
			LogHelper.showInfo("spawnerList#####[" + Utils.userFriendlyNames(Utils.implode(list, ", ")), sender, ChatColor.AQUA);
			return true;
		}

		if (args.length == 1){
			Block block = player.getTargetBlock(null, 30);
			if (block.getType() == Material.MOB_SPAWNER){
				String entityType = args[0];
				EntityType entity = EntityType.fromName(entityType);
				if (entity != null && list.contains(entity.getName())){
					CreatureSpawner spawner = (CreatureSpawner) block.getState();
					spawner.setSpawnedType(entity);
					LogHelper.showInfo("spawnerSuccess#####[" + Utils.userFriendlyNames(entity.getName()), sender, ChatColor.GREEN);
				} else {
					LogHelper.showInfo("spawnerInvalid", sender, ChatColor.RED);
				}
			} else {
				LogHelper.showInfo("spawnerLookAtSpawner", sender, ChatColor.RED);
			}
		}
		return true;
	}
	
}
