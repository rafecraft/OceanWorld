package me.rafe.oceanWorld.commands;

import me.rafe.oceanWorld.OceanWorld;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Handles the /oceanworldreload command for reloading plugin configuration
 */
public class OceanWorldReloadCommand implements CommandExecutor {

    private final OceanWorld plugin;

    public OceanWorldReloadCommand(OceanWorld plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Check permission
        if (!sender.hasPermission("oceanworld.reload")) {
            sender.sendMessage(plugin.getConfigManager().formatMessage("&cYou don't have permission to reload the plugin!"));
            return true;
        }

        // No arguments needed for reload
        if (args.length > 0) {
            sender.sendMessage(plugin.getConfigManager().formatMessage("&cUsage: /oceanworldreload"));
            return true;
        }

        try {
            // Show reloading message
            sender.sendMessage(plugin.getConfigManager().formatMessage("&eReloading OceanWorld configuration..."));

            // Reload the configuration
            plugin.getConfigManager().reloadConfig();

            // Show success message with new prefix (in case it changed)
            sender.sendMessage(plugin.getConfigManager().formatMessage("&aConfiguration reloaded successfully!"));

            // Log to console
            plugin.getLogger().info("Configuration reloaded by " + sender.getName());

        } catch (Exception e) {
            sender.sendMessage(plugin.getConfigManager().formatMessage("&cFailed to reload configuration! Check console for details."));
            plugin.getLogger().severe("Error reloading configuration: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }
}
