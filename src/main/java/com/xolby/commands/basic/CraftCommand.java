package com.xolby.commands.basic;

import com.xolby.commands.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CraftCommand implements CommandExecutor {
    private final Lang lang;
    public CraftCommand(Lang lang) { this.lang = lang; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(lang.get("only_players"));
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("xolby.commands.craft")) {
            p.sendMessage(lang.get("craft_no_permission"));
            return true;
        }
        p.openWorkbench(null, true);
        p.sendMessage(lang.get("craft_opened"));
        return true;
    }
}
