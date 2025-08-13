package com.xolby.commands;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private final JavaPlugin plugin;
    private YamlConfiguration config;
    private File configFile;
    private Map<String, String> currentMessages;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.currentMessages = new HashMap<>();
        setupConfig();
    }

    private void setupConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource("config.yml", false); 
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        loadMessages();
    }

    private void loadMessages() {
        currentMessages.clear();
        String lang = config.getString("language", "en_US");
        if (config.contains("messages." + lang)) {
            for (String key : config.getConfigurationSection("messages." + lang).getKeys(false)) {
                currentMessages.put(key, config.getString("messages." + lang + "." + key));
            }
        } else {
            plugin.getLogger().warning("Language '" + lang + "' not found! Using en_US.");
            lang = "en_US";
            for (String key : config.getConfigurationSection("messages.en_US").getKeys(false)) {
                currentMessages.put(key, config.getString("messages.en_US." + key));
            }
        }
    }

    public String getMessage(String key, Object... replacements) {
        String message = currentMessages.getOrDefault(key, "Missing message: " + key);
        for (int i = 0; i < replacements.length; i++) {
            message = message.replace("{" + i + "}", String.valueOf(replacements[i]));
        }
        return message;
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        loadMessages();
    }
}
