package com.dfsek.terra.bukkit.nms;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate.Sampler;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.stream.Stream;

import com.dfsek.terra.api.world.biome.generation.BiomeProvider;
import com.dfsek.terra.bukkit.world.BukkitPlatformBiome;


public class NMSBiomeProvider extends BiomeSource {
    private static final Method codec;
    static {
        try {
            codec = BiomeSource.class.getDeclaredMethod("codec");
            codec.setAccessible(true);
        } catch(NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static final MapCodec<BiomeSource> nmsBiomeMapCodec = BuiltInRegistries.BIOME_SOURCE.byNameCodec().dispatchMap(
        b -> {
            try {
                return (MapCodec<? extends BiomeSource>) codec.invoke(b);
            } catch(IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }, Function.identity());

    private final BiomeProvider delegate;
    private final long seed;
    private final Registry<Biome> biomeRegistry = RegistryFetcher.biomeRegistry();

    public NMSBiomeProvider(BiomeProvider delegate, long seed) {
        super();
        this.delegate = delegate;
        this.seed = seed;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return delegate.stream()
            .map(biome -> RegistryFetcher.biomeRegistry()
                .getHolderOrThrow(((BukkitPlatformBiome) biome.getPlatformBiome()).getContext()
                    .get(NMSBiomeInfo.class)
                    .biomeKey()));
    }

    @Override
    protected @NotNull MapCodec<? extends BiomeSource> codec() {
        return nmsBiomeMapCodec;
    }

    @Override
    public @NotNull Holder<Biome> getNoiseBiome(int x, int y, int z, @NotNull Sampler sampler) {
        return biomeRegistry.getHolderOrThrow(((BukkitPlatformBiome) delegate.getBiome(x << 2, y << 2, z << 2, seed)
            .getPlatformBiome()).getContext()
            .get(NMSBiomeInfo.class)
            .biomeKey());
    }
}
