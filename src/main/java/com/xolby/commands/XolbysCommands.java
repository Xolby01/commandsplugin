package com.xolby.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.xolby.commands.cmd.CraftCmd;
import com.xolby.commands.cmd.FurnaceCmd;
import com.xolby.commands.cmd.EcCmd;
import com.xolby.commands.cmd.SetHomeCmd;
import com.xolby.commands.cmd.HomeCmd;
import com.xolby.commands.cmd.DelHomeCmd;
import com.xolby.commands.cmd.TpaCmd;
import com.xolby.commands.cmd.TpaAcceptCmd;
import com.xolby.commands.cmd.TpaDenyCmd;
import com.xolby.commands.cmd.TpaHereCmd;
import com.xolby.commands.cmd.HelpCmd;

public class XolbysCommands extends JavaPlugin {

    private static XolbysCommands instance;
    private Messages messages;
    private Settings settings;
    private final HomeStore homeStore = new HomeStore(this);
    private final TpaManager tpaManager = new TpaManager(this);

    public static XolbysCommands get() { return instance; }
    public Messages messages() { return messages; }
    public Settings settings() { return settings; }
    public HomeStore homes() { return homeStore; }
    public TpaManager tpa() { return tpaManager; }

    private void safeReg(String name, org.bukkit.command.CommandExecutor exec) {
        if (getCommand(name) == null) {
            getLogger().severe("[XCmds] Command not found in plugin.yml: " + name);
            return;
        }
        getCommand(name).setExecutor(exec);
    }

    @Override
    public void onEnable() {
        instance = this;
        saveResource("config.json", false);
        loadConfigJson();
        homeStore.load();
        safeReg("craft", new CraftCmd(this));
        safeReg("furnace", new FurnaceCmd(this));
        safeReg("ec", new EcCmd(this));
        safeReg("sethome", new SetHomeCmd(this));
        safeReg("home", new HomeCmd(this));
        safeReg("delhome", new DelHomeCmd(this));
        safeReg("tpa", new TpaCmd(this));
        safeReg("tpaaccept", new TpaAcceptCmd(this));
        safeReg("tpadeny", new TpaDenyCmd(this));
        safeReg("tpahere", new TpaHereCmd(this));
        safeReg("xcmds", new HelpCmd(this));

        getLogger().info("Xolby's Commands enabled.");
    }

    @Override
    public void onDisable() {
        homeStore.save();
    }

    public void reloadAll() {
        loadConfigJson();
        homeStore.load();
    }

    private void loadConfigJson() {
        try {
            File file = new File(getDataFolder(), "config.json");
            Gson gson = new Gson();
            try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
                JsonObject root = gson.fromJson(reader, JsonObject.class);
                String lang = root.has("language") ? root.get("language").getAsString() : "fr";
                JsonObject msgs = root.has("messages_en") && lang.equalsIgnoreCase("en")
                        ? root.getAsJsonObject("messages_en")
                        : root.getAsJsonObject("messages");
                this.messages = new Messages(msgs);
                this.settings = new Settings(root.getAsJsonObject("settings"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.messages = new Messages(new JsonObject());
            this.settings = new Settings(new JsonObject());
        }
    }

    public static String cc(String s) { return ChatColor.translateAlternateColorCodes('&', s); }

    public static class Messages {
        private final Map<String, String> map = new HashMap<>();
        public Messages(JsonObject o) {
            put(o,"prefix","&6[XCmds]&r ");
            put(o,"noPermission","&cVous n'avez pas la permission pour cette commande.");
            put(o,"playersOnly","&cUniquement en jeu.");
            put(o,"craftOpened","&aÉtabli ouvert.");
            put(o,"furnaceOpened","&aFour ouvert.");
            put(o,"smeltAllDone","&aTous les objets smeltables ont été fondus (%count% stacks).");
            put(o,"smeltAllDisabled","&cLe smelt-all est désactivé sur ce serveur.");
            put(o,"enderOpened","&aEnderChest ouvert.");
            put(o,"homeSet","&aHome défini.");
            put(o,"homeMissing","&cVous n'avez pas de home.");
            put(o,"homeTeleported","&aTéléportation à votre home...");
            put(o,"homeDeleted","&aHome supprimé.");
            put(o,"tpaSent","&aDemande envoyée à &e%target%&a. Utilisez &e/tpaaccept &aou &e/tpadeny&a.");
            put(o,"tpaHereSent","&aDemande envoyée à &e%target%&a pour venir vers vous.");
            put(o,"tpaIncoming","&e%sender% &aa demandé à se téléporter vers vous. &e/tpaaccept &aou &e/tpadeny");
            put(o,"tpaHereIncoming","&e%sender% &a vous demande de vous téléporter vers lui. &e/tpaaccept &aou &e/tpadeny");
            put(o,"tpaNoRequest","&cAucune demande en attente.");
            put(o,"tpaAccepted","&aDemande acceptée.");
            put(o,"tpaDenied","&cDemande refusée.");
            put(o,"playerNotFound","&cJoueur introuvable.");
            put(o,"teleportInSeconds","&7Téléportation dans %seconds%s... Ne bougez pas!");
        }
        private void put(JsonObject o, String k, String def) { map.put(k, o!=null && o.has(k) ? o.get(k).getAsString() : def); }
        public String get(String key) { return map.getOrDefault(key, key); }
        public void send(Player p, String key) { p.sendMessage(cc(get("prefix") + get(key))); }
        public void send(Player p, String key, Map<String,String> repl) {
            String s = get(key);
            for (Map.Entry<String,String> e : repl.entrySet()) s = s.replace("%"+e.getKey()+"%", e.getValue());
            p.sendMessage(cc(get("prefix") + s));
        }
        public String raw(String key) { return cc(get(key)); }
    }

    public static class Settings {
        public final boolean allowInstantSmeltAll;
        public final boolean instantSmeltConsumesFuel;
        public final int tpaExpirySeconds;
        public final int tpaTeleportDelaySeconds;
        public final boolean allowMultipleHomes;
        public final int maxHomes;
        public Settings(JsonObject o) {
            JsonObject homes = (o!=null && o.has("homes")) ? o.getAsJsonObject("homes") : new JsonObject();
            allowInstantSmeltAll = o!=null && o.has("allowInstantSmeltAll") && o.get("allowInstantSmeltAll").getAsBoolean();
            instantSmeltConsumesFuel = o!=null && o.has("instantSmeltConsumesFuel") && o.get("instantSmeltConsumesFuel").getAsBoolean();
            tpaExpirySeconds = (o!=null && o.has("tpaExpirySeconds")) ? o.get("tpaExpirySeconds").getAsInt() : 60;
            tpaTeleportDelaySeconds = (o!=null && o.has("tpaTeleportDelaySeconds")) ? o.get("tpaTeleportDelaySeconds").getAsInt() : 0;
            allowMultipleHomes = homes!=null && homes.has("allowMultipleHomes") && homes.get("allowMultipleHomes").getAsBoolean();
            maxHomes = homes!=null && homes.has("maxHomes") ? homes.get("maxHomes").getAsInt() : 1;
        }
    }
}
