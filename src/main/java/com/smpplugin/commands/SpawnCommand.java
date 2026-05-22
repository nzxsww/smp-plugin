package com.smpplugin.commands;

import com.smpplugin.SMPUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpawnCommand extends Command {

    private final SMPUtils plugin;

    public SpawnCommand(SMPUtils plugin) {
        super("spawn");
        setDescription("Teleports you to the server spawn.");
        setUsage("/spawn");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use /spawn.");
            return true;
        }

        Location spawn = plugin.getCachedSpawn();
        if (spawn == null) {
            player.sendMessage("§cNo spawn has been set yet! Use /setspawn to define one.");
            return true;
        }

        if (SMPUtils.isFolia()) {
            player.teleportAsync(spawn);
        } else {
            player.teleport(spawn);
        }
        return true;
    }
}
