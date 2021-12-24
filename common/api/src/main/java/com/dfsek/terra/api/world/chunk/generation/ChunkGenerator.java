/*
 * Copyright (c) 2020-2021 Polyhedral Development
 *
 * The Terra API is licensed under the terms of the MIT License. For more details,
 * reference the LICENSE file in the common/api directory.
 */

package com.dfsek.terra.api.world.chunk.generation;

import com.dfsek.terra.api.world.info.WorldProperties;

import org.jetbrains.annotations.NotNull;

import com.dfsek.terra.api.block.state.BlockState;
import com.dfsek.terra.api.util.vector.Vector3;
import com.dfsek.terra.api.util.vector.Vector3Int;
import com.dfsek.terra.api.world.WritableWorld;


public interface ChunkGenerator {
    void generateChunkData(@NotNull ProtoChunk chunk, @NotNull WorldProperties world,
                           int chunkX, int chunkZ);
    
    BlockState getBlock(WorldProperties world, int x, int y, int z);
    
    default BlockState getBlock(WorldProperties world, Vector3 vector3) {
        return getBlock(world, vector3.getBlockX(), vector3.getBlockY(), vector3.getBlockZ());
    }
    
    default BlockState getBlock(WorldProperties world, Vector3Int vector3) {
        return getBlock(world, vector3.getX(), vector3.getY(), vector3.getZ());
    }
}
