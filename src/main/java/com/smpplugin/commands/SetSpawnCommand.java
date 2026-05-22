package com.smpplugin.commands;

import com.smpplugin.SMPUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetSpawnCommand extends Command {

    private final SMPUtils plugin;

    public SetSpawnCommand(SMPUtils plugin) {
        super("setspawn");
        setDescription("Sets the server spawn to your current location.");
        setUsage("/setspawn");
        setPermission("smputils.setspawn");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use /setspawn.");
            return false;
        }

        // Save to memory + file atomically
        plugin.setSpawn(player.getLocation());
        player.sendMessage("§aSpawn set to your current location!");
        return true;
    }
}
