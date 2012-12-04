package com.github.zathrus_writer.commandsex.handlers;

import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.Vault;
import com.github.zathrus_writer.commandsex.api.economy.Economy;
import com.github.zathrus_writer.commandsex.helpers.ClosestMatches;
import com.github.zathrus_writer.commandsex.helpers.ItemStackParser;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;

public class Handler_commandsigns implements Listener {

    public Handler_commandsigns(){
        Bukkit.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent e){
        Player player = e.getPlayer();

        Sign sign = (Sign) e.getBlock().getState();
        String[] lines = e.getLines();
        String function = lines[0].toLowerCase();

        if (function.equals("[balance]")){
            if (Permissions.checkPerms(player, "cex.sign.create.balance")){
                if (!lines[1].equals("") || !lines[2].equals("") || !lines[3].equals("")){
                    LogHelper.showWarning("commandSignInvalid", player);
                    sign.getBlock().breakNaturally();
                } else {
                    LogHelper.showInfo("commandSignBalance", player, ChatColor.GREEN);
                }
            } else {
                sign.getBlock().breakNaturally();
            }
        } else if (function.equals("[pay]")){
            if (Permissions.checkPerms(player, "cex.sign.create.pay")){
                if (lines[1].equals("")){
                    LogHelper.showWarning("commandSignInvalid", player);
                    sign.getBlock().breakNaturally();
                } else {
                    if (lines[2].equals("")){
                        e.setLine(2, player.getName());
                    }

                    try {
                        double amount = Double.parseDouble(lines[1]);
                        e.setLine(1, Economy.fixDecimals(amount));
                        LogHelper.showInfo("commandSignPay", player, ChatColor.GREEN);
                    } catch (NumberFormatException ex){
                        LogHelper.showWarning("commandSignInvalid", player);
                        sign.getBlock().breakNaturally();
                    }
                }
            }
        } else if (function.equals("[item]")){
            if (Permissions.checkPerms(player, "cex.sign.create.item")){
                if (lines[1].equals("")){
                    LogHelper.showWarning("commandSignInvalid", player);
                    sign.getBlock().breakNaturally();
                } else {
                    String[] data = lines[1].split(":");
                    List<Material> matches = ClosestMatches.material(data[0]);
                    Material material = (matches.size() > 0 ? matches.get(0) : null);
                    if (material == null){
                        LogHelper.showWarning("itemNotFound", player);
                        sign.getBlock().breakNaturally();
                    } else {
                        if (data.length > 0){
                            if (data.length != 2){
                                LogHelper.showWarning("commandSignInvalid", player);
                                sign.getBlock().breakNaturally();
                            } else {
                                if (material == Material.WOOL || material == Material.INK_SACK){
                                    List<DyeColor> dyeMatches = ClosestMatches.dyeColor(data[1]);
                                    DyeColor dye = (dyeMatches.size() > 0 ? dyeMatches.get(0) : null);
                                    if (dye == null){
                                        LogHelper.showWarning("commandSignInvalid", player);
                                        sign.getBlock().breakNaturally();
                                        return;
                                    } else {
                                        e.setLine(1, WordUtils.capitalize(material.name().toLowerCase().replaceAll("_", ""))  + ":" + WordUtils.capitalize(dye.toString().toLowerCase().replaceAll("_", "")));
                                    }
                                } else {
                                    if (!data[1].matches(CommandsEX.intRegex)){
                                        LogHelper.showWarning("commandSignInvalid", player);
                                        sign.getBlock().breakNaturally();
                                        return;
                                    } else {
                                        e.setLine(1, WordUtils.capitalize(material.name().toLowerCase().replaceAll("_", "")));
                                    }
                                }
                            }
                        }
                        
                        if (lines[2].equals("")){
                            e.setLine(2, String.valueOf(1));
                        } else {
                            try {
                                Integer.parseInt(lines[2]);

                                if (!lines[3].equals("")){
                                    try {
                                        double amount = Double.parseDouble(lines[3]);

                                        e.setLine(3, Economy.fixDecimals(amount));
                                    } catch (NumberFormatException ex1){
                                        LogHelper.showWarning("commandSignInvalid", player);
                                        sign.getBlock().breakNaturally();
                                        return;
                                    }
                                }
                            } catch (NumberFormatException ex){
                                LogHelper.showWarning("commandSignInvalid", player);
                                sign.getBlock().breakNaturally();
                                return;
                            }
                        }

                        LogHelper.showInfo("commandSignItem", player, ChatColor.GREEN);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent e){
        BlockState bs = e.getClickedBlock().getState();
        Player player = e.getPlayer();

        if (bs instanceof Sign){
            Sign sign = (Sign) bs;
            String[] lines = sign.getLines();
            String function = lines[0].toLowerCase();

            if (function.equals("[balance]")){
                if (Permissions.checkPerms(player, "cex.sign.use.balance")){
                    if (Vault.ecoEnabled()){
                        double balance = Vault.econ.getBalance(player.getName());
                        LogHelper.showInfo("economyBalance#####[" + Economy.fixDecimals(balance) + " " + (balance == 1 ? Vault.econ.currencyNameSingular() : Vault.econ.currencyNamePlural()), player);
                    } else {
                        // no eco plugin error
                        return;
                    }
                }
            } else if (function.equals("[pay]")){
                if (Permissions.checkPerms(player, "cex.sign.use.pay")){
                    double amount = Double.parseDouble(lines[1]);
                    String toPay = lines[2];
                    if (Vault.econ.has(player.getName(), amount)){
                        Economy.transfer(player.getName(), toPay, amount);
                    } else {
                        LogHelper.showWarning("economyNotEnough", player);
                    }
                }
            } else if (function.equals("[item]")){
                if (Permissions.checkPerms(player, "cex.sign.use.item")){
                    ItemStack is = ItemStackParser.parse(new String[] {lines[1], lines[2]}, player);
                    
                    if (is == null){
                        LogHelper.showWarning("commandSignInvalid", player);
                        return;
                    }
                    
                    player.getInventory().addItem(is);
                }
            }
        }
    }

}
