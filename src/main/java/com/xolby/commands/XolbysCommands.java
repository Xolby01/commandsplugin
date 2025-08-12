package com.xolby.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Optional;

public class XolbysCommands extends JavaPlugin {

    @Override
    public void onEnable() {
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
                player.sendMessage("§aVous avez cuit instantanément §e" + cookedCount + " §aitems (mods inclus si une recette existe) !");
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

    private Optional<CookingRecipe<?>> findCookingRecipeFor(ItemStack input) {
        List<Recipe> recipes = getServer().getRecipesFor(input);
        if (recipes == null) return Optional.empty();

        for (Recipe recipe : recipes) {
            if (recipe instanceof CookingRecipe) {
                return Optional.of((CookingRecipe<?>) recipe);
            }
        }
        return Optional.empty();
    }
}
