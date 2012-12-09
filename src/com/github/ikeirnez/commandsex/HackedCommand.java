package com.github.ikeirnez.commandsex;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.ikeirnez.commandsex.helpers.Utils;

public class HackedCommand extends Command {

    private CommandExecutor exe = null;
    
    public HackedCommand(String name, String description, String usageMessage, String[] aliases) {
        super(name, description, ("/<command> " + usageMessage).trim(), Utils.stringArrayToList(aliases));
    }

    public boolean execute(CommandSender sender, String commandLabel,String[] args) {
        if(exe != null){
            return exe.onCommand(sender, this, commandLabel,args);
        }
        
        return false;
    }
   
    public void setExecutor(CommandExecutor exe){
        this.exe = exe;
    }
    
}
