package com.mynamesraph.chiseled.registry.data.item

import net.minecraft.world.item.Item

interface IChiseledItem {
    val name: String
    val factory: Function<Item>
    val properties: Item.Properties
}