package me.rafe.oceanWorld.generator;

import me.rafe.oceanWorld.OceanWorld;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

/**
 * Custom chunk generator for creating ocean-only worlds
 */
public class OceanChunkGenerator extends ChunkGenerator {

    private final OceanWorld plugin;
    private final List<Biome> allowedBiomes;
    private final boolean coralReefsEnabled;
    private final int waterLevel;
    private final int seaFloorMinY;
    private final int seaFloorMaxY;
    private final List<Material> seaFloorMaterials;
    private final boolean strictOceanOnly;

    public OceanChunkGenerator(OceanWorld plugin) {
        this.plugin = plugin;
        this.allowedBiomes = plugin.getConfigManager().getAllowedBiomes();
        this.coralReefsEnabled = plugin.getConfigManager().isCoralReefsEnabled();
        this.waterLevel = plugin.getConfigManager().getWaterLevel();
        this.seaFloorMinY = plugin.getConfigManager().getSeaFloorMinY();
        this.seaFloorMaxY = plugin.getConfigManager().getSeaFloorMaxY();
        this.seaFloorMaterials = plugin.getConfigManager().getSeaFloorMaterials();
        this.strictOceanOnly = plugin.getConfigManager().isStrictOceanOnly();
    }

    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        SimplexOctaveGenerator generator = new SimplexOctaveGenerator(new Random(worldInfo.getSeed()), 8);
        generator.setScale(0.005D);

        // Generate base terrain
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkX * 16 + x;
                int worldZ = chunkZ * 16 + z;

                // Generate seafloor height using noise
                double noise = generator.noise(worldX, worldZ, 0.5D, 0.5D);
                int seaFloorHeight = (int) (seaFloorMinY + (seaFloorMaxY - seaFloorMinY) * (noise + 1) / 2);

                // Generate from world minimum to maximum height
                for (int y = plugin.getConfigManager().getMinWorldY(); y <= plugin.getConfigManager().getMaxWorldY(); y++) {
                    if (y < seaFloorMinY) {
                        // Below seafloor - use vanilla generation if enabled
                        if (plugin.getConfigManager().isVanillaBelowSeafloor()) {
                            // Don't set any blocks here - let vanilla generation handle it
                            // This allows natural stone, ores, caves, etc. to generate
                            continue;
                        } else {
                            // Use stone as fallback if vanilla generation is disabled
                            chunkData.setBlock(x, y, z, Material.STONE);
                        }
                    } else if (y <= seaFloorHeight) {
                        // Seafloor layer - use our custom materials
                        Material material = getSeaFloorMaterial(random, y, seaFloorHeight);
                        chunkData.setBlock(x, y, z, material);
                    } else if (y <= waterLevel) {
                        // Above seafloor, below water level - fill with water
                        chunkData.setBlock(x, y, z, Material.WATER);
                    } else {
                        // Above water level - keep as air (strict ocean mode)
                        if (strictOceanOnly) {
                            chunkData.setBlock(x, y, z, Material.AIR);
                        }
                    }
                }

                // Generate coral reefs in warm ocean areas
                if (coralReefsEnabled && shouldGenerateCoral(worldX, worldZ, random)) {
                    generateCoralReef(chunkData, x, z, seaFloorHeight, random);
                }
            }
        }
    }

    @Override
    public BiomeProvider getDefaultBiomeProvider(WorldInfo worldInfo) {
        return new OceanBiomeProvider(allowedBiomes, worldInfo.getSeed());
    }

    @Override
    public boolean shouldGenerateNoise() {
        return true;
    }

    @Override
    public boolean shouldGenerateSurface() {
        return true;
    }

    @Override
    public boolean shouldGenerateCaves() {
        return plugin.getConfigManager().shouldGenerateUnderwaterCaves();
    }

    @Override
    public boolean shouldGenerateDecorations() {
        return true;
    }

    @Override
    public boolean shouldGenerateMobs() {
        return plugin.getConfigManager().shouldAllowMobs();
    }

    @Override
    public boolean shouldGenerateStructures() {
        return plugin.getConfigManager().hasAnyStructuresEnabled();
    }

    private Material getSeaFloorMaterial(Random random, int currentY, int seaFloorHeight) {
        // Top layer is more likely to be sand, deeper layers more likely to be dirt/gravel
        double depthFactor = (double) currentY / seaFloorHeight;

        if (depthFactor > 0.8) {
            // Surface layer - prefer sand
            return random.nextFloat() < 0.7 ? Material.SAND : getRandomSeaFloorMaterial(random);
        } else if (depthFactor > 0.4) {
            // Middle layer - mix of materials
            return getRandomSeaFloorMaterial(random);
        } else {
            // Deep layer - prefer stone and dirt
            return random.nextFloat() < 0.6 ? Material.STONE : Material.DIRT;
        }
    }

    private Material getRandomSeaFloorMaterial(Random random) {
        return seaFloorMaterials.get(random.nextInt(seaFloorMaterials.size()));
    }

    private boolean shouldGenerateCoral(int worldX, int worldZ, Random random) {
        // Generate coral reefs with some spacing and randomness
        return (worldX % 32 == 0 || worldZ % 32 == 0) && random.nextFloat() < 0.15;
    }

    private void generateCoralReef(ChunkData chunkData, int x, int z, int seaFloorHeight, Random random) {
        // Generate coral blocks on and near the seafloor
        int coralHeight = seaFloorHeight + 1 + random.nextInt(3);

        if (coralHeight < waterLevel - 2) { // Ensure coral is underwater
            Material[] coralBlocks = {
                Material.BRAIN_CORAL_BLOCK,
                Material.BUBBLE_CORAL_BLOCK,
                Material.FIRE_CORAL_BLOCK,
                Material.HORN_CORAL_BLOCK,
                Material.TUBE_CORAL_BLOCK
            };

            Material[] coralPlants = {
                Material.BRAIN_CORAL,
                Material.BUBBLE_CORAL,
                Material.FIRE_CORAL,
                Material.HORN_CORAL,
                Material.TUBE_CORAL,
                Material.BRAIN_CORAL_FAN,
                Material.BUBBLE_CORAL_FAN,
                Material.FIRE_CORAL_FAN,
                Material.HORN_CORAL_FAN,
                Material.TUBE_CORAL_FAN
            };

            // Place coral block
            chunkData.setBlock(x, coralHeight, z, coralBlocks[random.nextInt(coralBlocks.length)]);

            // Sometimes add coral plants on top
            if (random.nextFloat() < 0.6 && coralHeight + 1 < waterLevel) {
                chunkData.setBlock(x, coralHeight + 1, z, coralPlants[random.nextInt(coralPlants.length)]);
            }

            // Add some sea pickles for variety
            if (random.nextFloat() < 0.3 && coralHeight + 1 < waterLevel) {
                chunkData.setBlock(x, coralHeight + 1, z, Material.SEA_PICKLE);
            }
        }
    }
}
