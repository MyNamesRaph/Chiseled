package com.mynamesraph.chiseled.compat.arealib

import com.mynamesraph.chiseled.Constants
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder
import net.minecraft.resources.Identifier
import net.minecraft.world.level.gamerules.GameRule
import net.minecraft.world.level.gamerules.GameRuleCategory


object ChiseledAreaGamerules {
    
    val LIMIT_CHISELING_TO_CHISELING_AREAS: GameRule<Boolean> = GameRuleBuilder
        .forBoolean(true)
        .category(GameRuleCategory.PLAYER)
        .buildAndRegister(Identifier.fromNamespaceAndPath(Constants.MOD_ID,"limitChiselingToChiselingAreas"))
}