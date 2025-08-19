package com.xolby.commands;

import com.xolby.commands.tpa.TpaManager;
import com.xolby.commands.tpa.TpaCommand;
import com.xolby.commands.tpa.TpahereCommand;
import com.xolby.commands.tpa.TpaAcceptCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class XolbysCommands extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        long cooldownSec = getConfig().getLong("teleport.cooldown_seconds", 5L);
        TpaManager tpaManager = new TpaManager(cooldownSec * 1000L);
        String msgReqSent = getConfig().getString("messages.tpa_request_sent", "&aDemande de téléportation envoyée à {player}.");
        String msgReqRecv = getConfig().getString("messages.tpa_request_received", "&e{player} veut se téléporter à vous. Tapez /tpaaccept pour accepter.");
        String msgHereSent = getConfig().getString("messages.tpahere_request_sent", "&aDemande envoyée à {player} de se téléporter vers vous.");
        String msgHereRecv = getConfig().getString("messages.tpahere_request_received", "&e{player} veut que vous vous téléportiez à lui. Tapez /tpaaccept pour accepter.");
        String msgAccepted = getConfig().getString("messages.tpa_accepted", "&aVotre demande de téléportation a été acceptée.");
        String msgNoReq = getConfig().getString("messages.tpa_no_request", "&cVous n'avez aucune demande de téléportation en attente.");

        getCommand("tpa").setExecutor(new TpaCommand(tpaManager, msgReqSent, msgReqRecv));
        getCommand("tpahere").setExecutor(new TpahereCommand(tpaManager, msgHereSent, msgHereRecv));
                String msgDeniedSender = getConfig().getString("messages.tpa_denied_sender", "&cVotre demande de téléportation a été refusée.");
        String msgDeniedTarget = getConfig().getString("messages.tpa_denied_target", "&aVous avez refusé la demande.");
        String msgExpired = getConfig().getString("messages.tpa_expired", "&cLa demande de téléportation a expiré.");
        getCommand("tpadeny").setExecutor(new TpadenyCommand(tpaManager, msgDeniedSender, msgDeniedTarget, msgNoReq));
        // update tpaaccept with expired message
        getCommand("tpaaccept").setExecutor(new TpaAcceptCommand(tpaManager, msgAccepted, msgNoReq, msgExpired));
    
    }

    @Override
    public void onDisable() {
        // nothing
    }
}
