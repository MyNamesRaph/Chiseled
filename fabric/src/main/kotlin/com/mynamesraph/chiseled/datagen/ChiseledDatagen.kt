package com.mynamesraph.chiseled.datagen

import com.mynamesraph.chiseled.datagen.provider.ChiseledBlockLootTableProvider
import com.mynamesraph.chiseled.datagen.provider.ChiseledModelProvider
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

class ChiseledDatagen : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDatagenerator: FabricDataGenerator) {
        val pack = fabricDatagenerator.createPack()

        pack.addProvider(::ChiseledModelProvider)
        pack.addProvider(::ChiseledBlockLootTableProvider)
    }
}