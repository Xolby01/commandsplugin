package com.xolby.commands.cmd;

import com.xolby.commands.XolbysCommands;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FurnaceCmd implements CommandExecutor {
    private final XolbysCommands plugin;
    public FurnaceCmd(XolbysCommands plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { sender.sendMessage("Players only."); return true; }
        Player p = (Player)sender;
        if (!p.hasPermission("xcmds.furnace")) { p.sendMessage(XolbysCommands.cc(plugin.messages().raw("prefix")+plugin.messages().raw("noPermission"))); return true; }

        if (args.length > 0 && args[0].equalsIgnoreCase("all")) {
            if (!plugin.settings().allowInstantSmeltAll) {
                plugin.messages().send(p, "smeltAllDisabled");
                return true;
            }
            int convertedStacks = smeltAll(p);
            Map<String,String> repl = new HashMap<>();
            repl.put("count", String.valueOf(convertedStacks));
            plugin.messages().send(p, "smeltAllDone", repl);
            return true;
        }

        Inventory inv = Bukkit.createInventory(null, 27, "Portable Furnace");
        p.openInventory(inv);
        plugin.messages().send(p, "furnaceOpened");
        return true;
    }

    private int smeltAll(Player p) {
        int stacks = 0;
        Map<Material, ItemStack> cache = new HashMap<>();
        for (int i=0; i<p.getInventory().getSize(); i++) {
            ItemStack it = p.getInventory().getItem(i);
            if (it == null || it.getType() == Material.AIR) continue;
            ItemStack res = getFurnaceResult(it.getType(), cache);
            if (res != null) {
                ItemStack out = res.clone();
                out.setAmount(it.getAmount());
                p.getInventory().setItem(i, out);
                stacks++;
            }
        }
        return stacks;
    }

    private ItemStack getFurnaceResult(Material mat, Map<Material, ItemStack> cache) {
        if (cache.containsKey(mat)) return cache.get(mat);
        ItemStack result = null;
        Iterator<Recipe> it = Bukkit.recipeIterator();
        while (it.hasNext()) {
            Recipe r = it.next();
            if (r instanceof FurnaceRecipe) {
                FurnaceRecipe fr = (FurnaceRecipe) r;
                if (fr.getInput().getType() == mat) {
                    result = fr.getResult();
                    break;
                }
            }
        }
        cache.put(mat, result);
        return result;
    }
}
