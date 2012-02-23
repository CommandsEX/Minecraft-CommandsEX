package com.github.zathrus_writer.commandsex;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandsEX extends JavaPlugin {

	// regex to check if String is a number
	public final String intRegex = "(-)?(\\d){1,10}(\\.(\\d){1,10})?";
	
	// logger
	public final static Logger LOGGER = Logger.getLogger("Minecraft");
	
	// language constants
	public final static String langInWorldCommandOnly = "This command can only be used in-world.";
	public final static String langInsufficientPerms = "You don't have sufficient permissions to use this command.";
	public final static String langEnableMsg = " has successfully started.";
	public final static String langDisableMsg = " has been disabled.";
	public final static String langVersion = "version";
	public final static String langInternalError = "An internal error has occured. Please try again or contact an administrator.";
	
	// classnames of all our plugin components
	public final String[] extenders = {"Teleportation"};

	// list of all available commands for our plugin, dynamically expanded via extending classes, used for commands handling
	// note: the list is stored in the format of "methodName:className", so we can easily implement onCommand which will binary-search
	//       through these values for the "command:" string and then we can dynamically invoke our function along with our parameters
	public List<String> commands = new ArrayList<String>();
	

	/***
	 * Checks if the CommandSender is a Player, gives the sender error message if he's not and returns Boolean value.
	 * @param cs
	 * @return
	 */
	public Boolean checkIsPlayer(CommandSender cs) {
		if (cs instanceof Player) {
			return true;
		}

		cs.sendMessage(ChatColor.RED + langInWorldCommandOnly);
		return false;
	}
	
	
	/***
	 * Returns name of the method that calls this function.
	 * @return
	 */
	public String getCurrentMethodNameForPerms() {
		return Thread.currentThread().getStackTrace()[3].getMethodName();
	}

	
	/***
	 * Check whether given player has permission to the function which called this method.
	 * This is determined from function name, so i.e. function /tploc will check for "cex.tploc" permission node.
	 * @param player
	 * @return
	 */
	public Boolean checkPerms(Player player) {
		return player.hasPermission("cex." + this.getCurrentMethodNameForPerms());
	}
	
	
	/***
	 * OnEnable
	 */
	public void onEnable() {
		// load all extending classes we have listed in extenders array, call their init() functions
		// and add all functions they offer for users into our commands list
		Class<?>[] proto = new Class[] {this.getClass()};
		Object[] params = new Object[] {this};

		for (String cName : this.extenders) {
			try {
				// no need to create instances here, just call init() and let them all live in peace
				Class<?> c = Class.forName("com.github.zathrus_writer.commandsex." + cName);
				Method method = c.getDeclaredMethod("init", proto);
				method.invoke(null, params);

				// we only add public methods as command functions
				Method[] methods = c.getMethods();
				for (Method m : methods) {
					String n = m.getName();
					if (n.startsWith("_")) {
						this.commands.add(n + ":" + c.getSimpleName());
					}
				}
			} catch(ClassNotFoundException e) {
				// all ok if class was not found, this is a modular plugin
		    } catch(Exception e) {
		    	// all other errors, we need to log these to console
		    	LOGGER.severe("Error loading class " + cName + " (" + e.getMessage() + ")");
		    }
		}

		PluginDescriptionFile pdfFile = this.getDescription();
		LOGGER.info(pdfFile.getName() + " " + langVersion + " " + pdfFile.getVersion() + langEnableMsg);
	}

	
	/***
	 * OnDisable
	 */
	public void onDisable() {
		LOGGER.info(this.getDescription().getName() + langDisableMsg);
	}
	
	
	/***
	 * NOTE: there is some room for improvement here, since iterating list of all commands isn't the quickest of searches
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (this.checkIsPlayer(sender)) {
    		Player player = (Player)sender;
    		String cmd = command.getName().toLowerCase();

    		// check if the command name if present in our list of commands, and if so, pass action to the appropriate function
    		for (String sCommand : this.commands) {
				// check for the correct class to be used
				if (sCommand.startsWith("_" + cmd + ":")) {
					String[] s = sCommand.split(":");
					try {
						Class<?>[] proto = new Class[] {Player.class, String[].class};
						Object[] params = new Object[] {player, args};
						Class<?> c = Class.forName("com.github.zathrus_writer.commandsex." + s[1]);
						Method method = c.getDeclaredMethod(s[0], proto);
						Object ret = method.invoke(null, params);
						return Boolean.TRUE.equals(ret);
					} catch (Throwable e) {
						player.sendMessage(ChatColor.RED + langInternalError);
						LOGGER.severe("Couldn't handle function call " + cmd + " (" + s[0] + ") for class " + s[1] + ", error returned: " + e.getMessage());
			    		return true;
			    	}
				}
					
				// we should never get here, except for errors
				player.sendMessage(ChatColor.RED + langInternalError);
				LOGGER.severe("Command /" + cmd + " did not match any known function for player "+ player.getDisplayName() +"!");
    		}
    	}
        return true;
	}
}
