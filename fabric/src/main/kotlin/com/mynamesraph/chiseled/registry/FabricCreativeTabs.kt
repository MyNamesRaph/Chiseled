package com.mynamesraph.chiseled.registry

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

object FabricCreativeTabs {
    val CYANIDE_CREATIVE_TAB_KEY: ResourceKey<CreativeModeTab> = ResourceKey.create(
        Registries.CREATIVE_MODE_TAB,
        Identifier.fromNamespaceAndPath(
            com.mynamesraph.chiseled.Constants.MOD_ID,
            "item_group_all"
        )
    )

    val CYANIDE_CREATIVE_TAB: CreativeModeTab = FabricCreativeModeTab.builder()
        .icon { ItemStack(FabricItems.map[ChiseledItems.DIAMOND_CHISEL]!!) }
        .title(Component.translatable("itemGroup.${com.mynamesraph.chiseled.Constants.MOD_ID}.all"))
        .build()

    init {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,CYANIDE_CREATIVE_TAB_KEY,CYANIDE_CREATIVE_TAB)


        CreativeModeTabEvents.modifyOutputEvent(CYANIDE_CREATIVE_TAB_KEY).register {
            FabricItems.map.entries.forEach { item -> it.accept(item.value) }
            FabricBlocks.itemMap.entries.forEach { item -> it.accept(item.value) }
        }
    }
}