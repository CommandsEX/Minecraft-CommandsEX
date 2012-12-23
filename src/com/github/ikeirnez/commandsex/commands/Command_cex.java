package com.github.ikeirnez.commandsex.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import com.github.ikeirnez.commandsex.CommandsEX;
import com.github.ikeirnez.commandsex.api.ACommand;
import com.github.ikeirnez.commandsex.api.ICommand;

@ACommand(command = "cex", description = "Displays information about CommandsEX", aliases = "cex_about, cex_info")
public class Command_cex implements ICommand {

    public void init(CommandsEX cex, FileConfiguration config) {
        
    }

    public boolean run(CommandSender sender, String[] args, String alias, CommandsEX cex, FileConfiguration config) {
        sender.sendMessage(ChatColor.AQUA + "Test :D");
        return false;
    }

}
