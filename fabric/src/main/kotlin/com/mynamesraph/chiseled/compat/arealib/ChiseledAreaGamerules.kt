package com.mynamesraph.chiseled.compat.arealib

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry
import net.minecraft.world.level.GameRules

object ChiseledAreaGamerules {
    val LIMIT_CHISELING_TO_CHISELING_AREAS = GameRuleRegistry.register(
        "limitChiselingToChiselingAreas",
        GameRules.Category.PLAYER,
        GameRuleFactory.createBooleanRule(true)
    )
}