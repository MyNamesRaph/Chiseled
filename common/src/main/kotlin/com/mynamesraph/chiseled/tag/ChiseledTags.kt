package com.mynamesraph.chiseled.tag

import com.mynamesraph.chiseled.Constants
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block

object ChiseledTags {
    val CHISEL_MINEABLE: TagKey<Block> = TagKey.create(
        BuiltInRegistries.BLOCK.key(),
        ResourceLocation.fromNamespaceAndPath(
            Constants.MOD_ID,
            "mineable/chisel"
        )
    )
}