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
            this.from = from;
            this.to = to;
            this.type = type;
            this.createdAt = System.currentTimeMillis();
        }
    }

    private final Map<UUID, Request> pendingByTarget = new HashMap<>();
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
        if (until == null) return 0L;
        long rem = until - System.currentTimeMillis();
        return Math.max(0L, rem);
    }

    public void markUsed(Player p) {
        cooldowns.put(p.getUniqueId(), System.currentTimeMillis() + cooldownMs);
    }

    public void setRequest(Player from, Player to, Type type) {
        pendingByTarget.put(to.getUniqueId(), new Request(from.getUniqueId(), to.getUniqueId(), type));
    }

    public Request peek(Player target) {
        Request r = pendingByTarget.get(target.getUniqueId());
        if (r == null) return null;
        if (isExpired(r)) { pendingByTarget.remove(target.getUniqueId()); return null; }
        return r;
    }

    public Request consume(Player target) {
        Request r = pendingByTarget.remove(target.getUniqueId());
        if (r == null) return null;
        if (isExpired(r)) return null;
        return r;
    }

    public boolean deny(Player target) {
        return pendingByTarget.remove(target.getUniqueId()) != null;
    }

    public boolean isExpired(Request r) {
        return (System.currentTimeMillis() - r.createdAt) >= expireMs;
    }
}
