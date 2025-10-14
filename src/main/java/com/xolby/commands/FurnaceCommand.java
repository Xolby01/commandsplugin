package com.xolby.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.CampfireRecipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class FurnaceCommand implements CommandExecutor, TabCompleter {
    
    private final XolbysCommands plugin;
    
    public FurnaceCommand(XolbysCommands plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("messages.only-players"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("xolby.furnace")) {
            player.sendMessage(plugin.getMessage("messages.no-permission"));
            return true;
        }
        
        // Si argument "all" est fourni
        if (args.length > 0 && args[0].equalsIgnoreCase("all")) {
            smeltAllItems(player);
            return true;
        }
        
        // Sinon, cuire l'item en main
        smeltItemInHand(player);
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.add("all");
            return completions;
        }
        return Collections.emptyList();
    }
    
    private void smeltItemInHand(Player player) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        
        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            player.sendMessage(plugin.getMessage("messages.no-item-in-hand"));
            return;
        }
        
        ItemStack result = getSmeltingResult(itemInHand);
        
        if (result == null) {
            player.sendMessage(plugin.getMessage("messages.cannot-smelt"));
            return;
        }
        
        int amount = itemInHand.getAmount();
        result.setAmount(amount);
        
        player.getInventory().setItemInMainHand(result);
        player.sendMessage(plugin.getMessage("messages.item-smelted")
                .replace("%amount%", String.valueOf(amount))
                .replace("%item%", result.getType().name()));
    }
    
    private void smeltAllItems(Player player) {
        PlayerInventory inventory = player.getInventory();
        int totalSmelted = 0;
        
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            
            ItemStack result = getSmeltingResult(item);
            
            if (result != null) {
                int amount = item.getAmount();
                result.setAmount(amount);
                inventory.setItem(i, result);
                totalSmelted += amount;
            }
        }
        
        if (totalSmelted == 0) {
            player.sendMessage(plugin.getMessage("messages.no-items-to-smelt"));
        } else {
            player.sendMessage(plugin.getMessage("messages.all-items-smelted")
                    .replace("%amount%", String.valueOf(totalSmelted)));
        }
    }
    
    private ItemStack getSmeltingResult(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return null;
        }
        
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        
        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            
            // Vérifier si c'est une recette de cuisson (Furnace, Blast Furnace, Smoker, Campfire)
            if (recipe instanceof FurnaceRecipe) {
                FurnaceRecipe furnaceRecipe = (FurnaceRecipe) recipe;
                if (furnaceRecipe.getInput().getType() == item.getType()) {
                    return furnaceRecipe.getResult().clone();
                }
            } else if (recipe instanceof BlastingRecipe) {
                BlastingRecipe blastingRecipe = (BlastingRecipe) recipe;
                if (blastingRecipe.getInput().getType() == item.getType()) {
                    return blastingRecipe.getResult().clone();
                }
            } else if (recipe instanceof SmokingRecipe) {
                SmokingRecipe smokingRecipe = (SmokingRecipe) recipe;
                if (smokingRecipe.getInput().getType() == item.getType()) {
                    return smokingRecipe.getResult().clone();
                }
            } else if (recipe instanceof CampfireRecipe) {
                CampfireRecipe campfireRecipe = (CampfireRecipe) recipe;
                if (campfireRecipe.getInput().getType() == item.getType()) {
                    return campfireRecipe.getResult().clone();
                }
            }
        }
        
        return null;
    }
}