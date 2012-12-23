package com.github.ikeirnez.commandsex;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HackedCommand extends Command {

    private CommandExecutor exe = null;
    
    public HackedCommand(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, ("/<command> " + usageMessage).trim(), aliases);
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
