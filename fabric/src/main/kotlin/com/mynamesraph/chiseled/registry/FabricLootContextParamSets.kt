package com.mynamesraph.chiseled.registry

import com.mynamesraph.chiseled.Constants
import com.mynamesraph.chiseled.registry.data.loot.ChiseledLootContextParamSet
import com.mynamesraph.chiseled.registry.data.loot.IChiseledLootContextParamSet
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.util.context.ContextKeySet

object FabricLootContextParamSets {
    fun register(chiseledLootContextParamSet: IChiseledLootContextParamSet): ContextKeySet {
        val location = Identifier.fromNamespaceAndPath(Constants.MOD_ID, chiseledLootContextParamSet.name)
        val resourceKey = ResourceKey.create(Registries.CONTEXT_KEY_SET, location)

        val keySet = chiseledLootContextParamSet.factory.call()

        Registry.register(BuiltInRegistries.CONTEXT_KEY_SET, resourceKey, keySet)

        return keySet
    }

    val map = enumValues<ChiseledLootContextParamSets>().associateWith { register(it.keySet) }
}