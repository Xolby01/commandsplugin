package com.xolby.commands;

import com.xolby.commands.tpa.TpaManager;
import com.xolby.commands.tpa.TpaCommand;
import com.xolby.commands.tpa.TpahereCommand;
import com.xolby.commands.tpa.TpaAcceptCommand;
import com.xolby.commands.tpa.TpadenyCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class XolbysCommands extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();

        long cooldownSec = getConfig().getLong("teleport.cooldown_seconds", 5L);
        long expireSec   = getConfig().getLong("teleport.request_expire_seconds", 60L);

        TpaManager manager = new TpaManager(cooldownSec * 1000L, expireSec * 1000L);

        String msgReqSent   = getConfig().getString("messages.tpa_request_sent", "&aDemande de téléportation envoyée à {player}.");
        String msgReqRecv   = getConfig().getString("messages.tpa_request_received", "&e{player} veut se téléporter à vous. Tapez /tpaaccept pour accepter.");
        String msgHereSent  = getConfig().getString("messages.tpahere_request_sent", "&aDemande envoyée à {player} de se téléporter vers vous.");
        String msgHereRecv  = getConfig().getString("messages.tpahere_request_received", "&e{player} veut que vous vous téléportiez à lui. Tapez /tpaaccept pour accepter.");
        String msgAccepted  = getConfig().getString("messages.tpa_accepted", "&aVotre demande de téléportation a été acceptée.");
        String msgNoReq     = getConfig().getString("messages.tpa_no_request", "&cVous n'avez aucune demande de téléportation en attente.");
        String msgCooldown  = getConfig().getString("messages.tpa_cooldown", "&cVeuillez patienter {seconds}s avant de refaire une demande.");
        String msgDeniedS   = getConfig().getString("messages.tpa_denied_sender", "&cVotre demande de téléportation a été refusée.");
        String msgDeniedT   = getConfig().getString("messages.tpa_denied_target", "&eVous avez refusé la demande.");
        String msgExpired   = getConfig().getString("messages.tpa_expired", "&cLa demande de téléportation a expiré.");

        getCommand("tpa").setExecutor(new TpaCommand(manager, msgReqSent, msgReqRecv, msgCooldown));
        getCommand("tpahere").setExecutor(new TpahereCommand(manager, msgHereSent, msgHereRecv, msgCooldown));
        getCommand("tpaaccept").setExecutor(new TpaAcceptCommand(manager, msgAccepted, msgNoReq, msgExpired));
        getCommand("tpadeny").setExecutor(new TpadenyCommand(manager, msgDeniedS, msgDeniedT, msgNoReq, msgExpired));
    }
}
