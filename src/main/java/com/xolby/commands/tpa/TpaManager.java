package com.xolby.commands.tpa;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TpaManager {
    public enum Type { TPA, TPAHERE }
    public static class Request {
        public final UUID from;
        public final UUID to;
        public final Type type;
        public final long createdAt;
        public Request(UUID from, UUID to, Type type) {
            this.from = from; this.to = to; this.type = type; this.createdAt = System.currentTimeMillis();
        }
    }

    private final Map<UUID, Request> pending = new HashMap<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final long cooldownMs;
    private final long expireMs;

    public TpaManager(long cooldownMs, long expireMs) {
        this.cooldownMs = cooldownMs;
        this.expireMs = expireMs;
    }

    public boolean isOnCooldown(Player p) {
        Long until = cooldowns.get(p.getUniqueId());
        return until != null && until > System.currentTimeMillis();
    }

    public long remaining(Player p) {
        Long until = cooldowns.get(p.getUniqueId());
        if (until == null) return 0;
        long rem = until - System.currentTimeMillis();
        return Math.max(0, rem);
    }

    public void markUsed(Player p) {
        cooldowns.put(p.getUniqueId(), System.currentTimeMillis() + cooldownMs);
    }

    public void setRequest(Player from, Player to, Type type) {
        pending.put(to.getUniqueId(), new Request(from.getUniqueId(), to.getUniqueId(), type));
    }

    public Request peek(Player acceptor) {
        return pending.get(acceptor.getUniqueId());
    }

    public Request consume(Player acceptor) {
        return pending.remove(acceptor.getUniqueId());
    }

    public boolean isExpired(Request req) {
        if (req == null) return true;
        return System.currentTimeMillis() - req.createdAt > expireMs;
    }
}
