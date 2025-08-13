package com.mynamesraph.chiseled.registry.data.block

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour

interface IChiseledBlock {
    val name: String
    val factory: Function<Block>
    val properties: BlockBehaviour.Properties
    val hasItem: Boolean
}