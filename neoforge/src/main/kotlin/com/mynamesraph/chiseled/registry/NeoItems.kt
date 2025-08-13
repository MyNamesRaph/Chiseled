package com.mynamesraph.chiseled.registry

import com.mynamesraph.chiseled.Constants
import com.mynamesraph.chiseled.registry.data.item.ChiseledChiselItem
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object NeoItems {
    val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(
        Constants.MOD_ID
    )

    val map = enumValues<ChiseledItems>().associateWith { ITEMS.register(
        it.item.name,
        Supplier {
            val resourceKey = ResourceKey.create(
                Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath(
                    Constants.MOD_ID,
                    it.item.name
                )
            )
            return@Supplier when (val chiseledItem = it.item) {
                is ChiseledChiselItem -> {
                    chiseledItem.factory(
                        chiseledItem.material,
                        chiseledItem.attackDamage,
                        chiseledItem.attackSpeed,
                        chiseledItem.properties.setId(resourceKey)
                    )
                }
                else -> {
                    throw UnsupportedOperationException("Item with factory of ${chiseledItem.factory} cannot be registered!")
                }
            }
        }
    ) }


    fun register(eventBus: IEventBus) {
        ITEMS.register(eventBus)
    }

}


