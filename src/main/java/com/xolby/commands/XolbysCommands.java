package com.xolby.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class XolbysCommands extends JavaPlugin {
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        getLogger().info("Xolby's Commands is enabled!");
        this.configManager = new ConfigManager(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Xolby's Commands is disabled!");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(getConfigManager().getMessage("command_only_players"));
            return true;
        }
        Player player = (Player) sender;
        switch (cmd.getName().toLowerCase()) {
            case "craft":
                if (!player.hasPermission("xolby.commands.craft")) {
                    player.sendMessage(getConfigManager().getMessage("command_no_permission"));
                    return true;
                }
                player.openWorkbench(player.getLocation(), true);
                return true;
            case "furnace":
                if (!player.hasPermission("xolby.commands.furnace")) {
                    player.sendMessage(getConfigManager().getMessage("command_no_permission"));
                    return true;
                }
                if (args.length > 0 && args[0].equalsIgnoreCase("all")) {
                    cookAllInventory(player);
                } else {
                    cookItemInHand(player);
                }
                return true;
            case "ec":
                if (!player.hasPermission("xolby.commands.ec")) {
                    player.sendMessage(getConfigManager().getMessage("command_no_permission"));
                    return true;
                }
                player.openInventory(player.getEnderChest());
                return true;

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("furnace")) {
            if (args.length == 1) {
                return Collections.singletonList("all");
            }
        }
        return Collections.emptyList();
    }

    private void cookItemInHand(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) {
            player.sendMessage(getConfigManager().getMessage("furnace_no_item"));
            return;
        }
        Optional<CookingRecipe<?>> recipeOpt = findCookingRecipeFor(item);
        if (recipeOpt.isPresent()) {
            CookingRecipe<?> cookingRecipe = recipeOpt.get();
            ItemStack result = cookingRecipe.getResult().clone();
            result.setAmount(item.getAmount());
            player.getInventory().setItemInMainHand(result);
            player.sendMessage(
                getConfigManager().getMessage("furnace_cooked_hand", result.getAmount())
            );
        } else {
            player.sendMessage(getConfigManager().getMessage("furnace_cannot_cook"));
        }
    }

    private void cookAllInventory(Player player) {
        int cookedCount = 0;
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null) continue;
            Optional<CookingRecipe<?>> recipeOpt = findCookingRecipeFor(item);
            if (recipeOpt.isPresent()) {
                CookingRecipe<?> cookingRecipe = recipeOpt.get();
                ItemStack result = cookingRecipe.getResult().clone();
                result.setAmount(item.getAmount());
                contents[i] = result;
                cookedCount += result.getAmount();
            }
        }
        player.getInventory().setContents(contents);
        player.sendMessage(
            getConfigManager().getMessage("furnace_cooked_all", cookedCount)
        );
    }

    private Optional<CookingRecipe<?>> findCookingRecipeFor(ItemStack input) {
        Iterator<Recipe> iterator = getServer().recipeIterator();
        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();
            if (recipe instanceof CookingRecipe<?> cookingRecipe) {
                RecipeChoice inputChoice = cookingRecipe.getInputChoice();
                if (inputChoice != null && inputChoice.test(input)) {
                    return Optional.of(cookingRecipe);
                }
            }
        }
        return Optional.empty();
    }
}
