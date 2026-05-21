package com.smpplugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SMPPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Check if PlaceholderAPI is installed
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("PlaceholderAPI not found! Chat formatting will not work.");
            getLogger().warning("Install PlaceholderAPI: https://www.spigotmc.org/resources/placeholderapi.6245/");
            return;
        }

        // Register chat listener
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);

        getLogger().info("SMPPlugin has been enabled!");
        getLogger().info("Chat format: [Prefix] [Team] PlayerName: message");
    }

    @Override
    public void onDisable() {
        getLogger().info("SMPPlugin has been disabled!");
    }
}
