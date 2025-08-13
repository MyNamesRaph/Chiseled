package com.mynamesraph.chiseled.registry

import com.mynamesraph.chiseled.Constants
import com.mynamesraph.chiseled.registry.data.item.ChiseledChiselItem
import com.mynamesraph.chiseled.registry.data.item.IChiseledItem
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item

object FabricItems {

    fun register(chiseledItem: IChiseledItem): Item {

        val location = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, chiseledItem.name)

        val resourceKey = ResourceKey.create(Registries.ITEM,location)

        val item = when (chiseledItem) {
            is ChiseledChiselItem -> {
                chiseledItem.factory(chiseledItem.material,chiseledItem.attackDamage,chiseledItem.attackSpeed, chiseledItem.properties.setId(resourceKey))
            }
            else -> {
                throw UnsupportedOperationException("Item with factory of ${chiseledItem.factory} cannot be registered!")
            }
        }

        Registry.register(BuiltInRegistries.ITEM, resourceKey, item)

        return item
    }


    val map = enumValues<ChiseledItems>().associateWith { register(it.item) }
}