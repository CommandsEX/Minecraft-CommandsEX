package com.github.zathrus_writer.commandsex.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.zathrus_writer.commandsex.CommandsEX;
import static com.github.zathrus_writer.commandsex.Language._;

/***
 * Contains set of functions and event listeners used to handle quizzes-related functionality.
 * @author zathrus-writer
 *
 */
public class Quizzes implements Listener {

	public static Quizzes p = null;
	public static String version = "1.0";
	public static Map<String, String> lastRewards = new HashMap<String, String>(); // difficulty : last_reward_name
	// this map contains name of the players who won a quiz and their reward
	public static Map<String, String> quizWinners = new HashMap<String, String>(); // player_name : reward_name
	public static Integer lastQuizTime;
	public static Boolean quizRunning = false;
	public static Boolean quizCaseSensitive;
	public static String quizQuestion;
	public static String quizDifficulty;
	public static List<String> quizAnswers = new ArrayList<String>();
	public static Integer quizTask;
	
	public Quizzes() {
		p = this;
	}
	
	/***
	 * INIT - if set in config, this function will activate a periodic timer that will throw quizzes
	 * @return
	 */
	public static void init(CommandsEX plugin) {
		// create Quizzes class instance, if not instantiated yet to allow for events listening
		if (p == null) {
			new Quizzes();
		}
				
		Integer repeatTime = CommandsEX.getConf().getInt("quizRepeatTime", 0);
		Quizzes.lastQuizTime = Utils.getUnixTimestamp(0L);
		if (repeatTime > 0) {
			// set up recurring task that throws a new quiz
			quizTask = CommandsEX.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(CommandsEX.plugin, new Runnable() {
				@Override
			    public void run() {
					Quizzes.lastQuizTime = Utils.getUnixTimestamp(0L);
					if (Bukkit.getOnlinePlayers().length > 0) {
						Quizzes.start();
					}
			    }
			}, (20 * repeatTime), (20 * repeatTime));
		}
	}
	
