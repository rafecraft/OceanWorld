package me.rafe.oceanWorld;

import me.rafe.oceanWorld.commands.CreateOceanWorldCommand;
import me.rafe.oceanWorld.commands.OceanTeleportCommand;
import me.rafe.oceanWorld.commands.OceanWorldReloadCommand;
import me.rafe.oceanWorld.config.ConfigManager;
import me.rafe.oceanWorld.generator.OceanChunkGenerator;
import org.bukkit.ChatColor;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public final class OceanWorld extends JavaPlugin {

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        // Initialize configuration manager
        configManager = new ConfigManager(this);

        // Register commands
        CreateOceanWorldCommand createCommand = new CreateOceanWorldCommand(this);
        getCommand("oceanworldcreate").setExecutor(createCommand);
        getCommand("oceanworldcreate").setTabCompleter(createCommand);

        OceanTeleportCommand teleportCommand = new OceanTeleportCommand(this);
        getCommand("oceantp").setExecutor(teleportCommand);
        getCommand("oceantp").setTabCompleter(teleportCommand);

        OceanWorldReloadCommand reloadCommand = new OceanWorldReloadCommand(this);
        getCommand("oceanworldreload").setExecutor(reloadCommand);

        // Log startup message
        getLogger().info(ChatColor.AQUA + "OceanWorld plugin enabled successfully!");
        getLogger().info("Loaded " + configManager.getAllowedBiomes().size() + " ocean biomes");
        getLogger().info("Water level: " + configManager.getWaterLevel());
        getLogger().info("Seafloor range: Y" + configManager.getSeaFloorMinY() + " to Y" + configManager.getSeaFloorMaxY());
    }

    @Override
    public void onDisable() {
        getLogger().info("OceanWorld plugin disabled. Goodbye!");
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new OceanChunkGenerator(this);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
