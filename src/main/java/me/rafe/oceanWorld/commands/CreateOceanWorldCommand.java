package me.rafe.oceanWorld.commands;

import me.rafe.oceanWorld.OceanWorld;
import me.rafe.oceanWorld.generator.OceanChunkGenerator;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the /createoceanworld command
 */
public class CreateOceanWorldCommand implements CommandExecutor, TabCompleter {

    private final OceanWorld plugin;

    public CreateOceanWorldCommand(OceanWorld plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Check basic permission first
        if (!sender.hasPermission("oceanworld.create")) {
            sender.sendMessage(plugin.getConfigManager().formatMessage("&cYou don't have permission to create ocean worlds!"));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(plugin.getConfigManager().formatMessage("&cUsage: /oceanworldcreate <world-name>"));
            return true;
        }

        String worldName = args[0];

        // Check if world already exists
        if (plugin.getServer().getWorld(worldName) != null) {
            sender.sendMessage(plugin.getConfigManager().formatMessage("&cA world with the name '" + worldName + "' already exists!"));
            return true;
        }

        // Validate world name
        if (!isValidWorldName(worldName)) {
            sender.sendMessage(plugin.getConfigManager().formatMessage("&cInvalid world name! Use only letters, numbers, underscores, and hyphens."));
            return true;
        }

        sender.sendMessage(plugin.getConfigManager().formatMessage("&eCreating ocean world '" + worldName + "'... This may take a moment."));

        // FIX: Create the world entirely on the main thread to avoid WorldBorderCenterChangeEvent synchronization error
        // Use a delayed task to prevent blocking the command execution
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            try {
                WorldCreator creator = new WorldCreator(worldName);
                creator.generator(new OceanChunkGenerator(plugin));
                creator.generateStructures(plugin.getConfigManager().hasAnyStructuresEnabled());

                World world = creator.createWorld();

                if (world != null) {
                    // Configure world settings
                    configureWorld(world);

                    sender.sendMessage(plugin.getConfigManager().formatMessage("&aOcean world '" + worldName + "' created successfully!"));

                    if (sender instanceof Player player) {
                        player.sendMessage(plugin.getConfigManager().formatMessage("&bUse '/oceantp " + worldName + "' to visit your new ocean world!"));
                    }

                    plugin.getLogger().info("Ocean world '" + worldName + "' created by " + sender.getName());
                } else {
                    sender.sendMessage(plugin.getConfigManager().formatMessage("&cFailed to create ocean world '" + worldName + "'!"));
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Error creating ocean world '" + worldName + "': " + e.getMessage());
                e.printStackTrace();
                sender.sendMessage(plugin.getConfigManager().formatMessage("&cAn error occurred while creating the world. Check console for details."));
            }
        });

        return true;
    }

    private void configureWorld(World world) {
        // Configure world settings based on plugin configuration
        world.setSpawnFlags(plugin.getConfigManager().shouldAllowMobs(), plugin.getConfigManager().shouldAllowMobs());
        world.setKeepSpawnInMemory(false); // Ocean worlds don't need spawn chunks loaded

        // Set world spawn to a safe location (on water surface)
        int spawnY = plugin.getConfigManager().getWaterLevel();
        world.setSpawnLocation(0, spawnY, 0);

        // Configure world rules
        world.setGameRule(org.bukkit.GameRule.DO_MOB_SPAWNING, plugin.getConfigManager().shouldAllowMobs());

        // Disable weather if desired (ocean worlds with constant weather can be more immersive)
        world.setStorm(false);
        world.setThundering(false);
        world.setWeatherDuration(0);
    }

    private boolean isValidWorldName(String name) {
        // Check for valid characters and reasonable length
        return name.matches("^[a-zA-Z0-9_-]+$") && name.length() >= 1 && name.length() <= 32;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Suggest some example world names
            String partial = args[0].toLowerCase();
            String[] suggestions = {"ocean_world", "deep_seas", "endless_ocean", "aquatic_realm", "blue_world"};

            for (String suggestion : suggestions) {
                if (suggestion.startsWith(partial)) {
                    completions.add(suggestion);
                }
            }
        }

        return completions;
    }
}
