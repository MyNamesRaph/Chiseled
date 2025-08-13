package com.mynamesraph.chiseled.registry.data.block.entity

import com.mynamesraph.chiseled.registry.ChiseledBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

data class ChiseledSimpleBlockEntity(
    override val name: String,
    override val blocks: Set<ChiseledBlocks>,
    private val blockEntityFactory: (BlockPos, BlockState) -> BlockEntity
    ) : IChiseledBlockEntity {
    override val factory: (BlockPos, BlockState) -> BlockEntity = blockEntityFactory
}