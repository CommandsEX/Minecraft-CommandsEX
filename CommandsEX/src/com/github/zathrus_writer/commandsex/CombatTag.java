package com.github.zathrus_writer.commandsex;

import com.trc202.CombatTagApi.CombatTagApi;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CombatTag {

    private static CombatTagApi combatTagApi = null;

    public CombatTag(){
        Plugin plugin = Bukkit.getPluginManager().getPlugin("CombatTag");

        if (plugin != null){
            combatTagApi = new CombatTagApi((com.trc202.CombatTag.CombatTag) plugin);
        }
    }

    public static boolean isEnabled(){
        return combatTagApi != null;
    }

    public static CombatTagApi getCombatTagApi(){
        return combatTagApi;
    }

    public static boolean isInCombat(Player player){
        return (isEnabled() ? getCombatTagApi().isInCombat(player) : false);
    }

}
