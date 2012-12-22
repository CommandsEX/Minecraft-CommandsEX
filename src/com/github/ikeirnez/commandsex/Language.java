package com.github.ikeirnez.commandsex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

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
     * @param user The user (player) to get the language for
     * @return The current language of the user
     */
    public static String getUserLanguage(String user){
        // TODO get this to work, requires language database
        return "";
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
