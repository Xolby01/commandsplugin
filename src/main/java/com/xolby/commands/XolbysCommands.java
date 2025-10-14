package com.xolby.commands;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

public class XolbysCommands extends JavaPlugin {
    
    private File messagesFile;
    private FileConfiguration messagesConfig;
    
    @Override
    public void onEnable() {
        // Créer le fichier de configuration des messages
        createMessagesConfig();
        
        // Enregistrer les commandes
        getCommand("craft").setExecutor(new CraftCommand(this));
        getCommand("furnace").setExecutor(new FurnaceCommand(this));
        getCommand("ec").setExecutor(new EnderChestCommand(this));
        
        getLogger().info("Xolby's Commands a été activé avec succès!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("Xolby's Commands a été désactivé!");
    }
    
    private void createMessagesConfig() {
        messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            messagesFile.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }
        
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }
    
    public String getMessage(String path) {
        return messagesConfig.getString(path, "Message non trouvé: " + path)
                .replace("&", "§");
    }
    
    public void saveMessagesConfig() {
        try {
            messagesConfig.save(messagesFile);
        } catch (IOException e) {
            getLogger().severe("Impossible de sauvegarder messages.yml!");
        }
    }
    
    public void reloadMessagesConfig() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }
}