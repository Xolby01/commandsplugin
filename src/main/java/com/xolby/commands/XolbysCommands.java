package com.xolby.commands;
import com.xolby.commands.tpa.*;
import org.bukkit.plugin.java.JavaPlugin;
public class XolbysCommands extends JavaPlugin {
    private Lang lang;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.lang = new Lang(getConfig());
        long cooldownSec = getConfig().getLong("teleport.cooldown_seconds", 5L);
        long expireSec   = getConfig().getLong("teleport.request_expire_seconds", 60L);
        TpaManager manager = new TpaManager(cooldownSec * 1000L, expireSec * 1000L);
        getCommand("tpa").setExecutor(new TpaCommand(manager, lang.get("tpa_request_sent"), lang.get("tpa_request_received"), lang.get("tpa_cooldown")));
        getCommand("tpahere").setExecutor(new TpahereCommand(manager, lang.get("tpahere_request_sent"), lang.get("tpahere_request_received"), lang.get("tpa_cooldown")));
        getCommand("tpaaccept").setExecutor(new TpaAcceptCommand(manager, lang.get("tpa_accepted"), lang.get("tpa_no_request"), lang.get("tpa_expired")));
        getCommand("tpadeny").setExecutor(new TpadenyCommand(manager, lang.get("tpa_denied_sender"), lang.get("tpa_denied_target"), lang.get("tpa_no_request"), lang.get("tpa_expired")));
        getCommand("craft").setExecutor(new com.xolby.commands.basic.CraftCommand(lang));
        getCommand("ec").setExecutor(new com.xolby.commands.basic.EcCommand(lang));
        getCommand("furnace").setExecutor(new com.xolby.commands.basic.FurnaceCommand(lang));
    }

}
