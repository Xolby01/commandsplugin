package com.xolby.commands.cmd;

import com.xolby.commands.TpaManager;
import com.xolby.commands.XolbysCommands;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class TpaCmd implements CommandExecutor {
    private final XolbysCommands plugin;
    public TpaCmd(XolbysCommands plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { sender.sendMessage("Players only."); return true; }
        Player p = (Player)sender;
        if (!p.hasPermission("xcmds.tpa")) { p.sendMessage(XolbysCommands.cc(plugin.messages().raw("prefix")+plugin.messages().raw("noPermission"))); return true; }
        if (args.length < 1) { p.sendMessage("/tpa <player>"); return true; }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) { plugin.messages().send(p,"playerNotFound"); return true; }
        plugin.tpa().sendRequest(p, target, TpaManager.Type.TO_TARGET);
        plugin.messages().send(p,"tpaSent", Map.of("target", target.getName()));
        plugin.messages().send(target,"tpaIncoming", Map.of("sender", p.getName()));
        return true;
    }
}
