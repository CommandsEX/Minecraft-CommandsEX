package com.github.ikeirnez.commandsex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ikeirnez.commandsex.helpers.LogHelper;

public class Language {

    private static HashMap<String, Properties> langs;
    
    public Language(CommandsEX plugin, String[] availableLanguages){
        langs = new HashMap<String, Properties>();
        
        for (String s : availableLanguages){
            File langFile = new File(plugin.getDataFolder(), "lang_" + s + ".properties");
            if (!langFile.exists()){
                LogHelper.addToEventLog("Couldn't find language file for language " + s, true, Level.WARNING);
                continue;
            }
            
            Properties lang = new Properties();
            
            try {
                lang.load(new FileInputStream(langFile));
            } catch (FileNotFoundException e) {
                // This should never be thrown as we check above whether the file exists
                e.printStackTrace();
            } catch (IOException e) {
                LogHelper.addExceptionToEventLog(e);
            }
            
            langs.put(s, lang);
            LogHelper.addToEventLog("Successfully loaded language " + s);
        }
    }
    
    public static String getTranslationForSender(CommandSender sender, Object...args){
        if (sender instanceof Player){
            return getTranslationForUser(sender.getName());
        }
        
        return getTranslationForLanguage(getDefaultLanguage());
    }
    
    public static String getTranslationForUser(String user, Object...args){
        return getTranslationForLanguage(getUserLanguage(user));
    }
    
    public static String getTranslationForLanguage(String language, Object...args){
        
    }
    
    public static String getUserLanguage(String user){
        
    }
    
    public static String getDefaultLanguage(){
        
    }
    
    public static Properties getLanguage(String lang){
        
    }
    
    public static String[] getAvailableLanguages(){
        
    }
    
}
