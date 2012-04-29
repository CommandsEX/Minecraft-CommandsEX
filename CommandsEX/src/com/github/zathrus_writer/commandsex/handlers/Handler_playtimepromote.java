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

public class Handler_playtimepromote extends Promotions implements Listener {

	public static Integer promotionTaskID;
	
	/***
	 * Create a periodic task that will promote players based on total time they played on the server.
	 * Also, activate onQuit event listener, so we check for promotion when a player quits the game to be sure we have it all covered :)
	 */
	public Handler_playtimepromote() {
		// check if we have Vault present
		if (!CommandsEX.vaultPresent || !Vault.permsEnabled()) {
			LogHelper.logSevere(_("timedPromoteNoVault", ""));
			return;
		}
		
		// set up the periodic task
		Integer taskTime = CommandsEX.getConf().getInt("timedPromoteTaskTime");
		
		// make sure taskTime is not lower than CommandsEX player playtimes flush time
		if (taskTime < CommandsEX.playTimesFlushTime) {
			taskTime = CommandsEX.playTimesFlushTime;
			LogHelper.logWarning(_("timedPromoteFlushTimeLow", "") + CommandsEX.playTimesFlushTime + " " + _("seconds", ""));
		}
		
		promotionTaskID = CommandsEX.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(CommandsEX.plugin, new Runnable() {

			@Override
			public void run() {
				// create ExecutorService to manage threads                        
				ExecutorService threadExecutor = Executors.newFixedThreadPool(1);
				threadExecutor.execute(new Runnable() {
					@Override
					public void run() {
						Handler_playtimepromote.checkTimedPromotions();
					}
				});
				threadExecutor.shutdown(); // shutdown worker threads
			}
			
		}, (20 * taskTime), (20 * taskTime));
		// start listening on player quit events
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	
	/***
	 * When a player quits, check and update his rank according to his playtime.
	 * Note: this needs to be at low priority, so we still have player's play time in stored
	 *       in main CommandsEX class (time gets emptied on NORMAL priority there when player quits)
	 * @param e
	 * @return
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void quitPromote(PlayerQuitEvent e) {
		if (CommandsEX.vaultPresent && Vault.permsEnabled()) {
			Handler_playtimepromote.checkTimedPromotions(e.getPlayer());
		}
	}
	
}
