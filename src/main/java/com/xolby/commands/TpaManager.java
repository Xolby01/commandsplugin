package com.xolby.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TpaManager {
    public enum Type { TO_TARGET, HERE }
    public static class Req {
        public final UUID sender;
        public final UUID target;
        public final long expireAt;
        public final Type type;
        public Req(UUID s, UUID t, long expireAt, Type type) {
            this.sender = s; this.target = t; this.expireAt = expireAt; this.type = type;
        }
        boolean expired() { return System.currentTimeMillis() > expireAt; }
    }
    private final XolbysCommands plugin;
    private final Map<UUID, Req> incoming = new HashMap<>(); // key: target

    public TpaManager(XolbysCommands plugin) { this.plugin = plugin; }

    public void sendRequest(Player sender, Player target, Type type) {
        long exp = System.currentTimeMillis() + plugin.settings().tpaExpirySeconds * 1000L;
        incoming.put(target.getUniqueId(), new Req(sender.getUniqueId(), target.getUniqueId(), exp, type));
    }

    public Req getIncoming(Player target) {
        Req r = incoming.get(target.getUniqueId());
        if (r != null && r.expired()) {
            incoming.remove(target.getUniqueId());
            return null;
        }
        return r;
    }

    public void clear(Player target) { incoming.remove(target.getUniqueId()); }

    public void accept(Player target) {
        Req r = getIncoming(target);
        if (r == null) return;
        Player sender = Bukkit.getPlayer(r.sender);
        if (sender == null) { clear(target); return; }
        final Player from, to;
        if (r.type == Type.TO_TARGET) { from = sender; to = target; }
        else { from = target; to = sender; }
        int delay = plugin.settings().tpaTeleportDelaySeconds;
        if (delay <= 0) {
            from.teleport(to.getLocation());
        } else {
            plugin.messages().send(from, "teleportInSeconds", Map.of("seconds", String.valueOf(delay)));
            Bukkit.getScheduler().runTaskLater(plugin, () -> from.teleport(to.getLocation()), 20L * delay);
        }
        clear(target);
    }
}
