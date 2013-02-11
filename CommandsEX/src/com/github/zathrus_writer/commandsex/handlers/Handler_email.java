package com.github.zathrus_writer.commandsex.handlers;

import java.io.File;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.ClasspathHacker;

public class Handler_email {

    public static File emailLibraryFile = new File(CommandsEX.plugin.getDataFolder(), "commons-email-1.2.jar");
	public static boolean classpathAdded = false;
	
	public Handler_email(){
		if (!emailLibraryFile.exists()){
			return;
		}

		try {
			ClasspathHacker.addFile(emailLibraryFile);
			classpathAdded = true;
		} catch (Exception e){
            e.printStackTrace();
		}
	}
	
}
