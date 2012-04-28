package com.github.zathrus_writer.commandsex;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

/***
 * Adds support of Vault permissions, chat and economy hooks to CommandsEX.
 * @author zathrus-writer
 *
 */
public class Vault {
	
	public static Economy econ = null;
    public static Permission perms = null;
    public static Chat chat = null;

    public Vault() {
    	setupChat();
    	setupEconomy();
    	setupPermissions();
    }
    
    private Boolean setupEconomy() {
        if (CommandsEX.plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = CommandsEX.plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        
        econ = rsp.getProvider();
        return econ != null;
    }

    private Boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = CommandsEX.plugin.getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private Boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = CommandsEX.plugin.getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    
    public static Boolean checkPerm(Player p, String perm) {
    	return perms.has(p, perm);
    }
}
