package com.smpplugin;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    private static final LegacyComponentSerializer LEGACY =
            LegacyComponentSerializer.legacyAmpersand();

    private final SMPPlugin plugin;

    public ChatListener(SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        // Check if player has a team via PAPI
        String team = PlaceholderAPI.setPlaceholders(player, "%sorekillteams_team%");
        boolean hasTeam = team != null
                && !team.isEmpty()
                && !team.equals("%sorekillteams_team%")
                && !team.equalsIgnoreCase("none");

        // Select the appropriate format from config.yml
        String formatPath = hasTeam ? "chat-format-with-team" : "chat-format-without-team";
        String formatStr = plugin.getConfig().getString(formatPath);
        
        // Fallback in case the config is missing or corrupted
        if (formatStr == null) {
            formatStr = "%luckperms_prefix% &f%player_name%&7: &f{message}";
        }

        // Parse ALL placeholders in the format string
        String parsedFormat = PlaceholderAPI.setPlaceholders(player, formatStr);

        // Split the string by the {message} placeholder
        String[] parts = parsedFormat.split("\\{message\\}", -1);
        Component beforeMessage = LEGACY.deserialize(parts[0]);
        Component afterMessage = parts.length > 1 ? LEGACY.deserialize(parts[1]) : Component.empty();

        // Set the renderer to combine the parts with the actual message component
        event.renderer((source, sourceDisplayName, message, viewer) -> {
            return beforeMessage.append(message).append(afterMessage);
        });
    }
}
