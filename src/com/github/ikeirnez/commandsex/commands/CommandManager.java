package com.github.ikeirnez.commandsex.commands;

import java.lang.reflect.Field;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.v1_4_6.CraftServer;
import org.reflections.Reflections;

import com.github.ikeirnez.commandsex.Commands;
import com.github.ikeirnez.commandsex.api.ICommand;

public class CommandManager extends Commands {
    public static CommandMap cmap = null;

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
            Reflections reflections = new Reflections("com.github.ikeirnez.commandsex.commands");
            Set<Class<? extends ICommand>> commandClasses = reflections.getSubTypesOf(ICommand.class);

            for (Class<? extends Object> clazz : commandClasses){
                registerCommand(clazz);
                ret++;
            }
            
        return ret;
    }
}
