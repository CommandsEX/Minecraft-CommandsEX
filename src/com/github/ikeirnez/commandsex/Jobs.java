package com.github.ikeirnez.commandsex;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;

import com.github.ikeirnez.commandsex.helpers.LogHelper;

/**
 * Class to handle methods that need executed when the plugin is reloaded or disabled
 * This will most commonly be used for saving configs or writing to the database
 */
public class Jobs {

    private static HashMap<Class<?>, String> reloadJobs = new HashMap<Class<?>, String>();
    private static HashMap<Class<?>, String> disableJobs = new HashMap<Class<?>, String>();
    
    /**
     * Adds a method to be executed when the plugin is reloaded
     * The method must take the parameters CommandsEX and FileConfiguration
     * @param clazz The class the executed method is located in
     * @param method The name of the method to be executed on plugin reload
     */
    public static void addReloadJob(Class<?> clazz, String method){
        reloadJobs.put(clazz, method);
    }
    
    /**
     * Adds a method to be executed when the plugin is disabled
     * The method must take the parameters CommandsEX and FileConfiguration
     * @param clazz The class the executed method is located in
     * @param method The name of the method to be executed on plugin disable
     */
    public static void addDisableJob(Class<?> clazz, String method){
        disableJobs.put(clazz, method);
    }
    
    /**
     * Executes the reload jobs, this should ONLY be executed when the plugin is actually reloading
     */
    public static void doReloadJobs(){
        for (Class<?> clazz : reloadJobs.keySet()){
            try {
                Method m = clazz.getDeclaredMethod(reloadJobs.get(clazz), CommandsEX.class, FileConfiguration.class);
                m.invoke(null, CommandsEX.plugin, CommandsEX.plugin.getConfig());
            } catch (Exception e) {
                LogHelper.addExceptionToEventLog(e);
            }
        }
    }
    
    /**
     * Executes the disable jobs, this should ONLY be executed when the plugin is actually disabling
     */
    public static void doDisableJobs(){
        for (Class<?> clazz : disableJobs.keySet()){
            try {
                Method m = clazz.getDeclaredMethod(reloadJobs.get(clazz), CommandsEX.class, FileConfiguration.class);
                m.invoke(null, CommandsEX.plugin, CommandsEX.plugin.getConfig());
            } catch (Exception e) {
                LogHelper.addExceptionToEventLog(e);
            }
        }
    }
    
}
