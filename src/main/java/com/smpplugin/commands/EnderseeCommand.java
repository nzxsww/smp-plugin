package com.smpplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class EnderseeCommand extends Command {

    public EnderseeCommand() {
        super("endersee");
        setDescription("Abre el enderchest de otro jugador en vivo.");
        setUsage("/endersee <jugador>");
        setPermission("smputils.endersee");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player viewer)) {
            sender.sendMessage("Solo jugadores pueden usar este comando.");
            return true;
        }

        if (!viewer.hasPermission("smputils.endersee")) {
            viewer.sendMessage("§cNo tienes permisos.");
            return true;
        }

        if (args.length != 1) {
            viewer.sendMessage("§cUso: /endersee <jugador>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        
        if (target == null) {
            viewer.sendMessage("§cJugador no encontrado o está desconectado.");
            return true;
        }

        if (target.equals(viewer)) {
            viewer.openInventory(viewer.getEnderChest());
            return true;
        }

        // Native method to access someone else's enderchest live.
        viewer.openInventory(target.getEnderChest());
        viewer.sendMessage("§dAbriendo el EnderChest de §5" + target.getName());

        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return Collections.emptyList();
    }
}
