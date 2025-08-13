package com.mynamesraph.chiseled.platform

import com.mynamesraph.chiseled.platform.services.PlatformHelper
import com.mynamesraph.chiseled.registry.ChiseledBlocks
import com.mynamesraph.chiseled.registry.NeoBlocks
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.Block
import net.neoforged.fml.ModList
import net.neoforged.fml.loading.FMLLoader

class NeoForgePlatformHelper : PlatformHelper {
    override fun getPlatformName(): String {
        return "NeoForge"
    }

    override fun isModLoaded(modId: String?): Boolean {
        return ModList.get().isLoaded(modId)
    }

    override fun isDevelopmentEnvironment(): Boolean {
        return !FMLLoader.isProduction()
    }

    override fun sendClientboundPacket(
        player: ServerPlayer,
        payload: CustomPacketPayload
    ) {
        TODO("Not yet implemented")
    }

    override fun sendServerboundPacket(
        payload: CustomPacketPayload
    ) {
        TODO("Not yet implemented")
    }

    override val blockMap: Map<ChiseledBlocks, Block>
        get() {
            return NeoBlocks.map.mapValues { it.value.get() }
        }
}