package com.github.ikeirnez.commandsex.api;

import org.bukkit.configuration.file.FileConfiguration;

import com.github.ikeirnez.commandsex.CommandsEX;

public interface IInit extends IEvent {

    public void init(CommandsEX cex, FileConfiguration config);
    
}
