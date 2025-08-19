package com.xolby.commands.tpa;

import org.bukkit.Bukkit;
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

    public TpadenyCommand(TpaManager manager, String msgDeniedSender, String msgDeniedTarget, String msgNoRequest) {
        this.manager = manager;
        this.msgDeniedSender = ChatColor.translateAlternateColorCodes('&', msgDeniedSender);
        this.msgDeniedTarget = ChatColor.translateAlternateColorCodes('&', msgDeniedTarget);
        this.msgNoRequest = ChatColor.translateAlternateColorCodes('&', msgNoRequest);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Commande réservée aux joueurs.");
            return true;
        }
        Player denyPlayer = (Player) sender;

        if (!denyPlayer.hasPermission("xolby.commands.tpa.deny")) {
            denyPlayer.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
            return true;
        }

        TpaManager.Request req = manager.consume(denyPlayer);
        if (req == null) {
            denyPlayer.sendMessage(msgNoRequest);
            return true;
        }

        Player requester = Bukkit.getPlayer(req.from);
        if (requester != null && requester.isOnline()) {
            requester.sendMessage(msgDeniedSender);
        }
        denyPlayer.sendMessage(msgDeniedTarget);
        return true;
    }
}
