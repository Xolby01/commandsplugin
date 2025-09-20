package com.xolby.commands;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeStore {
    private final XolbysCommands plugin;
    private final File file;
    private final Gson gson = new Gson();
    private Map<String, HomeEntry> data = new HashMap<>();

    public HomeStore(XolbysCommands plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "homes.json");
    }

    public void load() {
        try {
            if (!file.exists()) { save(); return; }
            try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
                Type t = new TypeToken<Map<String, HomeEntry>>(){}.getType();
                Map<String, HomeEntry> loaded = gson.fromJson(reader, t);
                if (loaded != null) data = loaded;
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void save() {
        try {
            file.getParentFile().mkdirs();
            try (FileWriter w = new FileWriter(file, StandardCharsets.UTF_8)) {
                gson.toJson(data, w);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void setHome(UUID uuid, Location loc) {
        data.put(uuid.toString(), HomeEntry.from(loc));
        save();
    }

    public Location getHome(UUID uuid) {
        HomeEntry e = data.get(uuid.toString());
        if (e == null) return null;
        World w = Bukkit.getWorld(e.world);
        if (w == null) return null;
        return new Location(w, e.x, e.y, e.z, e.yaw, e.pitch);
    }

    public void delHome(UUID uuid) {
        data.remove(uuid.toString());
        save();
    }

    private static class HomeEntry {
        String world;
        double x, y, z;
        float yaw, pitch;
        static HomeEntry from(Location l) {
            HomeEntry e = new HomeEntry();
            e.world = l.getWorld().getName();
            e.x = l.getX(); e.y = l.getY(); e.z = l.getZ();
            e.yaw = l.getYaw(); e.pitch = l.getPitch();
            return e;
        }
    }
}
