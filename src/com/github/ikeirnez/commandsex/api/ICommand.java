package com.github.ikeirnez.commandsex.api;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import com.github.ikeirnez.commandsex.CommandsEX;

public interface ICommand {

    public void init(CommandsEX cex, FileConfiguration config);
    public boolean run(CommandSender sender, String[] args, String alias, CommandsEX cex, FileConfiguration config);
    
}
