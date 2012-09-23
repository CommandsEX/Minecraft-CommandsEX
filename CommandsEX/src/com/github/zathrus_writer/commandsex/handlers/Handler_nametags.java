package com.github.zathrus_writer.commandsex.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.github.zathrus_writer.commandsex.helpers.Nametags;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Handler_nametags implements Listener {

	/**
	 * Events are registered inside the Nametags class, this prevents errors
	 * when TagAPI is not available
	 */
	
	@EventHandler
	public void onNameTag(org.kitteh.tag.PlayerReceiveNameTagEvent e){
		Player tagPlayer = e.getNamedPlayer();
		String pName = tagPlayer.getName();
		String tag = Nametags.getTag(pName);
		if (!tag.equals(pName)){
			tag = Utils.replaceChatColors(tag);
			e.setTag(tag);
		}
	}
	
}
