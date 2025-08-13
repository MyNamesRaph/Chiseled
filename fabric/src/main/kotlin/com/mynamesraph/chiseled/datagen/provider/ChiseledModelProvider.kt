package com.mynamesraph.chiseled.datagen.provider

import com.mynamesraph.chiseled.registry.ChiseledItems
import com.mynamesraph.chiseled.registry.FabricItems
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ModelTemplates

class ChiseledModelProvider(output: FabricDataOutput) : FabricModelProvider(output) {
    override fun generateBlockStateModels(generator: BlockModelGenerators) {
        //generator.createTrivialCube(FabricBlocks.map[ChiseledBlocks.CYANIDE_BLOCK]!!)
        //generator.createTrivialCube(FabricBlocks.map[ChiseledBlocks.CYANIDE_BLEND_BLOCK]!!)
    }

    override fun generateItemModels(generator: ItemModelGenerators) {
        simpleItem(ChiseledItems.WOODEN_CHISEL, generator)
        simpleItem(ChiseledItems.STONE_CHISEL, generator)
        simpleItem(ChiseledItems.IRON_CHISEL, generator)
        simpleItem(ChiseledItems.GOLDEN_CHISEL, generator)
        simpleItem(ChiseledItems.DIAMOND_CHISEL, generator)
        simpleItem(ChiseledItems.NETHERITE_CHISEL, generator)
        //simpleItem(ChiseledItems.ELECTRIC_CHISEL, generator)
    }

    private fun simpleItem(item: ChiseledItems, generator: ItemModelGenerators) {
        generator.generateFlatItem(FabricItems.map[item]!!, ModelTemplates.FLAT_HANDHELD_ITEM)
    }
}