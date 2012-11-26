package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Teleportation;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_tploc extends Teleportation {
    /***
     * TPLOC - teleports player to given coordinates
     * @param sender
     * @param args
     * @return
     */
    public static Boolean run(CommandSender sender, String alias, String[] args) {
        if (PlayerHelper.checkIsPlayer(sender)) {
            Player player = (Player)sender;

            if (!Utils.checkCommandSpam(player, "tp-tploc")) {
                // alternative usage, all 3 coords separated by comma in 1 argument
                if (args.length == 1) {
                    if (args[0].contains(",")) {
                        args = args[0].split(",");
                    } else {
                        Commands.showCommandHelpAndUsage(sender, "cex_tploc", alias);
                        return true;
                    }
                }

                if (args.length <= 0) {
                    // no coordinates
                    Commands.showCommandHelpAndUsage(sender, "cex_tploc", alias);
                } else if (!(args.length == 3 || args.length == 4)) {
                    // too few or too many arguments
                    LogHelper.showWarning("tpMissingCoords", sender);
                    return false;
                } else if (!args[0].matches(CommandsEX.intRegex) || !args[1].matches(CommandsEX.intRegex) || !args[2].matches(CommandsEX.intRegex)) {
                    // one of the coordinates is not a number
                    LogHelper.showWarning("tpCoordsMustBeNumeric", sender);
                } else {
                    try {
                        Player target = null;
                        if (args.length == 4){
                            if (Bukkit.getPlayer(args[3]) != null){
                                target = Bukkit.getPlayer(args[3]);
                            } else {
                                LogHelper.showInfo("invalidPlayer", player, ChatColor.RED);
                                return true;
                            }
                        } else {
                            target = player;
                        }

                        delayedTeleport(target, new Location(player.getWorld(), new Double(args[0]), new Double(args[1]), new Double(args[2])));

                        LogHelper.showInfo("tpLocMsgToTarget#####[" + args[0].toString() + " " + args[1].toString() + " " + args[2].toString(), sender, ChatColor.AQUA);
                        if (player != target){
                            LogHelper.showInfo("tpLocSuccess#####[" + target.getName() + " #####tpLocToCoords#####[" + args[0].toString() + " " + args[1].toString() + " " + args[2].toString(), sender, ChatColor.GREEN);
                        }
                    } catch (Throwable e) {
                        LogHelper.showWarning("internalError", sender);
                        LogHelper.logSevere("[CommandsEX]: TPLOC returned an unexpected error for player " + player.getName() + ".");
                        LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
                        e.printStackTrace();
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
