package com.xolby.commands.cmd;

import com.xolby.commands.XolbysCommands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CraftCmd implements CommandExecutor {
    private final XolbysCommands plugin;
    public CraftCmd(XolbysCommands plugin) { this.plugin = plugin; }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { sender.sendMessage("Players only."); return true; }
        Player p = (Player)sender;
        if (!p.hasPermission("xcmds.craft")) { p.sendMessage(XolbysCommands.cc(plugin.messages().raw("prefix")+plugin.messages().raw("noPermission"))); return true; }
        p.openWorkbench(p.getLocation(), true);
        plugin.messages().send(p, "craftOpened");
        return true;
    }
}
