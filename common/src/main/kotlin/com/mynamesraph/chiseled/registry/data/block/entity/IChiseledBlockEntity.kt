package com.mynamesraph.chiseled.registry.data.block.entity

import com.mynamesraph.chiseled.registry.ChiseledBlocks
import net.minecraft.world.level.block.entity.BlockEntity

interface IChiseledBlockEntity {
    val name: String
    val blocks: Set<ChiseledBlocks>
    val factory: Function<BlockEntity>
}