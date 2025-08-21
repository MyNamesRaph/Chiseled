package com.mynamesraph.chiseled.compat.arealib

import dev.doublekekse.area_lib.component.AreaDataComponent
import dev.doublekekse.area_lib.data.AreaSavedData
import net.minecraft.nbt.CompoundTag

class AllowChiselingAreaComponent() : AreaDataComponent {
    override fun load(savedData: AreaSavedData, compoundTag: CompoundTag) {}
    override fun save(): CompoundTag { return CompoundTag() }
}