package com.smpplugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class HealthListener implements Listener {

    private final SMPUtils plugin;

    public HealthListener(SMPUtils plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        // We only care about players
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        // Check if natural regeneration is disabled in config
        boolean naturalRegenEnabled = plugin.getConfig().getBoolean("natural-regeneration", true);
        if (!naturalRegenEnabled) {
            // SATIATED represents vanilla natural regeneration from full hunger/saturation
            if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                event.setCancelled(true);
            }
        }
    }
}
