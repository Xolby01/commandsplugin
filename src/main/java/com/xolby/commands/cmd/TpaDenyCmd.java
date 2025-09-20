package com.xolby.commands.cmd;

import com.xolby.commands.XolbysCommands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaDenyCmd implements CommandExecutor {
    private final XolbysCommands plugin;
    public TpaDenyCmd(XolbysCommands plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { sender.sendMessage("Players only."); return true; }
        Player p = (Player)sender;
        if (!p.hasPermission("xcmds.tpa.deny")) { p.sendMessage(XolbysCommands.cc(plugin.messages().raw("prefix")+plugin.messages().raw("noPermission"))); return true; }
        if (plugin.tpa().getIncoming(p) == null) { plugin.messages().send(p, "tpaNoRequest"); return true; }
        plugin.tpa().clear(p);
        plugin.messages().send(p,"tpaDenied");
        return true;
    }
}
