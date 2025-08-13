package com.xolby.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;
import java.util.Collections;

import java.util.*;

public class XolbysCommands extends JavaPlugin implements TabExecutor {

    @Override
    public void onEnable() {
        getCommand("craft").setExecutor(this);
        getCommand("furnace").setExecutor(this);
        getCommand("furnace").setTabCompleter(this);
        getCommand("ec").setExecutor(this);

        getLogger().info("Xolby's Commands est activé !");
    }

    @Override
    public void onDisable() {
        getLogger().info("Xolby's Commands est désactivé !");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Seuls les joueurs peuvent exécuter cette commande.");
            return true;
        }

        Player player = (Player) sender;

        switch (cmd.getName().toLowerCase()) {
            case "craft":
                if (!player.hasPermission("xolby.commands.craft")) {
                    player.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande.");
                    return true;
                }
                player.openWorkbench(player.getLocation(), true);
                return true;

            case "furnace":
                if (!player.hasPermission("xolby.commands.furnace")) {
                    player.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande.");
                    return true;
                }

                if (args.length > 0 && args[0].equalsIgnoreCase("all")) {
                    // Mode cuisson de tout l'inventaire
                    int cookedCountAll = cookInventory(player);
                    player.sendMessage("§aVous avez cuit instantanément §e" + cookedCountAll + " §aitems dans votre inventaire !");
                } else {
                    // Mode cuisson de l'item en main
                    int cookedCountHand = cookItemInHand(player);
                    player.sendMessage("§aVous avez cuit instantanément §e" + cookedCountHand + " §aitems en main !");
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

    private int cookInventory(Player player) {
        int cookedCount = 0;
        ItemStack[] contents = player.getInventory().getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null || item.getType().isAir()) continue;

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
