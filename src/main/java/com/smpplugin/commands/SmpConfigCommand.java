package com.smpplugin.commands;

import com.smpplugin.SMPUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class SmpConfigCommand extends Command {

    private final SMPUtils plugin;
    
    // Valid keys that can be edited in-game
    private final List<String> validKeys = Arrays.asList(
            "elytra-rocket-cooldown",
            "elytra-rocket-boost-percent",
            "elytra-durability-reduction",
            "elytra-max-unbreaking",
            "natural-regeneration"
    );

    public SmpConfigCommand(SMPUtils plugin) {
        super("smpconfig");
        setDescription("Configura los valores de SMPUtils in-game");
        setUsage("/smpconfig <opcion> <valor>");
        setPermission("smputils.admin");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("smputils.admin")) {
            sender.sendMessage("§cNo tienes permisos.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§a--- Configuración SMP ---");
            for (String key : validKeys) {
                sender.sendMessage("§e" + key + "§f: §b" + plugin.getConfig().get(key));
            }
            sender.sendMessage("§7Uso: /smpconfig <opcion> <valor>");
            return true;
        }
        
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            sender.sendMessage("§aConfiguración recargada correctamente.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("§cUso: /smpconfig <opcion> <valor>");
            return true;
        }

        String key = args[0];
        if (!validKeys.contains(key)) {
            sender.sendMessage("§cOpcion inválida. Opciones: " + String.join(", ", validKeys));
            return true;
        }

        String valStr = args[1];
        if (key.equals("natural-regeneration")) {
            if (valStr.equalsIgnoreCase("true") || valStr.equalsIgnoreCase("false")) {
                boolean value = Boolean.parseBoolean(valStr);
                plugin.getConfig().set(key, value);
                plugin.saveConfig();
                sender.sendMessage("§aSe ha actualizado §e" + key + " §aal valor §b" + value);
            } else {
                sender.sendMessage("§cEl valor debe ser true o false.");
            }
            return true;
        }

        try {
            int value = Integer.parseInt(valStr);
            plugin.getConfig().set(key, value);
            plugin.saveConfig();
            sender.sendMessage("§aSe ha actualizado §e" + key + " §aal valor §b" + value);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cEl valor debe ser un número entero.");
        }

        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return validKeys.stream().filter(k -> k.startsWith(args[0].toLowerCase())).toList();
        }
        return super.tabComplete(sender, alias, args);
    }
}
