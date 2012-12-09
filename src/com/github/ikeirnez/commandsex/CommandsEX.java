package com.github.ikeirnez.commandsex;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_4_5.CraftServer;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

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
            logger.log(Level.WARNING, "Please alert the developers of your CraftBukkit build and we may add support for it!");
            pm.disablePlugin(this);
            return;
        }
        
        cmdManger.registerCommands();
        evntManager.registerEvents();
        
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void onDisable(){

    }

}
