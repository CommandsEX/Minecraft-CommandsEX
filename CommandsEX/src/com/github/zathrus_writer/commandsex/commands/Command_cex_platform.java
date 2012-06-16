package com.github.zathrus_writer.commandsex.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;

public class Command_cex_platform {

	/***
	 * PLATFORM - places a block below the player, useful for building in the sky
	 * @author iKeirNez
	 * @param sender
	 * @param args
	 * @return
	 */
	
	// Get the closest matches for the string and return them
	public static List<Material> closestMatches(String input) {
        ArrayList<Material> matches = new ArrayList<Material>();
        
        for (Material mat : Material.values()){
        	// Quick fix for stone brick
        	if (input.equalsIgnoreCase("stonebrick")){
        		return Arrays.asList(Material.SMOOTH_BRICK);
        	} else if ((mat.name().replace("_", "").toLowerCase().equals(input.toLowerCase()) || String.valueOf(mat.getId()).equals(input)) && mat.isBlock()){
                return Arrays.asList(mat);
            } else if (mat.name().replace("_", "").toLowerCase().contains(input.toLowerCase()) && mat.isBlock()){
            	matches.add(mat);
            }
        }
        
        return matches;
    }
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player = (Player) sender;
			Location loc = (player.getLocation());
			
			// Subtract the Y coords by 1 to get the block at the players feet
			loc.setY(loc.getY() - 1);
			
			// If there are no args then set the block below to stone
			if (args.length == 0) {
				// Check if there is already a block below the player
				if (loc.getBlock().getTypeId() == 0) {
					LogHelper.showInfo("platformCreated", player, ChatColor.GREEN);
					loc.getBlock().setType(Material.STONE);
				} else {
					LogHelper.showInfo("platformBlockBelow", player, ChatColor.RED);
				}
			} else if (args.length == 1) {
				if (loc.getBlock().getTypeId() == 0) {
					List <Material> list = closestMatches(args[0]);
					
					// If the list is empty then display an error messages
					if (list.size() == 0) {
						LogHelper.showInfo("platformBlockNotFound", player, ChatColor.RED);
						
					// If the list has one match then set the block to that
					} else if (list.size() == 1) {
						loc.getBlock().setType(list.get(0));
						LogHelper.showInfo("platformCreated", player, ChatColor.GREEN);
						
					// If there are multiple matches then display it to the player
					} else {
						LogHelper.showInfo("platformMultipleMatches#####" + "[" + Arrays.toString(list.toArray()).replace("[", "").replace("]", "").toLowerCase().replaceAll("_", ""), player, ChatColor.RED);
					}
					
				// Check if there is already a block below the player
				} else {
					LogHelper.showInfo("platformBlockBelow", player, ChatColor.RED);
				}
				
			// Send an error message if the player uses more than 1 argument
			} else {
				LogHelper.showInfo("incorrectUsage", player, ChatColor.RED);
			}
		} else {
			// Console, need to find out lang string
		}
		return true;
	}
	
}
