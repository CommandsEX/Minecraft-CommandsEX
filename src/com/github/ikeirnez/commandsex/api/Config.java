package com.github.ikeirnez.commandsex.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import com.github.ikeirnez.commandsex.CommandsEX;
import com.github.ikeirnez.commandsex.ConfigOption;

public class Config {

    private static List<ConfigOption> cOptions = new ArrayList<ConfigOption>();
    private static FileConfiguration config = CommandsEX.plugin.getConfig();
    
    public static void addConfigOption(ConfigOption co){
        addConfigOption(co, true);
    }
    
    public static void addConfigOption(ConfigOption co, boolean isDefault){
        if (cOptions.contains(co)){
            cOptions.remove(co);
        }
        
        cOptions.add(co);
    }
    
    public static ConfigOption getConfigOption(String path){
        for (ConfigOption co : cOptions){
            if (co.getPath().equals(path)){
                return co;
            }
        }
        
        return null;
    }
    
    public static void setConfigOption(ConfigOption co){
        ConfigOption existing = getConfigOption(co.getPath());
        
        if (existing != null){
            cOptions.remove(existing);
        }
        
        cOptions.add(co);
        config.set(co.getPath(), co.getValue());
        CommandsEX.plugin.saveConfig();
    }
    
    public static void saveConfig(){
        for (ConfigOption co : cOptions){
            config.set(co.getPath(), co.getValue());
        }
    }
    
}
