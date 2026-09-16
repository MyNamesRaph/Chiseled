package com.mynamesraph.chiseled.registry.data.loot

import net.minecraft.util.context.ContextKeySet
import kotlin.reflect.KFunction

data class ChiseledLootContextParamSet(
    override val name: String,
    override val factory: KFunction<ContextKeySet>
) : IChiseledLootContextParamSet