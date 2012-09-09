package com.github.zathrus_writer.commandsex.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kitteh.tag.PlayerReceiveNameTagEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Nametags;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Handler_nametag implements Listener {
	
	public Handler_nametag(){
		if (CommandsEX.tagAPIPresent){
			CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
		}
	}
	
	@EventHandler
	public void onNameTag(PlayerReceiveNameTagEvent e){
		Player tagPlayer = e.getNamedPlayer();
		String pName = tagPlayer.getName();
		String tag = Nametags.getTag(pName);
		if (!tag.equals(pName)){
			tag = Utils.replaceChatColors(tag);
			e.setTag(tag);
		}
	}

}
