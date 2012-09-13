package com.github.zathrus_writer.commandsex.handlers;

import java.io.File;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.ClasspathHacker;

public class Handler_email {

	public static boolean classpathAdded = false;
	
	public Handler_email(){
		File f = new File(CommandsEX.plugin.getDataFolder() + "/mail.jar");

		if (!f.exists()){
			return;
		}

		try {
			ClasspathHacker.addFile(f);
			classpathAdded = true;
		} catch (Exception e){
		}
	}
	
}
