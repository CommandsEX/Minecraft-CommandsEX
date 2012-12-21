package com.github.ikeirnez.commandsex;

import com.github.ikeirnez.commandsex.api.ICommand;
import com.github.ikeirnez.commandsex.commands.CommandManager;
import com.github.ikeirnez.commandsex.helpers.CommandExe;

public class Commands {

    public static boolean isCommandAvailable(String command){
        try {
            return Class.forName("com.github.ikeirnez.commandsex.commands.Command_" + command) != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    public void registerCommand(Class<?> clazz){
        try {
            ICommand icmd = (ICommand) clazz.newInstance();
            HackedCommand cc = icmd.init(CommandsEX.plugin, CommandsEX.plugin.getConfig());
            registerCommand(cc);
        } catch (Throwable e){
            e.printStackTrace();
        }
    }

    public void registerCommand(HackedCommand cmd) {
        CommandManager.cmap.register("", cmd);
        cmd.setExecutor(new CommandExe());
    }
    
}
