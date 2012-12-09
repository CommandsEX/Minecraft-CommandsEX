package com.github.ikeirnez.commandsex;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigOption {

    private FileConfiguration config = CommandsEX.plugin.getConfig();
    private String path;
    private Object def;
    private Object val;
    
    public ConfigOption(String path, Object def){
        this.path = path;
        this.def = def;
        
        if (config.contains(path)){
            this.val = config.get(path);
        } else {
            this.val = this.def;
        }
    }
    
    public String getPath(){
        return path;
    }
    
    public Object getDefault(){
        return def;
    }
    
    public Object getValue(){
        return val;
    }
    
    public void setValue(Object val){
        this.val = val;
    }
    
}
