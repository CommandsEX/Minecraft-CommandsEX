package com.github.ikeirnez.commandsex.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import com.github.ikeirnez.commandsex.CommandsEX;
import com.github.ikeirnez.commandsex.HackedCommand;
import com.github.ikeirnez.commandsex.api.ICommand;

public class Command_cex implements ICommand {

    public HackedCommand init(CommandsEX cex, FileConfiguration config) {
        return new HackedCommand("cex", "Displays info about CommandsEX", "", new String[] {});
    }

    public boolean run(CommandSender sender, String[] args, String alias, CommandsEX cex, FileConfiguration config) {
        sender.sendMessage(ChatColor.AQUA + "Test :D");
        return false;
    }

}
