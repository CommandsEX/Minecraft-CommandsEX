package com.github.ikeirnez.commandsex;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.ikeirnez.commandsex.commands.CommandManager;
import com.github.ikeirnez.commandsex.handlers.EventManager;

public class CommandsEX extends JavaPlugin {

    public static CommandsEX plugin;
    public static Logger logger;
    public static CommandManager cmdManger;
    public static EventManager evntManager;
    
    public void onEnable(){
        plugin = this;
        logger = Bukkit.getLogger();
        cmdManger = new CommandManager();
        evntManager = new EventManager();
        
        PluginManager pm = getServer().getPluginManager();

        if (!(Bukkit.getServer() instanceof CraftServer)){
            logger.log(Level.WARNING, "Unfortunately CommandsEX is not compatible with custom CraftBukkit builds");
            logger.log(Level.WARNING, "Please alert the developers of your CraftBukkit build and we will add support for it!");
            pm.disablePlugin(this);
            return;
        }
        
        cmdManger.registerCommands();
        evntManager.registerEvents();
    }

    public void onDisable(){

    }

}
