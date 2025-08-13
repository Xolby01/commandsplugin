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
        getLogger().info("Xolby's Commands is enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Xolby's Commands is disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this command.");
            return true;
        }

        Player player = (Player) sender;

        switch (cmd.getName().toLowerCase()) {
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
                    cookAllInventory(player);
                } else {
                    cookItemInHand(player);
                }
                return true;

            case "ec":
                if (!player.hasPermission("xolby.commands.ec")) {
                    player.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande.");
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
            player.sendMessage("§cYou must be holding an item to cook it!");
            return;
        }

        Optional<CookingRecipe<?>> recipeOpt = findCookingRecipeFor(item);
        if (recipeOpt.isPresent()) {
            CookingRecipe<?> cookingRecipe = recipeOpt.get();
            ItemStack result = cookingRecipe.getResult().clone();
            result.setAmount(item.getAmount());
            player.getInventory().setItemInMainHand(result);
            player.sendMessage("§aCooked §e" + result.getAmount() + " §aitem(s) in your hand!");
        } else {
            player.sendMessage("§cThis item cannot be cooked!");
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
        player.sendMessage("§aCooked §e" + cookedCount + " §aitem(s) in your inventory!");
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
