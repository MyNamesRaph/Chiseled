package com.mynamesraph.chiseled

import com.mynamesraph.chiseled.registry.NeoBlockEntities
import com.mynamesraph.chiseled.registry.NeoBlocks
import com.mynamesraph.chiseled.registry.NeoCreativeTabs
import com.mynamesraph.chiseled.registry.NeoItems
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod

@Mod(Constants.MOD_ID)
class ChiseledNeo(eventBus: IEventBus, modContainer: ModContainer) {
    init {
        ChiseledCommon.init()
        NeoItems.register(eventBus)
        NeoBlocks.register(eventBus)
        NeoBlockEntities.register(eventBus)
        NeoCreativeTabs.register(eventBus)
    }
}