package com.xolby.commands.tpa;

import org.bukkit.entity.Player;

public class TpaCommand extends AbstractTpaAsk {
    public TpaCommand(TpaManager manager, String msgRequestSent, String msgRequestReceived, String msgCooldown) {
        super(manager, msgRequestSent, msgRequestReceived, msgCooldown);
    }

    @Override
    protected String getPermission() {
        return "xolby.commands.tpa";
    }

    @Override
    protected void handle(Player p, Player target) {
        manager.setRequest(p, target, TpaManager.Type.TPA);
    }
}
