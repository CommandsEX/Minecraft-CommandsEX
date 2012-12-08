package com.github.ikeirnez.commandsex.commands;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.v1_4_5.CraftServer;
import org.reflections.Reflections;

import com.github.ikeirnez.commandsex.CommandsEX;
import com.github.ikeirnez.commandsex.api.HackedCommand;
import com.github.ikeirnez.commandsex.api.interfaces.ICommand;
import com.github.ikeirnez.commandsex.helpers.CommandExe;

public class CommandManager {
    public static CommandMap cmap;

    /**
     * The constructor
     */
    public CommandManager(){
        try {
            Field f = CraftServer.class.getDeclaredField("commandMap");
            f.setAccessible(true);
            cmap = (CommandMap) f.get(Bukkit.getServer());
        } catch (Throwable e){
            e.printStackTrace();
        }
    }

    /**
     * Registers all CommandsEX commands
     * @return The amount of commands registered
     */
    public int registerCommands(){
        int ret = 0;
            // init cex command, this stops it being removed and also alerts classloader of package
            //registerCommand((new Command_cex()).init(CommandsEX.plugin));
        
            Reflections reflections = new Reflections("com.github.ikeirnez.commandsex.commands");
            Set<Class<? extends ICommand>> commandClasses = reflections.getSubTypesOf(ICommand.class);

            Iterator<Class<? extends ICommand>> it = commandClasses.iterator();
            while (it.hasNext()){
                Class<? extends ICommand> clazz = it.next();
                
                registerCommand(clazz);
                ret++;
            }
            
            /*for (Class<? extends Object> clazz : commandClasses){
                System.out.println(clazz.getName());
                registerCommand(clazz);
                ret++;
            }*/
            
        return ret;
    }

    /**
     * Registers a command via its class
     * @param clazz The command class to register
     */
    public void registerCommand(Class<?> clazz){
        try {
            // setup command
            ICommand icmd = (ICommand) clazz.newInstance();
            HackedCommand cc = icmd.init(CommandsEX.plugin);
            registerCommand(cc);
        } catch (Throwable e){
            e.printStackTrace();
        }
    }

    /**
     * Registers a command via its instanceof HackedCommand
     * @param cmd The instance of CCommand to register
     */
    public void registerCommand(HackedCommand cmd){
        cmap.register("", cmd);
        cmd.setExecutor(new CommandExe());
    }

}
