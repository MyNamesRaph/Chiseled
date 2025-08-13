package com.mynamesraph.chiseled.registry

import com.mynamesraph.chiseled.Constants
import com.mynamesraph.chiseled.registry.data.block.ChiseledChiseledBlock
import com.mynamesraph.chiseled.registry.data.block.IChiseledBlock
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

object FabricBlocks {

    fun register(chiseledBlock: IChiseledBlock): Block {

        val location = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, chiseledBlock.name)

        val resourceKey = ResourceKey.create(Registries.BLOCK,location)

        val block = when (chiseledBlock) {
            is ChiseledChiseledBlock -> {
                chiseledBlock.factory(chiseledBlock.properties.setId(resourceKey))
            }
            else -> {
                throw UnsupportedOperationException("Block with factory of ${chiseledBlock.factory} cannot be registered!")
            }
        }

        Registry.register(BuiltInRegistries.BLOCK, resourceKey, block)

        return block
    }

    fun registerBlockItem(cyanideBlock: IChiseledBlock, block: Block): BlockItem {
        val location = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, cyanideBlock.name)

        val resourceKey = ResourceKey.create(Registries.ITEM,location)

        val blockItem = BlockItem(block,Item.Properties().setId(resourceKey).useBlockDescriptionPrefix())
        Registry.register(BuiltInRegistries.ITEM,resourceKey,blockItem)

        return blockItem
    }

    val map = enumValues<ChiseledBlocks>().associateWith { register(it.block) }

    val itemMap = enumValues<ChiseledBlocks>().filter {
        it.block.hasItem
    }.associateWith {
        registerBlockItem(
            it.block,
            map[it]!!
            )
    }
}