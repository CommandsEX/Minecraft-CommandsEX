package com.github.ikeirnez.commandsex.commands;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.scheduler.BukkitTask;

import com.github.ikeirnez.commandsex.CommandsEX;
import com.github.ikeirnez.commandsex.api.ACommand;
import com.github.ikeirnez.commandsex.api.ICommand;
import com.github.ikeirnez.commandsex.helpers.Utils;

@ACommand(description = "Changes a dogs collar color at a set interval", command = "discodog")
public class Command_discodog implements ICommand {

    private HashMap<Integer, DyeColor> discoDogging = new HashMap<Integer, DyeColor>();
    
    public void init(CommandsEX cex, FileConfiguration config) {
        CommandsEX.pluginManager.addPermission(new Permission("cex.discodog", "Allows access to /discodog", PermissionDefault.OP));
    }

    public boolean run(CommandSender sender, String[] args, String alias, CommandsEX cex, FileConfiguration config) {
        if (!(sender instanceof Player)){
            return true;
        }
        
        int time = 30;
        if (args.length == 1){
            if (!Utils.isInt(args[0])){
                // integer message
                return true;
            }
            
            time = Integer.parseInt(args[0]);
        } else if (args.length > 1){
            return false;
        }
        
        Player p = (Player) sender;
        Entity e = null;
        
        for (Entity en : p.getNearbyEntities(5, 5, 5)){
            if (en.getType() == EntityType.WOLF){
                e = en;
                break;
            }
        }
        
        if (discoDogging.containsKey(e.getEntityId())){
            // already disco dogging
            return true;
        }
        
        final Wolf f = (Wolf) e;
        
        if (!f.isTamed()){
            return true;
        }
        
        DyeColor original = f.getCollarColor();
        discoDogging.put(f.getEntityId(), original);
        
        final BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(CommandsEX.plugin, new Runnable() {
            public void run() {
                if (!f.isDead()){
                    DyeColor[] dc = DyeColor.values();
                    Random r = new Random();
                    f.setCollarColor(dc[r.nextInt(dc.length)]);
                }
            }
        }, 1L, 1L);
        
        Bukkit.getScheduler().runTaskLaterAsynchronously(CommandsEX.plugin, new Runnable() {
            public void run() {
                task.cancel();
                f.setCollarColor(discoDogging.get(f.getEntityId()));
                discoDogging.remove(f.getEntityId());
            }
        }, 20L * time);
        
        return true;
    }

}
