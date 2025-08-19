package com.xolby.commands.tpa;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpadenyCommand implements CommandExecutor {
    private final TpaManager manager;
    private final String msgDeniedSender;
    private final String msgDeniedTarget;
    private final String msgNoRequest;
    private final String msgExpired;

    public TpadenyCommand(TpaManager manager, String msgDeniedSender, String msgDeniedTarget, String msgNoRequest, String msgExpired) {
        this.manager = manager;
        this.msgDeniedSender = ChatColor.translateAlternateColorCodes('&', msgDeniedSender);
        this.msgDeniedTarget = ChatColor.translateAlternateColorCodes('&', msgDeniedTarget);
        this.msgNoRequest = ChatColor.translateAlternateColorCodes('&', msgNoRequest);
        this.msgExpired = ChatColor.translateAlternateColorCodes('&', msgExpired);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Commande réservée aux joueurs.");
            return true;
        }
        Player target = (Player) sender;

        if (!target.hasPermission("xolby.commands.tpa.deny")) {
            target.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
            return true;
        }

        TpaManager.Request req = manager.peek(target);
        if (req == null) {
            target.sendMessage(msgNoRequest);
            return true;
        }

        if (manager.isExpired(req)) {
            target.sendMessage(msgExpired);
            return true;
        }

        manager.deny(target);
        target.sendMessage(msgDeniedTarget);
        return true;
    }
}
