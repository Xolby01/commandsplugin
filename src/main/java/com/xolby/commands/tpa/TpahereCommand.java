package com.xolby.commands.tpa;

import org.bukkit.entity.Player;

public class TpahereCommand extends AbstractTpaAsk {
    public TpahereCommand(TpaManager manager, String msgRequestSent, String msgRequestReceived, String msgCooldown) {
        super(manager, msgRequestSent, msgRequestReceived, msgCooldown);
    }

    @Override
    protected String getPermission() {
        return "xolby.commands.tpahere";
    }

    @Override
    protected void handle(Player p, Player target) {
        manager.setRequest(p, target, TpaManager.Type.TPAHERE);
    }
}
