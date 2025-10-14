package com.xolby.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CraftCommand implements CommandExecutor {
    
    private final XolbysCommands plugin;
    
    public CraftCommand(XolbysCommands plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("messages.only-players"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("xolby.craft")) {
            player.sendMessage(plugin.getMessage("messages.no-permission"));
            return true;
        }
        
        player.openWorkbench(player.getLocation(), true);
        player.sendMessage(plugin.getMessage("messages.craft-opened"));
        
        return true;
    }
}