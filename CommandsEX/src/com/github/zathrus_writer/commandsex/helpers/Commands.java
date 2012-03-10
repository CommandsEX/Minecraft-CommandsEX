package com.github.zathrus_writer.commandsex.helpers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CexCommands;
import com.github.zathrus_writer.commandsex.CommandsEX;
import static com.github.zathrus_writer.commandsex.Language._;

public class Commands implements CommandExecutor {

	// list of commands this plugin should ignore - values come from a config file
	private List<String> ignoredCommands = new ArrayList<String>();
	private CommandsEX plugin;
	
	/***
	 * Class constructor.
	 * @param plugin
	 */
	public Commands(CommandsEX plug) {
		this.plugin = plug;
		this.ignoredCommands = plug.getConfig().getStringList("disabledCommands");
	}
 
	@Override
	public boolean onCommand(CommandSender sender, Command command, String cmdAlias, String[] args) {
		String cmd = command.getName().toLowerCase();
		String alias = cmdAlias.toLowerCase();
		
		// first of all - check if this command shouldn't be ignored by our plugin
		if (this.ignoredCommands.contains(cmd) || this.ignoredCommands.contains(alias)) {
			return true;
		}
		
		// log the command if invoked by a player
		if ((sender instanceof Player) && (this.plugin.getConfig().getBoolean("logCommands") == true)) {
			String arguments = " ";
			if (args.length > 0) {
				for (String a : args) {
					arguments = arguments + a + " ";
				}
			}
			LogHelper.logInfo("[" + sender.getName() + "] /" + alias + arguments);
		}

		try {
			// the only exception to dynamic calls for now = calling /cex command directly from this class
			if (cmd.equals("cex")) {
				return this.command_cex(sender, cmdAlias, args);
			} else {
				Class<?>[] proto = new Class[] {CommandSender.class, String.class, String[].class};
				Object[] params = new Object[] {sender, cmdAlias, args};
				Class<?> c = Class.forName("com.github.zathrus_writer.commandsex.commands.Command_" + cmd);
				Method method = c.getDeclaredMethod("run", proto);
				Object ret = method.invoke(null, params);
				return Boolean.TRUE.equals(ret);
			}
		} catch (Throwable e) {
			LogHelper.showWarning("internalError", sender);
			LogHelper.logSevere("[CommandsEX] Couldn't handle function call '" + cmd + "'");
			LogHelper.logDebug("Message: " + e.getMessage() + ", cause: "+e.getCause());
    		return true;
    	}
	}
	
	/***
	 * Handles control over to CexCommands section,
	 * so the class can use our base class for managing configuration.
	 * @param sender
	 * @param alias
	 * @param args
	 * @return
	 */
	public Boolean command_cex(CommandSender sender, String alias, String[] args) {
		return CexCommands.handle_cex(this.plugin, sender, alias, args);
	}
	
	/***
	 * Gets help text and usage for a command and returns it in a 2-dimensional array for help purposes.
	 * @param commandName
	 * @return
	 */
	public static void showCommandHelpAndUsage(CommandSender sender, String commandName, String alias) {
		List<Command> cmdList = PluginCommandYamlParser.parse(CommandsEX.plugin);
		for(int i = 0; i <= cmdList.size() - 1; i++) {
			if (cmdList.get(i).getLabel().equals(commandName)) {
				// check if we have description in our language file, otherwise load it up from the plugin YAML file
				String description = _("cmdDesc_" + commandName, sender.getName());
				if (description.equals("cmdDesc_" + commandName)) {
					sender.sendMessage(ChatColor.WHITE + cmdList.get(i).getDescription());
				} else {
					sender.sendMessage(ChatColor.WHITE + description);
				}
				String usage = cmdList.get(i).getUsage().replaceAll("<command>", alias);
				if (usage.contains("\n") || usage.contains("\r")) {
					usage.replaceAll("\r", "\n").replaceAll("\n\n", "");
					String[] splitted = usage.split("\n");
					sender.sendMessage(ChatColor.WHITE + _("usage", sender.getName()) + ":");
					for (String rVal : splitted) {
						sender.sendMessage(ChatColor.WHITE + rVal);
					}
				} else {
					sender.sendMessage(ChatColor.WHITE + _("usage", sender.getName()) + ": " + usage);
				}
			}
	    }
	}
}
