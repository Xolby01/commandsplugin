package com.xolby.commands.cmd;

import com.xolby.commands.XolbysCommands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCmd implements CommandExecutor {
    private final XolbysCommands plugin;
    public HelpCmd(XolbysCommands plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String[] lines = new String[]{
                "&6==== Xolby's Commands ====",
                "&e/craft &7- établi portable",
                "&e/furnace [all] &7- four portable ou smelt tout",
                "&e/ec &7- ender chest",
                "&e/sethome &7- définir le home",
                "&e/home &7- téléportation au home",
                "&e/delhome &7- supprimer le home",
                "&e/tpa <joueur> &7- demander à se téléporter",
                "&e/tpahere <joueur> &7- demander au joueur de venir",
                "&e/tpaaccept &7- accepter",
                "&e/tpadeny &7- refuser"
        };
        for (String l : lines) sender.sendMessage(XolbysCommands.cc(l));
        return true;
    }
}
