package me.rafe.oceanWorld.commands;

import me.rafe.oceanWorld.OceanWorld;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the /oceantp command for teleporting to worlds
 */
public class OceanTeleportCommand implements CommandExecutor, TabCompleter {

    private final OceanWorld plugin;

    public OceanTeleportCommand(OceanWorld plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Only players can teleport
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().formatMessage("&cOnly players can use the teleport command!"));
            return true;
        }

        Player player = (Player) sender;

        // Check permission
        if (!player.hasPermission("oceanworld.teleport")) {
            player.sendMessage(plugin.getConfigManager().formatMessage("&cYou don't have permission to teleport to worlds!"));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(plugin.getConfigManager().formatMessage("&cUsage: /oceantp <world-name>"));
            return true;
        }

        String worldName = args[0];
        World targetWorld = plugin.getServer().getWorld(worldName);

        if (targetWorld == null) {
            player.sendMessage(plugin.getConfigManager().formatMessage("&cWorld '" + worldName + "' does not exist!"));
            return true;
        }

        // Get safe teleport location (spawn point of the world)
        Location teleportLocation = targetWorld.getSpawnLocation();

        // Ensure the location is safe (above water for ocean worlds)
        teleportLocation = getSafeLocation(teleportLocation);

        try {
            player.teleport(teleportLocation);
            player.sendMessage(plugin.getConfigManager().formatMessage("&aTeleported to world '" + worldName + "'!"));
            plugin.getLogger().info("Player " + player.getName() + " teleported to world '" + worldName + "'");
        } catch (Exception e) {
            player.sendMessage(plugin.getConfigManager().formatMessage("&cFailed to teleport to world '" + worldName + "'!"));
            plugin.getLogger().warning("Failed to teleport " + player.getName() + " to world " + worldName + ": " + e.getMessage());
        }

        return true;
    }

    private Location getSafeLocation(Location location) {
        World world = location.getWorld();
        if (world == null) return location;

        // For ocean worlds, ensure the player spawns above water
        int x = location.getBlockX();
        int z = location.getBlockZ();

        // Find the highest non-air block and add some height
        int y = world.getHighestBlockYAt(x, z) + 2;

        // Ensure minimum height above sea level
        y = Math.max(y, 65); // Above typical water level

        return new Location(world, x + 0.5, y, z + 0.5);
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String partial = args[0].toLowerCase();

            // Get all world names for tab completion
            for (World world : plugin.getServer().getWorlds()) {
                String worldName = world.getName();
                if (worldName.toLowerCase().startsWith(partial)) {
                    completions.add(worldName);
                }
            }
        }

        return completions;
    }
}
