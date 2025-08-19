package com.xolby.commands.tpa;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaCommand implements CommandExecutor {
    private final TpaManager manager;
    private final String msgRequestSent;
    private final String msgRequestReceived;

    public TpaCommand(TpaManager manager, String msgRequestSent, String msgRequestReceived) {
        this.manager = manager;
        this.msgRequestSent = ChatColor.translateAlternateColorCodes('&', msgRequestSent);
        this.msgRequestReceived = ChatColor.translateAlternateColorCodes('&', msgRequestReceived);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Commande réservée aux joueurs.");
            return true;
        }
        Player p = (Player) sender;

        if (manager.isOnCooldown(p)) {
            long sec = Math.max(1, manager.remaining(p) / 1000);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', getCooldownMessage()).replace("{seconds}", String.valueOf(sec)));
            return true;
        }

        if (args.length < 1) {
            p.sendMessage(ChatColor.RED + "Usage: /" + label + " <joueur>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            p.sendMessage(ChatColor.RED + "Joueur introuvable ou hors ligne.");
            return true;
        }
        if (target.getUniqueId().equals(p.getUniqueId())) {
            p.sendMessage(ChatColor.RED + "Vous ne pouvez pas vous cibler vous-même.");
            return true;
        }

        if (!p.hasPermission(getPermission())) {
            p.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
            return true;
        }

        // Specific behavior
        handle(p, target);

        manager.markUsed(p);
        p.sendMessage(msgRequestSent.replace("{player}", target.getName()));
        target.sendMessage(msgRequestReceived.replace("{player}", p.getName()));
        return true;
    }

    protected String getCooldownMessage() { return "&cVeuillez patienter {seconds}s avant de refaire une demande."; }
    protected String getPermission() { return "xolby.commands.tpa"; }
    protected void handle(Player p, Player target) { /* override */ }
}

    @Override
    protected String getPermission() { return "xolby.commands.tpa"; }

    @Override
    protected void handle(Player p, Player target) {
        manager.setRequest(p, target, TpaManager.Type.TPA);
    }
}
