package com.smpplugin;

import com.smpplugin.commands.SetSpawnCommand;
import com.smpplugin.commands.SpawnCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class SMPUtils extends JavaPlugin {

    private Location cachedSpawn;
    private File spawnFile;
    private static final boolean FOLIA = detectFolia();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Load spawn into memory from file on startup
        spawnFile = new File(getDataFolder(), "spawn.yml");
        cachedSpawn = loadSpawnFromFile();

        // Register chat formatting (requires PlaceholderAPI)
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("PlaceholderAPI not found! Chat formatting will not work.");
            getLogger().warning("Install: https://www.spigotmc.org/resources/placeholderapi.6245/");
        } else {
            Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
        }

        // Register elytra rocket cooldown
        Bukkit.getPluginManager().registerEvents(new RocketCooldownListener(this), this);

        // Register commands via CommandMap (no plugin.yml commands block needed)
        Bukkit.getCommandMap().register("smputils", new SpawnCommand(this));
        Bukkit.getCommandMap().register("smputils", new SetSpawnCommand(this));

        getLogger().info("SMPUtils enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SMPUtils disabled!");
    }

    // ── Spawn API ────────────────────────────────────────────────────────────

    /** Returns the cached spawn (loaded at startup, stays in memory). */
    public Location getCachedSpawn() {
        return cachedSpawn;
    }

    /** Updates the in-memory spawn AND persists it to spawn.yml. */
    public void setSpawn(Location location) {
        this.cachedSpawn = location.clone();
        saveSpawnToFile(location);
    }

    // ── File I/O ─────────────────────────────────────────────────────────────

    private Location loadSpawnFromFile() {
        if (!spawnFile.exists()) return null;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(spawnFile);
        String worldName = yaml.getString("world");
        if (worldName == null) return null;
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            getLogger().warning("spawn.yml references unknown world: " + worldName);
            return null;
        }
        return new Location(
                world,
                yaml.getDouble("x"),
                yaml.getDouble("y"),
                yaml.getDouble("z"),
                (float) yaml.getDouble("yaw"),
                (float) yaml.getDouble("pitch")
        );
    }

    private void saveSpawnToFile(Location loc) {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("world", loc.getWorld().getName());
        yaml.set("x",     loc.getX());
        yaml.set("y",     loc.getY());
        yaml.set("z",     loc.getZ());
        yaml.set("yaw",   (double) loc.getYaw());
        yaml.set("pitch", (double) loc.getPitch());
        try {
            yaml.save(spawnFile);
        } catch (IOException e) {
            getLogger().severe("Failed to save spawn.yml: " + e.getMessage());
        }
    }

    // ── Folia detection ──────────────────────────────────────────────────────

    public static boolean isFolia() {
        return FOLIA;
    }

    private static boolean detectFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
