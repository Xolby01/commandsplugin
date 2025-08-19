package com.xolby.commands.tpa;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaAcceptCommand implements CommandExecutor {
    private final TpaManager manager;
    private final String msgAccepted;
    private final String msgNoRequest;
    private final String msgExpired;

    public TpaAcceptCommand(TpaManager manager, String msgAccepted, String msgNoRequest, String msgExpired) {
        this.manager = manager;
        this.msgAccepted = ChatColor.translateAlternateColorCodes('&', msgAccepted);
        this.msgNoRequest = ChatColor.translateAlternateColorCodes('&', msgNoRequest);
        this.msgExpired = ChatColor.translateAlternateColorCodes('&', msgExpired);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Commande réservée aux joueurs.");
            return true;
        }
        Player acceptor = (Player) sender;

        if (!acceptor.hasPermission("xolby.commands.tpa.accept")) {
            acceptor.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
            return true;
        }

        TpaManager.Request req = manager.consume(acceptor);
        if (req == null) {
            acceptor.sendMessage(msgNoRequest);
            return true;
        }

        if (manager.isExpired(req)) {
            acceptor.sendMessage(msgExpired);
            Player requester = Bukkit.getPlayer(req.from);
            if (requester != null && requester.isOnline()) {
                requester.sendMessage(msgExpired);
            }
            return true;
        }

        Player from = Bukkit.getPlayer(req.from);
        Player to = Bukkit.getPlayer(req.to);
        if (from == null || to == null || !from.isOnline() || !to.isOnline()) {
            acceptor.sendMessage(ChatColor.RED + "Joueur non disponible.");
            return true;
        }

        if (req.type == TpaManager.Type.TPA) {
            from.teleport(to.getLocation());
        } else {
            to.teleport(from.getLocation());
        }

        from.sendMessage(msgAccepted);
        to.sendMessage(msgAccepted);
        return true;
    }
}
