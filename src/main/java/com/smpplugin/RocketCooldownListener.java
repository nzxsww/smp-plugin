package com.smpplugin;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RocketCooldownListener implements Listener {

    private final SMPUtils plugin;

    public RocketCooldownListener(SMPUtils plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onElytraBoost(com.destroystokyo.paper.event.player.PlayerElytraBoostEvent event) {
        Player player = event.getPlayer();

        // Check if player already has cooldown (Minecraft's native system)
        if (player.hasCooldown(Material.FIREWORK_ROCKET)) {
            event.setCancelled(true);
            
            // Calculate remaining time
            int remainingTicks = player.getCooldown(Material.FIREWORK_ROCKET);
            int remainingSecs = (remainingTicks + 19) / 20; // Round up
            
            // Avoid spamming the chat every tick they spam right click
            player.sendActionBar(net.kyori.adventure.text.Component.text("§cDebes esperar §e" + remainingSecs + " §csegundo(s) antes de usar otro cohete."));
            return;
        }

        // Apply cooldown (convert seconds to ticks: 1 second = 20 ticks)
        int cooldownSeconds = plugin.getConfig().getInt("elytra-rocket-cooldown", 2);
        player.setCooldown(Material.FIREWORK_ROCKET, cooldownSeconds * 20);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onElytraDamage(PlayerItemDamageEvent event) {
        // Get durability reduction percentage from config (default 20%)
        int reductionPercent = plugin.getConfig().getInt("elytra-durability-reduction", 20);
        
        if (event.getItem().getType() == Material.ELYTRA && reductionPercent > 0) {
            // To reduce uses by X%, increase damage by X/(100-X)
            // Example: 20% reduction = 20/(100-20) = 25% chance of extra damage
            double extraDamageChance = (double) reductionPercent / (100.0 - reductionPercent);
            
            if (Math.random() < extraDamageChance) {
                event.setDamage(event.getDamage() + 1);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack result = event.getResult();
        
        // Check if result is an Elytra
        if (result == null || result.getType() != Material.ELYTRA) return;
        
        // Get max unbreaking level from config (default 2)
        int maxUnbreaking = plugin.getConfig().getInt("elytra-max-unbreaking", 2);
        
        // If max unbreaking is 0 or less, skip this check
        if (maxUnbreaking <= 0) return;
        
        if (result.containsEnchantment(Enchantment.UNBREAKING)) {
            int unbreakingLevel = result.getEnchantmentLevel(Enchantment.UNBREAKING);
            
            // If Unbreaking is above max, downgrade to max
            if (unbreakingLevel > maxUnbreaking) {
                // Modify the meta instead of the item directly to be safe in PrepareAnvil
                ItemMeta meta = result.getItemMeta();
                if (meta != null) {
                    meta.removeEnchant(Enchantment.UNBREAKING);
                    meta.addEnchant(Enchantment.UNBREAKING, maxUnbreaking, true);
                    result.setItemMeta(meta);
                    event.setResult(result);
                    // We DO NOT send a message here because PrepareAnvil triggers constantly when typing in the anvil
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent event) {
        // Check if player is clicking
        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() != Material.ELYTRA) return;
        
        int maxUnbreaking = plugin.getConfig().getInt("elytra-max-unbreaking", 2);
        if (maxUnbreaking <= 0) return;
        
        // Check if Elytra has Unbreaking above allowed level
        if (clickedItem.containsEnchantment(Enchantment.UNBREAKING)) {
            int unbreakingLevel = clickedItem.getEnchantmentLevel(Enchantment.UNBREAKING);
            
            if (unbreakingLevel > maxUnbreaking) {
                ItemMeta meta = clickedItem.getItemMeta();
                if (meta != null) {
                    meta.removeEnchant(Enchantment.UNBREAKING);
                    meta.addEnchant(Enchantment.UNBREAKING, maxUnbreaking, true);
                    clickedItem.setItemMeta(meta);
                    // Update the item in the inventory
                    event.setCurrentItem(clickedItem);
                    
                    // Use Action Bar to avoid chat spam during inventory clicks
                    player.sendActionBar(net.kyori.adventure.text.Component.text("§cLas Elytras han sido limitadas a Unbreaking " + maxUnbreaking + "."));
                }
            }
        }
    }
}