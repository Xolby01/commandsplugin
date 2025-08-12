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

                ItemStack itemInHand = player.getInventory().getItemInMainHand();
                if (itemInHand == null || itemInHand.getType().isAir()) {
                    player.sendMessage("§cVous devez tenir un objet à cuire dans votre main.");
                    return true;
                }

                Optional<CookingRecipe<?>> recipeOpt = findCookingRecipeFor(itemInHand);
                if (recipeOpt.isPresent()) {
                    CookingRecipe<?> cookingRecipe = recipeOpt.get();
                    ItemStack result = cookingRecipe.getResult().clone();
                    result.setAmount(itemInHand.getAmount());
                    player.getInventory().setItemInMainHand(result);

                    player.sendMessage("§aVous avez cuit instantanément §e" + result.getAmount() + " §aitems !");
                } else {
                    player.sendMessage("§cAucune recette de cuisson trouvée pour cet objet.");
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

    private Optional<CookingRecipe<?>> findCookingRecipeFor(ItemStack input) {
        Iterator<Recipe> iterator = getServer().recipeIterator();

        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();

            if (recipe instanceof CookingRecipe<?> cookingRecipe) {
                RecipeChoice inputChoice = cookingRecipe.getInputChoice();
                if (inputChoice.test(input)) {
                    return Optional.of(cookingRecipe);
                }
            }
        }
        return Optional.empty();
    }
}
