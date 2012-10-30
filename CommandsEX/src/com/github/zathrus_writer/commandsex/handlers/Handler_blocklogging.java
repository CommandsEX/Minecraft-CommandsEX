package com.github.zathrus_writer.commandsex.handlers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.painting.*;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.SQLManager;
import com.github.zathrus_writer.commandsex.helpers.BlockLogging;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.BlockLogging.BlockLoggingAction;

public class Handler_blocklogging implements Listener {
	//ID, PlayerName, Action, Time, Block, World, Location
	
	public Handler_blocklogging() {
		if(CommandsEX.getConf().getBoolean("BlockloggingEnabled")) {
			CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
			SQLManager.query("CREATE TABLE IF NOT EXISTS " + SQLManager.prefix +"BlockLogging (ID int NOT NULL AUTO_INCREMENT, PlayerName varchar(255) NOT_NULL, Action varchar(255) NOT_NULL, Time varchar(255) NOT_NULL, Block int NOT_NULL, World varchar(255) NOT_NULL, Location varchar(255) NOT_NULL, PRIMARY KEY (ID) )");
			LogHelper.logInfo("loggingEnabled");
			BlockLogging.enabled = true;
			BlockLogging.table = SQLManager.prefix + "BlockLogging";
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		BlockLogging.logEvent(e.getPlayer().getName(), BlockLoggingAction.BLOCK_BREAK, e.getBlock(), e.getBlock().getWorld(), e.getBlock().getLocation());
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		BlockLogging.logEvent(e.getPlayer().getName(), BlockLoggingAction.BLOCK_PLACE, e.getBlockPlaced(), e.getBlock().getWorld(), e.getBlock().getLocation());
	}

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		BlockLogging.logEvent(e.getPlayer().getName(), BlockLoggingAction.SIGN_CHANGE, e.getBlock(), e.getBlock().getWorld(), e.getBlock().getLocation());
	}
	
	@EventHandler
	public void onBlockForm(BlockFormEvent e) {
		BlockLogging.logEvent("ENVIROMENTAL_CHANGE", BlockLoggingAction.BLOCK_FORM, e.getBlock(), e.getBlock().getWorld(), e.getBlock().getLocation());
	}
	
	@EventHandler
	public void onBlockFade(BlockFadeEvent e) {
		BlockLogging.logEvent("ENVIROMENTAL_CHANGE", BlockLoggingAction.BLOCK_FADE, e.getBlock(), e.getBlock().getWorld(), e.getBlock().getLocation());
	}
	
	@EventHandler
	public void onBlockBurn(BlockBurnEvent e) {
		BlockLogging.logEvent("ENVIROMENTAL_CHANGE", BlockLoggingAction.BLOCK_BURN, e.getBlock(), e.getBlock().getWorld(), e.getBlock().getLocation());
	}
	
	@EventHandler
	public void onLeafDecay(LeavesDecayEvent e) {
		BlockLogging.logEvent("ENVIROMENTAL_CHANGE", BlockLoggingAction.LEAF_DECAY, e.getBlock(), e.getBlock().getWorld(), e.getBlock().getLocation());
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {
		for(Block b : e.blockList().toArray(new Block[0])) {
			BlockLogging.logEvent("ENVIROMENTAL_CHANGE", BlockLoggingAction.ENTITY_EXPLOSION, b, b.getWorld(), b.getLocation());
		}
	}
	
	@EventHandler
	public void onPaintingBreak(PaintingBreakByEntityEvent e) {
		BlockLogging.logEvent(((Player)e.getRemover()).getName(), BlockLoggingAction.PAINTING_BREAK, e.getPainting(), e.getPainting().getWorld(), e.getPainting().getLocation());
	}
	
	@EventHandler
	public void onPaintingPlace(PaintingPlaceEvent e) {
		BlockLogging.logEvent(e.getPlayer().getName(), BlockLoggingAction.PAINTING_PLACE, e.getPainting(), e.getPainting().getWorld(), e.getPainting().getLocation());
	}
	
	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent e) {
		if(e.getTo() == Material.AIR) {
			BlockLogging.logEvent("ENVIROMENTAL_CHANGE", BlockLoggingAction.ENDERMAN_PICKUP, e.getBlock(), e.getBlock().getWorld(), e.getBlock().getLocation());
		} else {
			BlockLogging.logEvent("ENVIROMENTAL_CHANGE", BlockLoggingAction.ENDERMAN_PLACE, e.getBlock(), e.getBlock().getWorld(), e.getBlock().getLocation());
		}
	}
	
}
