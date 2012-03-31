package com.github.zathrus_writer.commandsex.helpers;

import static com.github.zathrus_writer.commandsex.Language._;

import java.lang.reflect.Method;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CexCommands;
import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.Language;

public class Commands implements CommandExecutor {

	// list of commands this plugin should ignore - values come from a config file
	private CommandsEX plugin;
	
	/***
	 * Class constructor.
	 * @param plugin
	 */
	public Commands(CommandsEX plug) {
		this.plugin = plug;
	}
 
	@Override
	public boolean onCommand(CommandSender sender, Command command, String cmdAlias, String[] args) {
		String cmd = command.getName().toLowerCase();
		String alias = cmdAlias.toLowerCase();
		
		// log the command if invoked by a player
		if ((sender instanceof Player) && (this.plugin.getConfig().getBoolean("logCommands") == true)) {
			String arguments = " ";
			if (args.length > 0) {
				for (String a : args) {
					arguments = arguments + a + " ";
				}
			}
			LogHelper.logInfo("[" + sender.getName() + "] /" + (!alias.equals("") ? alias : cmd) + arguments);
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
		Boolean cmdFound = false;
		for(int i = 0; i <= cmdList.size() - 1; i++) {
			if (cmdList.get(i).getLabel().equals(commandName)) {
				cmdFound = true;
				String usage = "";
				// try to load description from alias first, since some commands (like /home, /warp) use this request type
				Language.noMissingLangWarning = true;
				String description = _("cmdDesc_" + alias, sender.getName());
				if (description.equals("cmdDesc_" + alias)) {
					description = _("cmdDesc_" + commandName, sender.getName());
					if (description.equals("cmdDesc_" + commandName)) {
						sender.sendMessage(ChatColor.WHITE + cmdList.get(i).getDescription());
						usage = _("cmdDesc_" + commandName + "_usage", sender.getName());
						if (usage.equals("cmdDesc_" + commandName + "_usage")) {
							usage = "";
						}
					} else {
						sender.sendMessage(ChatColor.WHITE + description);
						usage = _("cmdDesc_" + commandName + "_usage", sender.getName());
						if (usage.equals("cmdDesc_" + commandName + "_usage")) {
							usage = "";
						}
					}
				} else {
					sender.sendMessage(ChatColor.WHITE + description);
					usage = _("cmdDesc_" + alias + "_usage", sender.getName());
					if (usage.equals("cmdDesc_" + alias + "_usage")) {
						usage = "";
					}
				}
				Language.noMissingLangWarning = false;
				
				// load up usage if not loaded up from language file (for command alias - like /home invite) yet
				if (usage.equals("")) {
					usage = cmdList.get(i).getUsage();
				}

				// replace <command> with the actual alias
				usage = usage.replaceAll("<command>", alias);
				
				if (usage.contains("\n") || usage.contains("\r")) {
					usage.replaceAll("\r", "\n").replaceAll("\n\n", "");
					String[] splitted = usage.split("\n");
					sender.sendMessage(ChatColor.RED + _("usage", sender.getName()) + ":");
					for (String rVal : splitted) {
						sender.sendMessage(ChatColor.RED + rVal);
					}
				} else {
					sender.sendMessage(ChatColor.RED + _("usage", sender.getName()) + ": " + usage);
				}
			}
	    }
		
		// command not found
		if (!cmdFound) {
			LogHelper.showWarning("commandDescriptionNotFound", sender);
			LogHelper.logWarning(_("commandDescriptionNotFound", "") + ": " + commandName + ", alias = " + alias);
		}
	}
}
