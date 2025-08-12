package com.xolby.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;
import java.util.Optional;

public class XolbysCommands extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Xolby's Commands enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Xolby's Commands disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        Player player = (Player) sender;
        String commandName = cmd.getName().toLowerCase();

        switch (commandName) {
            case "craft":
                if (!player.hasPermission("xolby.commands.craft")) {
                    player.sendMessage("§cYou don't have permission to use this command.");
                    return true;
                }
                player.openWorkbench(player.getLocation(), true);
                return true;

            case "furnace":
                if (!player.hasPermission("xolby.commands.furnace")) {
                    player.sendMessage("§cYou don't have permission to use this command.");
                    return true;
                }

                if (args.length > 0 && args[0].equalsIgnoreCase("all")) {
                    int cookedAll = cookAllItems(player);
                    player.sendMessage("§aYou instantly cooked §e" + cookedAll + " §aitems in your inventory!");
                } else {
                    int cookedOne = cookItemInHand(player);
                    if (cookedOne > 0) {
                        player.sendMessage("§aYou instantly cooked §e" + cookedOne + " §aitem(s) in your hand!");
                    } else {
                        player.sendMessage("§cThe item in your hand cannot be cooked.");
                    }
                }
                return true;

            case "ec":
                if (!player.hasPermission("xolby.commands.ec")) {
                    player.sendMessage("§cYou don't have permission to use this command.");
                    return true;
                }
                player.openInventory(player.getEnderChest());
                return true;

            default:
                return false;
        }
    }

    private int cookItemInHand(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) return 0;

        Optional<CookingRecipe<?>> recipeOpt = findCookingRecipeFor(item);
        if (recipeOpt.isPresent()) {
            CookingRecipe<?> cookingRecipe = recipeOpt.get();
            ItemStack result = cookingRecipe.getResult().clone();
            result.setAmount(item.getAmount());
            player.getInventory().setItemInMainHand(result);
            return result.getAmount();
        }
        return 0;
    }

    private int cookAllItems(Player player) {
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
        return cookedCount;
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
