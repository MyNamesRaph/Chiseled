package com.mynamesraph.chiseled.compat.arealib

import com.mynamesraph.chiseled.Constants
import dev.doublekekse.area_lib.component.SampledAreaComponentType
import dev.doublekekse.area_lib.registry.AreaComponentRegistry
import net.minecraft.resources.Identifier
import net.minecraft.util.Unit

object ChiseledAreaComponents {
    val ALLOW_CHISELING_AREA: SampledAreaComponentType<Unit> = AreaComponentRegistry.registerSampled(
        Identifier.fromNamespaceAndPath("my_mod", "building_allowed"),
        Unit.CODEC
    )
}