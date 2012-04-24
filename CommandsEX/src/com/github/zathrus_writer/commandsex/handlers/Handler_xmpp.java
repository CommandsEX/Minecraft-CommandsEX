package com.github.zathrus_writer.commandsex.handlers;

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
		// first of all, load smack libraries
		try {
			ClasspathHacker.addFile(CommandsEX.plugin.getDataFolder() + "/smack.jar");
			ClasspathHacker.addFile(CommandsEX.plugin.getDataFolder() + "/smackx.jar");
		} catch (IOException e) {
			LogHelper.logSevere("[CommandsEX] " + _("xmppDownloadSmack", ""));
		}
		
		// now initialize the actual XMPP communication handling class
		new XMPPer();
	}
	
	/***
	 * Closes XMPP connection on plugin disable.
	 */
	public static void onDisable(CommandsEX p) {
		XMPPer.onDisable(p);
	}
}