	/***
	 * QUIZ - shows the quiz module version
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean quiz(CommandSender sender, String[] args, String command, String alias, Boolean... omitMessage) {
		if (!Utils.checkCommandSpam((Player) sender, "quiz-version")) {
			LogHelper.showInfo("quiz#####[, #####version#####[ " + version, sender);
		}
		return true;
	}
	
	/***
	 * QREMAIN - shows remaining time until the next quiz
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean qremain(CommandSender sender, String[] args, String command, String alias, Boolean... omitMessage) {
		if (!Utils.checkCommandSpam((Player) sender, "quiz-status")) {
			if (CommandsEX.getConf().getInt("quizRepeatTime", 0) > 0) {
				Integer stamp = Utils.getUnixTimestamp(0L);
				Integer timeRemaining = (lastQuizTime + CommandsEX.getConf().getInt("quizRepeatTime", 0) - stamp);
				if (timeRemaining < 120) {
					// seconds remaining
					LogHelper.showInfo("quizTimeToNext#####[" + timeRemaining + " #####seconds", sender);
				} else if (timeRemaining < 7200) {
					// minutes remaining
					LogHelper.showInfo("quizTimeToNext#####[" + ((int) (timeRemaining / 60)) + " #####minutes", sender);
				} else if (timeRemaining < 172800) {
					// hours remaining
					LogHelper.showInfo("quizTimeToNext#####[" + ((int) ((timeRemaining / 60) / 60)) + " #####hours", sender);
				} else {
					// days remaining
					LogHelper.showInfo("quizTimeToNext#####[" + ((int) (((timeRemaining / 60) / 60) / 24)) + " #####days", sender);
				}
			} else {
				if (quizRunning) {
					LogHelper.showInfo("quizRunning", sender);
				} else {
					LogHelper.showInfo("quizNotPlanned", sender);
				}
			}
		}
		return true;
	}
	
	/***
	 * CLAIM - claims price for a winning player
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean claim(CommandSender sender, String[] args, String command, String alias, Boolean... omitMessage) {
		if (!Utils.checkCommandSpam((Player) sender, "quiz-claim")) {
			// check if the player won anything
			Player p = (Player) sender;
			String pName = p.getName();
			if (quizWinners.containsKey(pName)) {
				// load all items for this player's reward
				FileConfiguration f = CommandsEX.getConf();
				ConfigurationSection configGroups = f.getConfigurationSection("quizDiff." + quizWinners.get(pName));
				Set<String> s = configGroups.getKeys(false);
				
				// first of all, count all rewards and see if they'd fit into player's inventory
				Integer allBlocks = 0;
				Inventory pi = p.getInventory();
				Integer maxStackSize = pi.getMaxStackSize();
				for (String reward : s) {
					Integer blockCount = f.getInt("quizDiff." + quizWinners.get(pName) + "." + reward);
					if (blockCount > maxStackSize) {
						allBlocks = (int) (allBlocks + Math.ceil(blockCount / maxStackSize));
					} else {
						allBlocks++;
					}
				}
				
				// calculate available slots
				Integer fullSlots = 0;
				for (ItemStack istack : p.getInventory().getContents()) {
					if ((istack != null) && istack.getAmount() > 0) {
						fullSlots++;
					}
				}
	
				if ((pi.getSize() - fullSlots) >= allBlocks) {
					// fill player's inventory with the reward
					for (String reward : s) {
						try {
							if (reward.contains(":")) {
								String[] expl = reward.split(":");
								pi.addItem(new ItemStack(Integer.parseInt(expl[0]), f.getInt("quizDiff." + quizWinners.get(pName) + "." + reward), (short) 0, Byte.parseByte(expl[1])));
							} else {
								pi.addItem(new ItemStack(Integer.parseInt(reward), f.getInt("quizDiff." + quizWinners.get(pName) + "." + reward)));
							}
						} catch (Throwable e) {
							// unable to add item into inventory, inform server owner
							LogHelper.logSevere("[CommandsEX] " + _("quizUnableToAddItem", "") + reward + ":" + f.getInt("quizDiff." + quizWinners.get(pName) + "." + reward));
							LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
						}
					}
					
					// tell player to check inventory for rewards
					LogHelper.showInfo("quizRewardsInYourInventory", p);
					
					// remove rewarded player from winners list
					quizWinners.remove(pName);
				} else {
					// not sure if we could fit reward in, better let player empty their inventory
					LogHelper.showInfo("quizInsufficientSpace", sender);
				}
			} else {
				// not a winner
				LogHelper.showInfo("quizNotAWinner", sender);
			}
		}
		return true;
	}
	
	/***
	 * START - forces start of a new quiz
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static void start() {
		// check if we don't have a quiz running already
		if (quizRunning) {
        	// cancel old quiz
        	HandlerList.unregisterAll(Quizzes.p);
        	CommandsEX.plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + _("quizEnd1", ""));
        	CommandsEX.plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + _("quizEnd2", ""));
        	quizRunning = false;
        }
		
		try {
			// start new quiz
			FileConfiguration f = CommandsEX.getConf();
			ConfigurationSection configGroups = f.getConfigurationSection("quizzes");
			Set<String> s = configGroups.getKeys(false);
			Integer len = s.size();
			String quizName = null;
			
			if (len > 0) {
				// pick a random one
				Integer pos = (0 + (int)(Math.random() * ((len - 1) + 1)));
				Object[] quizNames = s.toArray();
				quizName = (String) quizNames[pos];
				quizQuestion = f.getString("quizzes." + quizName + ".question", "Ping");
				quizDifficulty = f.getString("quizzes." + quizName + ".difficulty", "easy");
				quizCaseSensitive = f.getBoolean("quizDiff." + quizDifficulty + ".caseSentisive", false);
				quizAnswers = (List<String>) f.getList("quizzes." + quizName + ".answers");
				Integer quizDelay = f.getInt("quizDelay", 10);
				
				// make answers lowecase if the answer we expect does not depend on letters case
				if (!quizCaseSensitive) {
					ListIterator<String> iterator = quizAnswers.listIterator();
				    while (iterator.hasNext())
				    {
				    	Object o = iterator.next();
				        iterator.set(("" + o).toLowerCase());
				    }
				}
				
				// show information about a quiz starting soon
				CommandsEX.plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + _("quisStartsIn", "") + quizDelay + " " + _("seconds", "") + "!");
				
				// time the quiz start
				CommandsEX.plugin.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new Runnable() {
					@Override
					public void run() {
						// ask the question
						CommandsEX.plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + _("quizQuestion", "") + Quizzes.quizQuestion);
						
						// start listening for chat responses
						Quizzes.quizRunning = true;
						CommandsEX.plugin.getServer().getPluginManager().registerEvents(Quizzes.p, CommandsEX.plugin);
						
						// schedule quiz end
						CommandsEX.plugin.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new Runnable() {
							@Override
							public void run() {
								// cancel the quiz
								if (Quizzes.quizRunning) {
						        	HandlerList.unregisterAll(Quizzes.p);
						        	CommandsEX.plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + _("quizEnd1", ""));
						        	CommandsEX.plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + _("quizEnd2", ""));
						        	quizRunning = false;
								}
							}
						}, (20 * CommandsEX.getConf().getInt("quizDuration", 60)));
					}
				}, (20 * quizDelay));
			}
		} catch (Throwable ex) {
			// no quizzes
		}
	}
	
	/***
	 * QSTART - command counterpart to force start of a new quiz when requested from a player or console
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean qstart(CommandSender sender, String[] args, String command, String alias, Boolean... omitMessage) {
		start();
		return true;
	}
	
	/***
	 * Monitors chat events for correct answers to currently active quiz.
	 * @param e
	 * @return
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void checkChat(PlayerChatEvent e) {
		if (!quizRunning || (e.getRecipients().size() == 0)) return;
		
		// check if we have an answer
		String msg = e.getMessage();
		if ((quizCaseSensitive && quizAnswers.contains(msg)) || (!quizCaseSensitive && quizAnswers.contains(msg.toLowerCase()))) {
			// we have a winner, stop listening now
			HandlerList.unregisterAll(Quizzes.p);
			Quizzes.quizRunning = false;

			// announce winner
			String pName = e.getPlayer().getName();
			CommandsEX.plugin.getServer().broadcastMessage(ChatColor.GREEN + _("quizWinner1", ""));
			CommandsEX.plugin.getServer().broadcastMessage(ChatColor.YELLOW + _("quizWinner2", "") + ChatColor.WHITE + msg);
			CommandsEX.plugin.getServer().broadcastMessage(ChatColor.YELLOW + _("quizWinner3", "") + ChatColor.WHITE + pName);
			
			// select a random reward
			FileConfiguration f = CommandsEX.getConf();
			
			try {
				ConfigurationSection configGroups = f.getConfigurationSection("quizDiff." + quizDifficulty);
				Set<String> s = configGroups.getKeys(false);
				s.remove("caseSentisive");
				Integer len = s.size();
				String rewardName = null;
			
				if (len > 0) {
					do {
						Integer pos = (0 + (int)(Math.random() * ((len - 1) + 1)));
						Object[] quizNames = s.toArray();
						rewardName = (String) quizNames[pos];
					} while (((len == 1) ? false : ((lastRewards.get(quizDifficulty) != null) && lastRewards.get(quizDifficulty).equals(rewardName))));
					
					// store the reward name and tell player how to pick it up
					quizWinners.put(pName, quizDifficulty + "." + rewardName);
					lastRewards.put(quizDifficulty, rewardName);
					LogHelper.showInfo("quizClaimReward", e.getPlayer());
				} else {
					// tell the player we found and error and that we'll reward him later
					LogHelper.showInfo("quizCannotReward", e.getPlayer());
					
					// log the error to console
					LogHelper.logSevere("[CommandsEX] " + _("quizNoRewardsError", "") + pName);
				}
			} catch (Throwable ex) {
				// tell the player we found and error and that we'll reward him later
				LogHelper.showInfo("quizCannotReward", e.getPlayer());
				
				// log the error to console
				LogHelper.logSevere("[CommandsEX] " + _("quizNoRewardsError", "") + pName);
			}
		}
	}
}