package com.github.ikeirnez.commandsex.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.github.ikeirnez.commandsex.CommandsEX;
import com.github.ikeirnez.commandsex.api.HackedCommand;
import com.github.ikeirnez.commandsex.api.interfaces.ICommand;

public class Command_cex implements ICommand {

    public HackedCommand init(CommandsEX cex) {
        return new HackedCommand("cex", "Displays info about CommandsEX", "", new ArrayList<String>());
    }

    public boolean run(CommandSender sender, String[] args, String alias, CommandsEX cex) {
        sender.sendMessage(ChatColor.AQUA + "Test :D");
        return false;
    }

}
