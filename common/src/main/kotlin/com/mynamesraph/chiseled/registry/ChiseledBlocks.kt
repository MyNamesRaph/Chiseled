package com.mynamesraph.chiseled.registry

import com.mynamesraph.chiseled.registry.data.block.ChiseledChiseledBlock
import com.mynamesraph.chiseled.registry.data.block.IChiseledBlock
import net.minecraft.world.level.block.state.BlockBehaviour


enum class ChiseledBlocks(val block: IChiseledBlock) {
     CHISELED_BLOCK(
         ChiseledChiseledBlock(
             "chiseled_block",
             BlockBehaviour.Properties.of().destroyTime(0.5f),
             false
         )
     )
}