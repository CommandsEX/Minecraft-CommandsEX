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
    		System.out.println("Vault Null");
    		return false;
        }

        RegisteredServiceProvider<Economy> rsp = CommandsEX.plugin.getServer().getServicesManager().getRegistration(Economy.class);
        System.out.println(rsp);
        if (rsp == null) {
            return false;
        }
        
        econ = rsp.getProvider();
        System.out.println(econ);
        return econ != null;
    }

    private Boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = CommandsEX.plugin.getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp == null){
        	return false;
        }
        
        chat = rsp.getProvider();
        return chat != null;
    }

    private Boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = CommandsEX.plugin.getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null){
        	return false;
        }
        
        perms = rsp.getProvider();
        return perms != null;
    }
    
    public static Boolean checkPerm(Player p, String perm) {
    	if (perms != null) {
    		return perms.has(p, perm);
    	} else {
    		return p.hasPermission(perm);
    	}
    }
    
    public static Boolean permsEnabled() {
    	return (perms != null);
    }
    
    public static Boolean ecoEnabled() {
    	return (econ != null);
    }
    
    public static Boolean chatEnabled() {
    	return (chat != null);
    }
}
