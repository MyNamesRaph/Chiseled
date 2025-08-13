package com.mynamesraph.chiseled.registry

import com.mynamesraph.chiseled.block.entity.ChiseledBlockEntity
import com.mynamesraph.chiseled.registry.data.block.entity.ChiseledSimpleBlockEntity
import com.mynamesraph.chiseled.registry.data.block.entity.IChiseledBlockEntity

enum class ChiseledBlockEntities(val blockEntity: IChiseledBlockEntity) {
    CHISELED_BLOCK_ENTITY(
        ChiseledSimpleBlockEntity(
            "chiseled_block",
            setOf(ChiseledBlocks.CHISELED_BLOCK),
            ::ChiseledBlockEntity
        )
    )
}