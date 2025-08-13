package com.mynamesraph.chiseled.registry.data.block

import com.mynamesraph.chiseled.block.ChiseledBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour

data class ChiseledChiseledBlock(
    override val name: String,
    override val properties: BlockBehaviour.Properties,
    override val hasItem: Boolean,
    private val blockFactory: (BlockBehaviour.Properties) -> Block = ::ChiseledBlock
) : IChiseledBlock {
    override val factory: (BlockBehaviour.Properties) -> Block = blockFactory
}