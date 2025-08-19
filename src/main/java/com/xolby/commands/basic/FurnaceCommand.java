package com.xolby.commands.basic;

import com.xolby.commands.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class FurnaceCommand implements CommandExecutor {
    private final Lang lang;
    public FurnaceCommand(Lang lang) { this.lang = lang; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(lang.get("only_players"));
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("xolby.commands.furnace")) {
            p.sendMessage(lang.get("furnace_no_permission"));
            return true;
        }
        boolean all = args.length > 0 && args[0].equalsIgnoreCase("all");
        if (all) {
            for (ItemStack item : p.getInventory().getContents()) {
                smelt(item);
            }
        } else {
            smelt(p.getInventory().getItemInMainHand());
        }
        p.sendMessage(lang.get("furnace_done"));
        return true;
    }

    private void smelt(ItemStack item) {
        if (item == null) return;
        Recipe recipe = Bukkit.getRecipesFor(item).stream().findFirst().orElse(null);
        if (recipe != null && recipe.getResult().getType() != Material.AIR) {
            item.setType(recipe.getResult().getType());
        }
    }
}
