package com.mynamesraph.chiseled.registry

import com.mynamesraph.chiseled.Constants
import com.mynamesraph.chiseled.registry.NeoItems.ITEMS
import com.mynamesraph.chiseled.registry.data.block.ChiseledChiseledBlock
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object NeoBlocks {
    val BLOCKS: DeferredRegister.Blocks = DeferredRegister.createBlocks(
        Constants.MOD_ID
    )

    val map = enumValues<ChiseledBlocks>().associateWith { BLOCKS.register(
        it.block.name,
        Supplier {
            val resourceKey = ResourceKey.create(
                Registries.BLOCK,
                ResourceLocation.fromNamespaceAndPath(
                    Constants.MOD_ID,
                    it.block.name
                )
            )

            return@Supplier when (val chiseledBlock = it.block) {
                is ChiseledChiseledBlock -> {
                    chiseledBlock.factory(chiseledBlock.properties.setId(resourceKey))
                }
                else -> {
                    throw UnsupportedOperationException("Block with factory of ${chiseledBlock.factory} cannot be registered!")
                }
            }
        }
    ) }

    val itemMap = enumValues<ChiseledBlocks>().filter { it.block.hasItem }.associateWith {
        ITEMS.registerSimpleBlockItem(
            it.block.name,
            map[it]!!
        )
    }

    fun register(eventBus: IEventBus) {
        BLOCKS.register(eventBus)
    }
}