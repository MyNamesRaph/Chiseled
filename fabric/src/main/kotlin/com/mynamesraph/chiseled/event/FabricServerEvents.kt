package com.mynamesraph.chiseled.event

import com.mynamesraph.chiseled.Constants
import com.mynamesraph.chiseled.block.entity.ChiseledBlockEntity
import com.mynamesraph.chiseled.center
import com.mynamesraph.chiseled.compat.arealib.ChiseledAreaComponents
import com.mynamesraph.chiseled.compat.arealib.ChiseledAreaGamerules
import com.mynamesraph.chiseled.networking.ServerboundChiseledBlockPayload
import com.mynamesraph.chiseled.networking.onServerboundChiseledBlockPayload
import dev.doublekekse.area_lib.AreaLib
import net.fabricmc.fabric.api.event.player.PlayerPickItemEvents
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState

object FabricServerEvents {

    fun registerEvents() {
        PlayerPickItemEvents.BLOCK.register(::onPickBlock)
    }

    private fun onPickBlock(player: ServerPlayer ,pos: BlockPos , state: BlockState, requestIncludeData: Boolean): ItemStack? {
        val be = player.level().getBlockEntity(pos)

        if (be is ChiseledBlockEntity) {
            return be.copiedState.block.asItem().defaultInstance
        }

        return null
    }

    fun registerServerboundPayloads() {
        PayloadTypeRegistry.serverboundPlay().register(
            ServerboundChiseledBlockPayload.TYPE,
            ServerboundChiseledBlockPayload.STREAM_CODEC
        )
        ServerPlayNetworking.registerGlobalReceiver(ServerboundChiseledBlockPayload.TYPE, ::onFabricServerboundChiseledBlockPayload)
    }

    private fun onFabricServerboundChiseledBlockPayload(payload: ServerboundChiseledBlockPayload,context: ServerPlayNetworking.Context) {

        val player = context.player()
        val level = player.level()
        val pos = payload.blockPos

        if (FabricLoader.getInstance().isModLoaded("area_lib")) {
            if (!isValidArea(level,pos)) {
                player.sendSystemMessage(Component.translatable("chiseled.warning.not_chiselable_area"),true)
                return
            }
        }

        onServerboundChiseledBlockPayload(payload,player)
    }

    private fun isValidArea(level: ServerLevel, pos: BlockPos): Boolean {
        if (level.gameRules.get(ChiseledAreaGamerules.LIMIT_CHISELING_TO_CHISELING_AREAS)) {
            val areas = AreaLib.getSavedData(level.server).getSampledAreas(ChiseledAreaComponents.ALLOW_CHISELING_AREA,level,pos.center())

            for (area in areas) {
                Constants.LOG.error(area.type.toString())
                if (area.get(ChiseledAreaComponents.ALLOW_CHISELING_AREA) != null) {
                    return true
                }
            }
            return false
        }
        return true
    }
}