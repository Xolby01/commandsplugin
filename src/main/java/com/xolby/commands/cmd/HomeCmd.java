package com.xolby.commands.cmd;

import com.xolby.commands.XolbysCommands;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCmd implements CommandExecutor {
    private final XolbysCommands plugin;
    public HomeCmd(XolbysCommands plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { sender.sendMessage("Players only."); return true; }
        Player p = (Player)sender;
        if (!p.hasPermission("xcmds.home")) { p.sendMessage(XolbysCommands.cc(plugin.messages().raw("prefix")+plugin.messages().raw("noPermission"))); return true; }
        Location l = plugin.homes().getHome(p.getUniqueId());
        if (l == null) { plugin.messages().send(p, "homeMissing"); return true; }
        plugin.messages().send(p, "homeTeleported");
        p.teleport(l);
        return true;
    }
}
