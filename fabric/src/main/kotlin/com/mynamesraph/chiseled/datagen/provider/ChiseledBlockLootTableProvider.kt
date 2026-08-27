package com.mynamesraph.chiseled.datagen.provider

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture

class ChiseledBlockLootTableProvider(
    dataOutput: FabricPackOutput,
    registryLookup: CompletableFuture<HolderLookup.Provider>
) : FabricBlockLootSubProvider(dataOutput, registryLookup) {
    override fun generate() {

    }
}