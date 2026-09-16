package com.mynamesraph.chiseled.registry


import com.mynamesraph.chiseled.Constants
import com.mynamesraph.chiseled.registry.data.loot.ChiseledLootContextParamSet
import com.mynamesraph.chiseled.registry.data.loot.IChiseledLootContextParamSet
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.util.context.ContextKeySet
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import java.util.function.Consumer

enum class ChiseledLootContextParamSets(val keySet: IChiseledLootContextParamSet) {
    CHISELING(
        ChiseledLootContextParamSet(
            "chiseling",
            ContextKeySet.Builder()
                .required(LootContextParams.ORIGIN)
                .required(LootContextParams.TOOL)
                .required(LootContextParams.BLOCK_STATE)
                .required(LootContextParams.BLOCK_ENTITY)::build
        )
    )
}