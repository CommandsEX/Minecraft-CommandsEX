package com.github.zathrus_writer.commandsex;


import java.lang.reflect.Method;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;

import static com.github.zathrus_writer.commandsex.Language._;

public class CommandsEX extends JavaPlugin {
	// our plugin :-)
	public static CommandsEX plugin;
	// regex to check if String is a number
	public final static String intRegex = "(-)?(\\d){1,10}(\\.(\\d){1,10})?";
	// plugin description file, used at least on 2 places, so it's here :-P
	public static PluginDescriptionFile pdfFile;
	// if true, the _() function won't try to load existing perUserLocales from DB - used in exceptions handling
	public static Boolean avoidDB = false;
	// our command listener class
	private Commands cListener;
	// if SQL is enabled for our plugin, this will be true,
	// otherwise it'll remain false... this variable removes
	// the need for unneccessary SQLManager class if we don't need it
	public static Boolean sqlEnabled = false;
	// list of all existing event listeners that may exist for this plugin
	private static String[] eventListenersList = {"PlayerChatListener", "PlayerCommandListener", "PlayerTeleportListener", "ServerCommandListener"};

	/***
	 * Class constructor.
	 * We need the static plugin declaration for getting config from other modules.
	 */
	public CommandsEX() {
		plugin = this;
	}
	
	/***
	 * OnEnable
	 */
	@Override
	public void onEnable() {
		// save default config if not saved yet
		getConfig().options().copyDefaults(true);
		saveConfig();

		// set up commands listener
		cListener = new Commands(this);

		// initialize translations
		Language.init(this);

		// get description file and display initial startup OK info
		pdfFile = this.getDescription();
		LogHelper.logInfo("[" + pdfFile.getName() + "] " + _("startupMessage", "") + " " + Language.defaultLocale);
		LogHelper.logInfo("[" + pdfFile.getName() + "] " + _("version", "") + " " + pdfFile.getVersion() + " " + _("enableMsg", ""));

		// initialize database, if we have it included in our build
		if (getConf().getBoolean("enableDatabase")) {
			Class<?>[] proto = new Class[] {this.getClass()};
			Object[] params = new Object[] {this};
			try {
				Class<?> c = Class.forName("com.github.zathrus_writer.commandsex.SQLManager");
				Method method = c.getDeclaredMethod("init", proto);
				method.invoke(null, params);
			} catch (ClassNotFoundException e) {
				// this is OK, since we won't neccessarily have this class in each build
			} catch (Throwable e) {
				LogHelper.logSevere(_("dbError", ""));
				LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
			}
		}
		
		// create default table structure if not created already
		if (sqlEnabled) {
			SQLManager.query("CREATE TABLE IF NOT EXISTS "+ SQLManager.prefix +"user2lang (username varchar(50) NOT NULL, lang varchar(5) NOT NULL, PRIMARY KEY (`username`))" + (SQLManager.sqlType.equals("mysql") ? " ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='stores per-user selected plugin language'" : ""));
		}

		// enable existing event listeners here
		if (eventListenersList.length > 0) {
			for (String s : eventListenersList) {
				try {
					Class.forName("com.github.zathrus_writer.commandsex.listeners." + s).newInstance();
				} catch (Throwable e) {}
			}
		}
	}

	/***
	 * Bukkit API requires us to bind each existing command individually
	 * to a separate class if we want to utilize such a class.
	 * Here, we'll just pass each command to the class' onCommand function,
	 * saving us the trouble of writing a list of all possible commands.
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		return cListener.onCommand(sender, cmd, commandLabel, args);
	}
	
	/***
	 * OnDisable
	 */
	@Override
	public void onDisable() {
		// if we don't have per-player language loaded from DB, do not try to load it now :-)
		avoidDB = true;
		// close all database connections
		SQLManager.close();
		LogHelper.logInfo("[" + this.getDescription().getName() + "] " + _("disableMsg", ""));
	}

	/***
	 * Returns current config.
	 * @return
	 */
	public static FileConfiguration getConf() {
		return plugin.getConfig();
	}
}