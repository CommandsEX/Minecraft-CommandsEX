package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.command.CommandSender;

import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.Warps;

public class Command_cex_warp extends Warps {
	/***
	 * WARPS - manages warps (allows to create, visit and administer them, based on permissions and command parameters)
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
			// check permissions and call to action
			if (args.length > 0) {
				Boolean hasPerms = true;
				if ((args.length >= 1) && args[0].toLowerCase().equals("list") && ((hasPerms = Permissions.checkPerms(sender, "cex.warp.listpublic")) || (hasPerms = Permissions.checkPerms(sender, "cex.warp.listprivate")))) {
					/***
					 * WARP LIST, LISTWARPS
					 */
					list(sender, args, "warp", alias);
				} else if ((args.length >= 1) && args[0].toLowerCase().equals("create") && (hasPerms = Permissions.checkPerms(sender, "cex.warp.create"))) {
					/***
					 * WARP CREATE
					 */
					create(sender, args, "warp", alias);
				} else if ((args.length >= 1) && args[0].toLowerCase().equals("rename") && (hasPerms = Permissions.checkPerms(sender, "cex.warp.rename"))) {
					/***
					 * WARP RENAME
					 */
					rename(sender, args, "warp", alias);
				} else if ((args.length >= 1) && args[0].toLowerCase().equals("public") && (hasPerms = Permissions.checkPerms(sender, "cex.warp.public"))) {
					/***
					 * WARP PUBLIC
					 */
					make_public(sender, args, "warp", alias);
				} else if ((args.length >= 1) && args[0].toLowerCase().equals("private") && (hasPerms = Permissions.checkPerms(sender, "cex.warp.private"))) {
					/***
					 * WARP PRIVATE
					 */
					make_private(sender, args, "warp", alias);
				} else if ((args.length == 1) && args[0].toLowerCase().equals("help")) {
					/***
					 * WARP HELP
					 */
					help(sender, args, "warp", alias);
				} else if ((args.length > 1) && (args[0].toLowerCase().equals("delete") || (args[0].toLowerCase().equals("remove")))) {
					/***
					 * WARP DELETE
					 */
					if ((hasPerms = Permissions.checkPerms(sender, "cex.warp.delete"))) {
						delete(sender, args, "warp", alias);
					}
				} else if ((args.length == 1 || args.length == 2) && (Permissions.checkPerms(sender, "cex.warp.own") || Permissions.checkPerms(sender, "cex.warp")) && hasPerms) {
					/***
					 * WARP <Name>
					 */
					warp(sender, args, "warp", alias);
				} else if (hasPerms) {
					// no match? show help :)
					help(sender, args, "warp", alias);
				}
			} else {
				/***
				 * WARP (show help)
				 */
				help(sender, args, "warp", alias);
			}
        return true;
	}
}
