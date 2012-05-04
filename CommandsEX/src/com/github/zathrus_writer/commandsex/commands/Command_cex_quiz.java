package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Quizzes;

public class Command_cex_quiz extends Quizzes {
	/***
	 * QUIZ - commands related to predefined quizzes, their running, checking time to next quiz, claiming rewards etc.
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (args.length > 0) {
			Boolean hasPerms = true;
			Boolean isPlayer = (sender instanceof Player);
			if (args[0].toLowerCase().equals("status") && (!isPlayer || (isPlayer && (hasPerms = Permissions.checkPerms((Player) sender, "cex.quiz.status"))))) {
				/***
				 * QUIZ STATUS - shows when the next quiz starts
				 */
				qremain(sender, args, "quiz", alias);
			} else if (args[0].toLowerCase().equals("claim") && PlayerHelper.checkIsPlayer(sender) && isPlayer && (hasPerms = Permissions.checkPerms((Player) sender, "cex.quiz.claim"))) {
				/***
				 * QUIZ CLAIM - claims reward after winning a quiz
				 */
				claim(sender, args, "quiz", alias);
			} else if (args[0].toLowerCase().equals("start") && (!isPlayer || (isPlayer && (hasPerms = Permissions.checkPerms((Player) sender, "cex.quiz.start"))))) {
				/***
				 * QUIZ START - forces a quiz to begin
				 */
				qstart(sender, args, "quiz", alias);
			} else if (hasPerms) {
				// no match? show version
				if (sender instanceof Player) {
					hasPerms = Permissions.checkPerms((Player) sender, "cex.quiz.version"); 
				}
				
				if (hasPerms) {
					quiz(sender, args, "quiz", alias);
				}
			}
		} else {
			/***
			 * QUIZ (show version)
			 */
			Boolean hasPerms = true;
			if (sender instanceof Player) {
				hasPerms = Permissions.checkPerms((Player) sender, "cex.quiz.version"); 
			}
			
			if (hasPerms) {
				quiz(sender, args, "quiz", alias);
			}
		}
		
        return true;
	}
}
