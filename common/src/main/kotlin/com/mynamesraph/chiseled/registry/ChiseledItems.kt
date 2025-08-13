package com.mynamesraph.chiseled.registry

import com.mynamesraph.chiseled.registry.data.item.ChiseledChiselItem
import com.mynamesraph.chiseled.registry.data.item.IChiseledItem
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.Item
import net.minecraft.world.item.ToolMaterial

const val attackDamage = 0.5f
const val attackSpeed = 6.0f

enum class ChiseledItems(val item: IChiseledItem) {
     WOODEN_CHISEL(
          ChiseledChiselItem(
               "wooden_chisel",
               ToolMaterial.WOOD,
               attackDamage,
               attackSpeed,
               Item.Properties()
          )
     ),
     STONE_CHISEL(
          ChiseledChiselItem(
               "stone_chisel",
               ToolMaterial.STONE,
               attackDamage,
               attackSpeed,
               Item.Properties()
          )
     ),
     IRON_CHISEL(
          ChiseledChiselItem(
               "iron_chisel",
               ToolMaterial.IRON,
               attackDamage,
               attackSpeed,
               Item.Properties()
          )
     ),
     GOLDEN_CHISEL(
          ChiseledChiselItem(
               "golden_chisel",
               ToolMaterial.GOLD,
               attackDamage,
               attackSpeed,
               Item.Properties()
          )
     ),
     DIAMOND_CHISEL(
          ChiseledChiselItem(
               "diamond_chisel",
               ToolMaterial.DIAMOND,
               attackDamage,
               attackSpeed,
               Item.Properties()
          )
     ),
     NETHERITE_CHISEL(
          ChiseledChiselItem(
               "netherite_chisel",
               ToolMaterial.NETHERITE,
               attackDamage,
               attackSpeed,
               Item.Properties().fireResistant()
          )
     );
     /*ELECTRIC_CHISEL(
          ChiseledChiselItem(
               "electric_chisel",
               ToolMaterial.IRON,
               1.0f,
               1.0f,
               Item.Properties()
          )
     )*/
}