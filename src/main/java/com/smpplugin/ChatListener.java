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

    private static final MiniMessage MINI = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY =
            LegacyComponentSerializer.legacySection();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        // Get LuckPerms prefix via PAPI
        String prefix = PlaceholderAPI.setPlaceholders(player, "%luckperms_prefix%");
        if (prefix == null || prefix.equals("%luckperms_prefix%")) {
            prefix = "";
        }

        // Get SoreKillTeams team name via PAPI
        String team = PlaceholderAPI.setPlaceholders(player, "%sorekillteams_team%");
        boolean hasTeam = team != null
                && !team.isEmpty()
                && !team.equals("%sorekillteams_team%")
                && !team.equalsIgnoreCase("none");

        // Build the prefix component (supports § color codes from LuckPerms)
        Component prefixComponent = LEGACY.deserialize(prefix);

        // Build team tag: [TeamName]
        Component teamComponent;
        if (hasTeam) {
            teamComponent = Component.text(" ")
                    .append(Component.text("["))
                    .append(LEGACY.deserialize(team))
                    .append(Component.text("] "));
        } else {
            teamComponent = Component.text(" ");
        }

        // Set the renderer to format: [Prefix] [Team] PlayerName: message
        event.renderer((source, sourceDisplayName, message, viewer) -> {
            return prefixComponent
                    .append(teamComponent)
                    .append(sourceDisplayName)
                    .append(Component.text(": "))
                    .append(message);
        });
    }
}
