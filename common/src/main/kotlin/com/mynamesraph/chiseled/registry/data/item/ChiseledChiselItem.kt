package com.mynamesraph.chiseled.registry.data.item

import com.mynamesraph.chiseled.item.ChiselItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ToolMaterial

data class ChiseledChiselItem(
    override val name: String,
    val material: ToolMaterial,
    val attackDamage: Float,
    val attackSpeed: Float,
    override val properties: Item.Properties,
    private val itemFactory: (ToolMaterial, Float, Float, Item.Properties) -> Item = ::ChiselItem
) : IChiseledItem {
    override val factory: (ToolMaterial, Float, Float, Item.Properties) -> Item = itemFactory
}