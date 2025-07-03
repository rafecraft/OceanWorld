package me.rafe.oceanWorld.config;

import me.rafe.oceanWorld.OceanWorld;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages plugin configuration settings
 */
public class ConfigManager {

    private final OceanWorld plugin;
    private FileConfiguration config;

    // Configuration values
    private List<Biome> allowedBiomes;
    private boolean coralReefsEnabled;
    private int waterLevel;
    private int seaFloorMinY;
    private int seaFloorMaxY;
    private List<Material> seaFloorMaterials;
    private boolean allowMobs;
    private boolean strictOceanOnly;
    private boolean underwaterCaves;
    private boolean underwaterRavines;

    // Prefix configuration
    private boolean prefixEnabled;
    private String prefixText;

    // World height limits
    private int minWorldY;
    private int maxWorldY;

    // Vanilla generation settings
    private boolean vanillaGenerationEnabled;
    private boolean vanillaBelowSeafloor;
    private boolean cavesBelowSeafloor;
    private boolean oresBelowSeafloor;

    // Structure settings
    private boolean oceanMonuments;
    private boolean shipwrecks;
    private boolean netherPortals;
    private boolean endPortals;
    private boolean ruins;

    public ConfigManager(OceanWorld plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();

        loadBiomes();
        loadBasicSettings();
        loadPrefixSettings();
        loadWaterSettings();
        loadSeaFloorSettings();
        loadWorldSettings();
        loadStructureSettings();
    }

