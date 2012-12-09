package com.github.ikeirnez.commandsex.api.interfaces;

import org.bukkit.command.CommandSender;

import com.github.ikeirnez.commandsex.CommandsEX;
import com.github.ikeirnez.commandsex.HackedCommand;

public interface ICommand {

    public HackedCommand init(CommandsEX cex);
    public boolean run(CommandSender sender, String[] args, String alias, CommandsEX cex);
    
}
