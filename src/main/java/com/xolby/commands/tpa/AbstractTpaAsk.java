package com.xolby.commands.tpa;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class AbstractTpaAsk implements CommandExecutor {
    protected final TpaManager manager;
    private final String msgRequestSent;
    private final String msgRequestReceived;
    private final String msgCooldown;

    protected AbstractTpaAsk(TpaManager manager, String msgRequestSent, String msgRequestReceived, String msgCooldown) {
        this.manager = manager;
        this.msgRequestSent = ChatColor.translateAlternateColorCodes('&', msgRequestSent);
        this.msgRequestReceived = ChatColor.translateAlternateColorCodes('&', msgRequestReceived);
        this.msgCooldown = msgCooldown;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Commande réservée aux joueurs.");
            return true;
        }
        Player p = (Player) sender;

        if (manager.isOnCooldown(p)) {
            long sec = Math.max(1L, manager.remaining(p) / 1000L);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', msgCooldown).replace("{seconds}", String.valueOf(sec)));
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

        handle(p, target);
        manager.markUsed(p);
        p.sendMessage(msgRequestSent.replace("{player}", target.getName()));
        target.sendMessage(msgRequestReceived.replace("{player}", p.getName()));
        return true;
    }

    protected abstract String getPermission();
    protected abstract void handle(Player p, Player target);
}
