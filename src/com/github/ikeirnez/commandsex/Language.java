package com.github.ikeirnez.commandsex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.github.ikeirnez.commandsex.helpers.LogHelper;

/**
 * All helper methods to do with languages and translations
 */
public class Language {

    private static HashMap<String, Properties> langs;
    private static FileConfiguration config = CommandsEX.config;
    
    /**
     * Loads all available languages when instigated
     * @param plugin The CommandsEX instance
     */
    public Language(CommandsEX plugin){
        langs = new HashMap<String, Properties>();

        for (String s : config.getStringList("availableLanguages")){
            if (s.length() > 5){
                LogHelper.logWarning("Language " + s + " is too long, this language file will not be available");
                continue;
            }
            
            File langFile = new File(plugin.getDataFolder(), "lang_" + s + ".properties");
            if (!langFile.exists()){
                LogHelper.logWarning("Couldn't find language file for language " + s);
                continue;
            }

            Properties lang = new Properties();

            try {
                lang.load(new FileInputStream(langFile));
            } catch (FileNotFoundException e) {
                // This should never be thrown as we check above whether the file exists
                e.printStackTrace();
                continue;
            } catch (IOException e) {
                e.printStackTrace();
                LogHelper.logSevere("IO error when reading language file for language " + s);
                continue;
            }

            langs.put(s, lang);
            LogHelper.logDebug("Successfully loaded language " + s);
        }
    }

    /**
     * Gets a translation for a CommandSender, if console will return in default languages
     * @param sender The CommandSender to get the translation for
     * @param key The message to get
     * @param args Formatting arguments
     * @return The translated message
     */
    public static String getTranslationForSender(CommandSender sender, String key, Object...args){
        if (sender instanceof Player){
            return getTranslationForUser(sender.getName(), key, args);
        } else {
            return getTranslationForLanguage(getDefaultLanguage(), key, args);
        }
    }

    /**
     * Gets a translation for a user (player)
     * @param user The user (player) to get the message for
     * @param key The message to get
     * @param args Formatting arguments
     * @return The translated message
     */
    public static String getTranslationForUser(String user, String key, Object...args){
        return getTranslationForLanguage(getUserLanguage(user), key, args);
    }

    /**
     * Gets a translation in a language
     * @param language The language to get the message in
     * @param key The message to get
     * @param args Formatting arguments
     * @return The translated message
     */
    public static String getTranslationForLanguage(String language, String key, Object...args){
        return String.format(langs.get(language).getProperty(key), args);
    }

    /**
     * Gets the user's (player's) language
     * Returns null if the player does not have a language set (possibly never joined server)
     * 
     * @param user The user (player) to get the language for
     * @return The current language of the user
     */
    public static String getUserLanguage(String user){
        ResultSet rs = CommandsEX.database.query_res("SELECT lang FROM %prefix%userlangs WHERE user = ?", user);
        String lang = null;
        
        try {
            if (rs.next()){
                lang = rs.getString("lang");
            }
            
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return lang;
    }

    /**
     * Gets the server's default language
     * @return The server's default language
     */
    public static String getDefaultLanguage(){
        return config.getString("defaultLanguage");
    }

    /**
     * Gets the properties file for the specified language
     * @param lang The language to get
     * @return The properties file of the language
     */
    public static Properties getLanguage(String lang){
        return langs.get(lang);
    }

    /**
     * Gets all available languages on the server
     * @return A list of all available languages
     */
    public static List<String> getAvailableLanguages(){
        return config.getStringList("availableLanguages");
    }

}
