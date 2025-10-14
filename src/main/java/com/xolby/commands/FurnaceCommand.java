package com.xolby.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class FurnaceCommand implements CommandExecutor {
    
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
        
        Inventory furnace = Bukkit.createInventory(null, InventoryType.FURNACE, "Furnace");
        player.openInventory(furnace);
        player.sendMessage(plugin.getMessage("messages.furnace-opened"));
        
        return true;
    }
}
