package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Home;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;

public class Command_cex_home extends Home {
	/***
	 * HOME - manages homes (allows to create, visit and administer them, based on permissions and command parameters)
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player = (Player)sender;
			// check permissions and call to action
			if (args.length > 0) {
				Boolean hasPerms = true;
				if ((args.length == 1) && args[0].toLowerCase().equals("set") && (hasPerms = Permissions.checkPerms(player, "cex.sethome"))) {
					/***
					 * HOME SET
					 */
					setHome(sender, args, "sethome", alias);
				} else if ((args.length == 1) && args[0].toLowerCase().equals("list") && (hasPerms = Permissions.checkPerms(player, "cex.home.list"))) {
					/***
					 * HOME LIST
					 */
					list(sender, args, "home", alias);
				} else if ((args.length == 1) && args[0].toLowerCase().equals("ilist") && (hasPerms = Permissions.checkPerms(player, "cex.home.list"))) {
					/***
					 * HOME ILIST
					 */
					ilist(sender, args, "home", alias);
				} else if ((args.length == 1) && args[0].toLowerCase().equals("listall") && (hasPerms = Permissions.checkPerms(player, "cex.home.listall"))) {
					/***
					 * HOME LISTALL
					 */
					listall(sender, args, "home", alias);
				} else if ((args.length == 1) && args[0].toLowerCase().equals("public") && (hasPerms = Permissions.checkPerms(player, "cex.home.publicize"))) {
					/***
					 * HOME PUBLIC
					 */
					make_public(sender, args, "home", alias);
				} else if ((args.length == 1) && args[0].toLowerCase().equals("private") && (hasPerms = Permissions.checkPerms(player, "cex.home.publicize"))) {
					/***
					 * HOME PRIVATE
					 */
					make_private(sender, args, "home", alias);
				}  else if ((args.length == 1) && args[0].toLowerCase().equals("help")) {
					/***
					 * HOME HELP
					 */
					help(sender, args, "home", alias);
				} else if ((args.length >= 1) && args[0].toLowerCase().equals("delete")) {
					/***
					 * HOME DELETE [Player]
					 */
					if (
						((args.length == 1) && (hasPerms = Permissions.checkPerms(player, "cex.home.delete")))
						||
						((args.length > 1) && (hasPerms = Permissions.checkPerms(player, "cex.home.delete.all")))
					   ) {
						delete(sender, args, "home", alias);
					}
				} else if ((args.length >= 1) && args[0].toLowerCase().equals("iclear") && (hasPerms = Permissions.checkPerms(player, "cex.home.iclear"))) {
					/***
					 * HOME ICLEAR <Days>
					 */
					iclear(sender, args, "home", alias);
				} else if ((args.length >= 1) && args[0].toLowerCase().equals("tclear") && (hasPerms = Permissions.checkPerms(player, "cex.home.tclear"))) {
					/***
					 * HOME TCLEAR <Days>
					 */
					tclear(sender, args, "home", alias);
				} else if ((args.length >= 1) && args[0].toLowerCase().equals("invite") && (hasPerms = Permissions.checkPerms(player, "cex.home.invite"))) {
					/***
					 * HOME INVITE <Player>
					 */
					invite(sender, args, "home", alias);
				} else if ((args.length >= 1) && args[0].toLowerCase().equals("uninvite") && (hasPerms = Permissions.checkPerms(player, "cex.home.invite"))) {
					/***
					 * HOME UNINVITE <Player>
					 */
					uninvite(sender, args, "home", alias);
				} else if ((args.length >= 1) && Permissions.checkPerms(player, "cex.home") && hasPerms) {
					/***
					 * HOME <Player>
					 */
					home(sender, args, "home", alias);
				} else if (hasPerms) {
					// no match? show help :-)
					help(sender, args, "home", alias);
				}
			} else {
				/***
				 * HOME (tp to player's own home)
				 */
				if (Permissions.checkPerms(player, "cex.home")) {
					home(sender, args, "home", alias);
				}
			}
		}
        return true;
	}
}
