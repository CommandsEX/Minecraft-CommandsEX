package com.github.zathrus_writer.commandsex;

import java.lang.reflect.Constructor;
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
	public final Logger l = Logger.getLogger("Minecraft");
	
	// language constants
	public final String langInWorldCommandOnly = "This command can only be used in-world.";
	public final String langInsufficientPerms = "You don't have sufficient permissions to use this command.";
	public final String langEnableMsg = " has successfully started.";
	public final String langDisableMsg = " has been disabled.";
	public final String langVersion = "version";
	public final String langInternalError = "An internal error has occured. Please try again or contact an administrator.";
	
	// classnames of all our plugin components
	public final String[] extenders = {"Teleportation"};

	// list of all available commands for our plugin, dynamically expanded via extending classes, used for commands handling
	// note: the list is stored in the format of "methodName:className", so we can easily implement onCommand which will binary-search
	//       through these values for the "command:" string and then we can dynamically invoke our function along with our parameters
	public List<String> commands = new ArrayList<String>();
	
	// holds instances of successfully loaded classes, so we don't re-initialize them again when we need them later
	private List<Class<?>> extenderClassInstances = new ArrayList<Class<?>>();
	
	
	
	/***
	 * Checks if the CommandSender is a Player, gives the sender error message if he's not and returns Boolean value.
	 * @param cs
	 * @return
	 */
	public Boolean checkIsPlayer(CommandSender cs) {
		if (cs instanceof Player) {
			return true;
		} else {
			cs.sendMessage(ChatColor.RED + langInWorldCommandOnly);
			return false;
		}
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
		// load all extending classes we have listed in extenders array and let them initialize their stuff (commands, events...)
		Class<?>[] proto = new Class[] {CommandsEX.class};
		Object[] params = new Object[] {this};

		for (String cName : this.extenders) {
			try {
				Class<?> c = Class.forName("com.github.zathrus_writer.commandsex." + cName);
				Constructor<?> ct = c.getConstructor(proto);
				Object obj = ct.newInstance(params);
				extenderClassInstances.add(obj.getClass());
			} catch(ClassNotFoundException e) {
				// all ok if class was not found, this is a modular plugin
		    } catch(Exception e) {
		    	// all other errors, we need to log these to console
		    	this.l.severe("Error loading class " + cName + " (" + e.getMessage() + ")");
		    }
		}

		PluginDescriptionFile pdfFile = this.getDescription();
		this.l.info(pdfFile.getName() + " " + langVersion + " " + pdfFile.getVersion() + langEnableMsg);
	}

	
	/***
	 * OnDisable
	 */
	public void onDisable() {
		this.l.info(this.getDescription().getName() + langDisableMsg);
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
				if (sCommand.startsWith(cmd + ":")) {
					String[] s = sCommand.split(":");
					for (Class<?> c : extenderClassInstances) {
						if (c.getSimpleName().equals(s[1])) {
							try {
								Class<?>[] proto = new Class[] {Player.class, String[].class};
								Object[] params = new Object[] {player, args};
								Method method = c.getDeclaredMethod(s[0], proto);
								Object ret = method.invoke(c, params);
								return Boolean.TRUE.equals(ret);
							} catch (Throwable e) {
								player.sendMessage(ChatColor.RED + this.langInternalError);
			    				this.l.severe("Couldn't handle function call " + cmd + " (" + s[0] + ") for class " + s[1] + ", error returned: " + e.getMessage());
			    				return true;
			    			}
						}
		    		}
					
					// we should never get here, except for errors
					player.sendMessage(ChatColor.RED + this.langInternalError);
	        		this.l.severe("Command /" + cmd + " did not match any known function for player "+ player.getDisplayName() +"!");
				}
    		}
    	}
        return true;
	}
}
