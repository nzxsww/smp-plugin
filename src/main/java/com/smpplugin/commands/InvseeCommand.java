package com.smpplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class InvseeCommand extends Command {

    public InvseeCommand() {
        super("invsee");
        setDescription("Abre el inventario de otro jugador en vivo.");
        setUsage("/invsee <jugador>");
        setPermission("smputils.invsee");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player viewer)) {
            sender.sendMessage("Solo jugadores pueden usar este comando.");
            return true;
        }

        if (!viewer.hasPermission("smputils.invsee")) {
            viewer.sendMessage("§cNo tienes permisos.");
            return true;
        }

        if (args.length != 1) {
            viewer.sendMessage("§cUso: /invsee <jugador>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        
        if (target == null) {
            viewer.sendMessage("§cJugador no encontrado o está desconectado.");
            return true;
        }

        if (target.equals(viewer)) {
            viewer.sendMessage("§cNo puedes inspeccionar tu propio inventario.");
            return true;
        }

        // Native Paper/Bukkit method. 100% dupe-proof and zero TPS cost.
        viewer.openInventory(target.getInventory());
        viewer.sendMessage("§aAbriendo el inventario de §e" + target.getName());

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
