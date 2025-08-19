package com.xolby.commands;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.ChatColor;

public class Lang {
    private final FileConfiguration config;
    private final String lang;

    public Lang(FileConfiguration config) {
        this.config = config;
        this.lang = config.getString("lang", "fr");
    }

    public String get(String key) {
        String path = "messages." + lang + "." + key;
        String raw = config.getString(path, "&cMissing message: " + key);
        return ChatColor.translateAlternateColorCodes('&', raw);
    }
}
