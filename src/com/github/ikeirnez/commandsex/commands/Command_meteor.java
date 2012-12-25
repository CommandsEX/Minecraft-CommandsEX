package com.github.ikeirnez.commandsex.commands;

import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.github.ikeirnez.commandsex.CommandsEX;
import com.github.ikeirnez.commandsex.api.ICommand;
import com.github.ikeirnez.commandsex.handlers.Meteor;

public class Command_meteor implements ICommand {
	private Random rand = new Random();

	@Override public void init(CommandsEX cex, FileConfiguration config) {
	    
	}

	@Override public boolean run(CommandSender sender, String[] args, String alias, CommandsEX cex, FileConfiguration config) {
        if (!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "This command can only be used in-game");
            return true;
        }        
        Player player = (Player) sender;
        List<Block> targets = player.getLastTwoTargetBlocks(null, 64);
        Location target = targets.get(1).getLocation();
        target = player.getLocation();//TODO removed after test
        
        new Meteor(findMeteorSpawn(target), target);//TODO arguments for explosion size
        sender.sendMessage(ChatColor.GREEN + "Meteor successfully fired at your crosshair location");
        return true;
	}
	
	private Location findMeteorSpawn(Location target) {
		Location start = target.getWorld().getHighestBlockAt(target).getLocation().add(0, 100, 0);
		int offsetX = 0;
		int offsetZ = 0;
		switch(rand.nextInt(8)) {
			case 1:
				offsetX = 50;
				break;
			case 2:
				offsetX = -50;
				break;
			case 3: 
				offsetZ = 50;
				break;
			case 4: 
				offsetZ = -50;
				break;
			case 5:
				offsetX = 50;
				offsetZ = 50;
				break;
			case 6:
				offsetX = -50;
				offsetZ = -50;
				break;
			case 7: 
				offsetX = -50;
				offsetZ = 50;
				break;
			case 8: 
				offsetX = 50;
				offsetZ = -50;
				break;
		}
		return start.add(offsetX, 0, offsetZ);
	}
}

