package com.github.ikeirnez.commandsex.helpers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.ikeirnez.commandsex.CommandsEX;
import com.github.ikeirnez.commandsex.api.ICommand;

public class CommandExe implements CommandExecutor {

    /**
     * This method acts as a command forwarder, this class forwards the command to the applicable class
     */
    
    public boolean onCommand(CommandSender sender, Command cmd, String cmdAlias, String[] args) {
        String c = cmd.getName().toLowerCase();
        cmdAlias = cmdAlias.toLowerCase();
        
        try {
            Class<?> clazz = Class.forName("com.github.ikeirnez.commandsex.commands.Command_" + c);
            Object instance = clazz.newInstance();
            ICommand iCmd = (ICommand) instance;
            return iCmd.run(sender, args, cmdAlias, CommandsEX.plugin, CommandsEX.plugin.getConfig());
        } catch (Throwable e){
            e.printStackTrace();
        }

        return true;
    }

}
