package com.mynamesraph.chiseled.registry


import com.mynamesraph.chiseled.Constants
import com.mynamesraph.chiseled.mixin.LootContextParamSetsAccessor
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.context.ContextKeySet
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import java.util.function.Consumer

object ChiseledLootContextParamSets {
    val CHISELING = register("chiseling") {
        it.required(LootContextParams.ORIGIN)
            .required(LootContextParams.TOOL)
            .required(LootContextParams.BLOCK_STATE)
            .required(LootContextParams.BLOCK_ENTITY)
    }

    fun register(name: String, constructor: Consumer<ContextKeySet.Builder>): ContextKeySet {
        val builder = ContextKeySet.Builder()
        constructor.accept(builder)
        val contextKeyset = builder.build()
        val location = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name)
        val value = LootContextParamSetsAccessor.getREGISTRY().put(location, contextKeyset)
        if (value != null) {
            throw IllegalStateException("Loot table parameter set $location is already registered")
        }

        return contextKeyset
    }
}