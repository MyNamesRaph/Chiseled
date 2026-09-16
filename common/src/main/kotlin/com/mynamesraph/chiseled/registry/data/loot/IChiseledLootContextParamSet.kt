package com.mynamesraph.chiseled.registry.data.loot

import net.minecraft.util.context.ContextKeySet
import kotlin.reflect.KFunction

interface IChiseledLootContextParamSet {
    val name: String
    val factory: KFunction<ContextKeySet>
}