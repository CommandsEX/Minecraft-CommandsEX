package com.github.zathrus_writer.commandsex.handlers;

import java.io.File;
import java.io.IOException;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.ClasspathHacker;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.XMPPer;

import static com.github.zathrus_writer.commandsex.Language._;

public class Handler_xmpp {

	/***
	 * Constructor, initiates XMPP connection based on config settings.
	 * @param plugin
	 */
	public Handler_xmpp() {
		// Check if the files exist, if they do the load the libaries and startup XMPPer
		if (new File(CommandsEX.plugin.getDataFolder() + "/smack.jar").exists() && new File(CommandsEX.plugin.getDataFolder() + "/smackx.jar").exists()){
			try {
				ClasspathHacker.addFile(CommandsEX.plugin.getDataFolder() + "/smack.jar");
				ClasspathHacker.addFile(CommandsEX.plugin.getDataFolder() + "/smackx.jar");
			} catch (IOException e){
				LogHelper.logSevere(_("xmppSmackReadError", ""));
			}

			// now initialize the actual XMPP communication handling class if smack is installed
			new XMPPer();
		} else {
			LogHelper.logSevere(_("xmppDownloadSmack", ""));
		}
	}

	/***
	 * Closes XMPP connection on plugin disable.
	 */
	public static void onDisable(CommandsEX p) {
		XMPPer.onDisable(p);
	}
}
