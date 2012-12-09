package com.github.ikeirnez.commandsex.commands;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.github.ikeirnez.commandsex.Builder;
import com.github.ikeirnez.commandsex.CommandsEX;
import com.github.ikeirnez.commandsex.HackedCommand;
import com.github.ikeirnez.commandsex.api.interfaces.ICommand;

@Builder(description = "Kills yourself!")
public class Command_cex_suicide implements ICommand {

    public HackedCommand init(CommandsEX cex) {
        return new HackedCommand("cex_suicide", "Kills the player sending the command", "", new String[] {"suicide"});
    }
    
    public boolean run(CommandSender sender, String[] args, String alias, CommandsEX cex) {
        if (!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "This command can only be used in-game");
            return true;
        }
        
        Player player = (Player) sender;
        
        EntityDamageEvent event = new EntityDamageEvent(player, DamageCause.SUICIDE, 1000);
        Bukkit.getPluginManager().callEvent(event);
        
        if (event.isCancelled()) return true;
        
        event.getEntity().setLastDamageCause(event);
        player.damage(event.getDamage());
        Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + ChatColor.RED + " committed suicide");
        
        return true;
    }
}
