package com.xolby.commands.cmd;

import com.xolby.commands.XolbysCommands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCmd implements CommandExecutor {
    private final XolbysCommands plugin;
    public SetHomeCmd(XolbysCommands plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { sender.sendMessage("Players only."); return true; }
        Player p = (Player)sender;
        if (!p.hasPermission("xcmds.sethome")) { p.sendMessage(XolbysCommands.cc(plugin.messages().raw("prefix")+plugin.messages().raw("noPermission"))); return true; }
        plugin.homes().setHome(p.getUniqueId(), p.getLocation());
        plugin.messages().send(p, "homeSet");
        return true;
    }
}