    private void loadBiomes() {
        allowedBiomes = new ArrayList<>();
        List<String> biomeNames = config.getStringList("included_biomes");

        for (String biomeName : biomeNames) {
            try {
                // Use modern Registry API instead of deprecated valueOf
                NamespacedKey key = NamespacedKey.minecraft(biomeName.toLowerCase());
                Biome biome = Registry.BIOME.get(key);

                if (biome != null && isOceanBiome(biome)) {
                    allowedBiomes.add(biome);
                } else if (biome != null) {
                    plugin.getLogger().warning("Biome " + biomeName + " is not an ocean biome and will be ignored!");
                } else {
                    plugin.getLogger().warning("Unknown biome: " + biomeName);
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid biome name: " + biomeName);
            }
        }

        // Ensure we have at least one biome
        if (allowedBiomes.isEmpty()) {
            plugin.getLogger().warning("No valid ocean biomes found in config! Using default ocean biome.");
            Biome defaultOcean = Registry.BIOME.get(NamespacedKey.minecraft("ocean"));
            if (defaultOcean != null) {
                allowedBiomes.add(defaultOcean);
            }
        }
    }

    private void loadBasicSettings() {
        coralReefsEnabled = config.getBoolean("coral_reefs", true);
        allowMobs = config.getBoolean("allow_mobs", true);
    }

    private void loadPrefixSettings() {
        prefixEnabled = config.getBoolean("prefix.enabled", true);
        prefixText = config.getString("prefix.text", "[OceanWorld]");
    }

    private void loadWaterSettings() {
        waterLevel = config.getBoolean("water_height.enabled", true) ?
                    config.getInt("water_height.level", 62) : 62;
    }

    private void loadSeaFloorSettings() {
        if (config.getBoolean("sea_floor.enabled", true)) {
            seaFloorMinY = config.getInt("sea_floor.min_y", 30);
            seaFloorMaxY = config.getInt("sea_floor.max_y", 40);
        } else {
            seaFloorMinY = 30;
            seaFloorMaxY = 40;
        }

        seaFloorMaterials = new ArrayList<>();
        List<String> materialNames = config.getStringList("sea_floor.materials");

        for (String materialName : materialNames) {
            try {
                Material material = Material.valueOf(materialName.toUpperCase());
                if (material.isBlock()) {
                    seaFloorMaterials.add(material);
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Unknown material: " + materialName);
            }
        }

        // Default materials if none specified
        if (seaFloorMaterials.isEmpty()) {
            seaFloorMaterials.add(Material.SAND);
            seaFloorMaterials.add(Material.GRAVEL);
            seaFloorMaterials.add(Material.DIRT);
        }
    }

    private void loadWorldSettings() {
        strictOceanOnly = config.getBoolean("world_settings.strict_ocean_only", true);
        underwaterCaves = config.getBoolean("world_settings.underwater_caves", false);
        underwaterRavines = config.getBoolean("world_settings.underwater_ravines", false);

        // Load world height limits
        minWorldY = config.getInt("world_limits.min_world_y", -64);
        maxWorldY = config.getInt("world_limits.max_world_y", 320);

        // Load vanilla generation settings
        vanillaGenerationEnabled = config.getBoolean("vanilla_generation.enabled", true);
        vanillaBelowSeafloor = config.getBoolean("vanilla_generation.below_seafloor", true);
        cavesBelowSeafloor = config.getBoolean("vanilla_generation.caves_below_seafloor", true);
        oresBelowSeafloor = config.getBoolean("vanilla_generation.ores_below_seafloor", true);
    }

    private void loadStructureSettings() {
        oceanMonuments = config.getBoolean("structures.ocean_monuments", false);
        shipwrecks = config.getBoolean("structures.shipwrecks", false);
        netherPortals = config.getBoolean("structures.nether_portals", false);
        endPortals = config.getBoolean("structures.end_portals", false);
        ruins = config.getBoolean("structures.ruins", false);
    }

    private boolean isOceanBiome(Biome biome) {
        // Use modern Registry API to get the biome key
        NamespacedKey biomeKey = Registry.BIOME.getKey(biome);
        if (biomeKey == null) return false;

        String biomeName = biomeKey.getKey();
        return biomeName.equals("warm_ocean") ||
               biomeName.equals("lukewarm_ocean") ||
               biomeName.equals("ocean") ||
               biomeName.equals("cold_ocean") ||
               biomeName.equals("frozen_ocean") ||
               biomeName.equals("deep_lukewarm_ocean") ||
               biomeName.equals("deep_ocean") ||
               biomeName.equals("deep_cold_ocean") ||
               biomeName.equals("deep_frozen_ocean");
    }

    // Getter methods
    public List<Biome> getAllowedBiomes() { return allowedBiomes; }
    public boolean isCoralReefsEnabled() { return coralReefsEnabled; }
    public int getWaterLevel() { return waterLevel; }
    public int getSeaFloorMinY() { return seaFloorMinY; }
    public int getSeaFloorMaxY() { return seaFloorMaxY; }
    public List<Material> getSeaFloorMaterials() { return seaFloorMaterials; }
    public boolean shouldAllowMobs() { return allowMobs; }
    public boolean isStrictOceanOnly() { return strictOceanOnly; }
    public boolean shouldGenerateUnderwaterCaves() { return underwaterCaves; }
    public boolean shouldGenerateUnderwaterRavines() { return underwaterRavines; }
    public int getMinWorldY() { return minWorldY; }
    public int getMaxWorldY() { return maxWorldY; }
    public boolean isVanillaGenerationEnabled() { return vanillaGenerationEnabled; }
    public boolean isVanillaBelowSeafloor() { return vanillaBelowSeafloor; }
    public boolean isCavesBelowSeafloor() { return cavesBelowSeafloor; }
    public boolean isOresBelowSeafloor() { return oresBelowSeafloor; }

    // Structure getters
    public boolean shouldGenerateOceanMonuments() { return oceanMonuments; }
    public boolean shouldGenerateShipwrecks() { return shipwrecks; }
    public boolean shouldGenerateNetherPortals() { return netherPortals; }
    public boolean shouldGenerateEndPortals() { return endPortals; }
    public boolean shouldGenerateRuins() { return ruins; }

    // Prefix getters and utility methods
    public boolean isPrefixEnabled() { return prefixEnabled; }
    public String getPrefixText() { return prefixText; }

    /**
     * Formats a message with the plugin prefix
     * @param message The message to format
     * @return Formatted message with prefix and color codes translated
     */
    public String formatMessage(String message) {
        if (!prefixEnabled) {
            return org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
        }
        String formattedPrefix = org.bukkit.ChatColor.translateAlternateColorCodes('&', prefixText);
        String formattedMessage = org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
        return formattedPrefix + formattedMessage;
    }

    public boolean hasAnyStructuresEnabled() {
        return oceanMonuments || shipwrecks || netherPortals || endPortals || ruins;
    }

    public void reloadConfig() {
        loadConfig();
        plugin.getLogger().info("Configuration reloaded successfully!");
    }
}
