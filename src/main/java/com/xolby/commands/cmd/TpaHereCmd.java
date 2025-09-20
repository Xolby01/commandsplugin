package com.xolby.commands.cmd;

import com.xolby.commands.TpaManager;
import com.xolby.commands.XolbysCommands;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class TpaHereCmd implements CommandExecutor {
    private final XolbysCommands plugin;
    public TpaHereCmd(XolbysCommands plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { sender.sendMessage("Players only."); return true; }
        Player p = (Player)sender;
        if (!p.hasPermission("xcmds.tpahere")) { p.sendMessage(XolbysCommands.cc(plugin.messages().raw("prefix")+plugin.messages().raw("noPermission"))); return true; }
        if (args.length < 1) { p.sendMessage("/tpahere <player>"); return true; }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) { plugin.messages().send(p,"playerNotFound"); return true; }
        plugin.tpa().sendRequest(p, target, TpaManager.Type.HERE);
        plugin.messages().send(p,"tpaHereSent", Map.of("target", target.getName()));
        plugin.messages().send(target,"tpaHereIncoming", Map.of("sender", p.getName()));
        return true;
    }
}
