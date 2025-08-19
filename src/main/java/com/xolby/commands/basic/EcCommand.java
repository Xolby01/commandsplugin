package com.xolby.commands.basic;

import com.xolby.commands.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EcCommand implements CommandExecutor {
    private final Lang lang;
    public EcCommand(Lang lang) { this.lang = lang; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(lang.get("only_players"));
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("xolby.commands.ec")) {
            p.sendMessage(lang.get("ec_no_permission"));
            return true;
        }
        p.openInventory(p.getEnderChest());
        p.sendMessage(lang.get("ec_opened"));
        return true;
    }
}
