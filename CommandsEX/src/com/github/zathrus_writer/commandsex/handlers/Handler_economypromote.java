package com.github.zathrus_writer.commandsex.handlers;

import static com.github.zathrus_writer.commandsex.Language._;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.Vault;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Promotions;

public class Handler_economypromote extends Promotions implements Listener {

	public static Integer promotionTaskID;
	
	/***
	 * Create a periodic task that will promote or demote players based on their current wealth.
	 * Also, activate onQuit event listener, so we check for promotion when a player quits the game to be sure we have it all covered :)
	 */
	public Handler_economypromote() {
		// check if we have Vault present
		if (!CommandsEX.vaultPresent || !Vault.permsEnabled() || !Vault.ecoEnabled()) {
			LogHelper.logSevere(_("ecoPromoteNoVault", ""));
			return;
		}
		
		// set up the periodic task
		Integer taskTime = CommandsEX.getConf().getInt("ecoPromoteTaskTime");
		promotionTaskID = CommandsEX.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(CommandsEX.plugin, new Runnable() {

			@Override
			public void run() {
				// create ExecutorService to manage threads                        
				ExecutorService threadExecutor = Executors.newFixedThreadPool(1);
				threadExecutor.execute(new Runnable() {
					@Override
					public void run() {
						Handler_economypromote.checkEcoPromotions();
					}
				});
				threadExecutor.shutdown(); // shutdown worker threads
			}
			
		}, (20 * taskTime), (20 * taskTime));
		// start listening on player quit events
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	
	/***
	 * When a player quits, check and update his rank according to his wealth.
	 * @param e
	 * @return
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void quitPromote(PlayerQuitEvent e) {
		if (CommandsEX.vaultPresent && Vault.permsEnabled() && Vault.ecoEnabled()) {
			Handler_economypromote.checkEcoPromotions(e.getPlayer());
		}
	}
	
}
