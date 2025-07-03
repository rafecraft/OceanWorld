package me.rafe.oceanWorld.generator;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

/**
 * Custom biome provider that only provides ocean biomes
 */
public class OceanBiomeProvider extends BiomeProvider {

    private final List<Biome> allowedBiomes;
    private final long seed;

    public OceanBiomeProvider(List<Biome> allowedBiomes, long seed) {
        this.allowedBiomes = allowedBiomes;
        this.seed = seed;
    }

    @Override
    public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
        // Use coordinates to create deterministic but varied biome selection
        Random chunkRandom = new Random(seed + (long) x * 341873128712L + (long) z * 132897987541L);
        return allowedBiomes.get(chunkRandom.nextInt(allowedBiomes.size()));
    }

    @Override
    public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
        return allowedBiomes;
    }
}
