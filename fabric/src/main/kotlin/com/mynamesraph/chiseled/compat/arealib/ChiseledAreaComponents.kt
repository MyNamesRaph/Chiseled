package com.mynamesraph.chiseled.compat.arealib

import com.mynamesraph.chiseled.Constants
import dev.doublekekse.area_lib.registry.AreaDataComponentTypeRegistry
import net.minecraft.resources.ResourceLocation

object ChiseledAreaComponents {
    val ALLOW_CHISELING_AREA = AreaDataComponentTypeRegistry.registerTracking(
        ResourceLocation.fromNamespaceAndPath(
            Constants.MOD_ID,
            "allow_chiseling"
        ),
        ::AllowChiselingAreaComponent
        )
}